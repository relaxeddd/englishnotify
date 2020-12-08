package relaxeddd.englishnotify.model.repository

import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import relaxeddd.englishnotify.App
import relaxeddd.englishnotify.common.*
import relaxeddd.englishnotify.model.db.AppDatabase
import relaxeddd.englishnotify.model.db.WordDao
import relaxeddd.englishnotify.model.http.ApiHelper
import relaxeddd.englishnotify.model.preferences.SharedHelper
import java.util.*
import kotlin.collections.ArrayList
import kotlin.collections.HashSet

class RepositoryWord private constructor(private val wordDao: WordDao) {

    companion object {
        @Volatile private var instance: RepositoryWord? = null
        fun getInstance(wordDao: WordDao = AppDatabase.getInstance(App.context).wordDao()) = instance ?: synchronized(this) {
            instance ?: RepositoryWord(wordDao).also { instance = it }
        }
    }

    private val ioScope = CoroutineScope(Dispatchers.IO)
    private val uiScope = CoroutineScope(Dispatchers.Main)

    val words = wordDao.getAll().also {
        uiScope.launch {
            it.observeForever {
                if (it.isEmpty() && !SharedHelper.isDefaultWordsLoaded()) {
                    ioScope.launch {
                        val defaultWords = listOf(
                            Word("cause", "cause", "причина, дело, повод, вызывать", "kɔːz", listOf("frequent_verbs"),
                                timestamp = System.currentTimeMillis(), isCreatedByUser = false),
                            Word("catch", "catch", "ловить, поймать, улов, выгода, добыча, захват", "kæʧ",
                                listOf("irregular", "movement", "frequent_verbs"), v2 = "caught", v3 = "caught", timestamp = System.currentTimeMillis(), isCreatedByUser = false),
                            Word("dream", "dream", "мечтать, сниться, мечта, сон, фантазировать", "driːm",
                                listOf("irregular"), v2 = "dreamt", v3 = "dreamt", timestamp = System.currentTimeMillis(), isCreatedByUser = false),
                            Word("throw", "throw", "бросать, бросок, кидать, метать, метание", "θroʊ",
                                listOf("irregular", "movement", "frequent_verbs", "sport"), v2 = "threw", v3 = "thrown",
                                timestamp = System.currentTimeMillis(), isCreatedByUser = false),
                            Word("forget", "forget", "забывать, не помнить, забыть", "fəˈɡet",
                                listOf("irregular", "frequent_verbs"), v2 = "forgot", v3 = "forgotten",
                                timestamp = System.currentTimeMillis(), isCreatedByUser = false),
                            Word("bite", "bite", "кусать, укусить, укус, кусок, кусаться", "baɪt",
                                listOf("irregular", "frequent_verbs"), v2 = "bit", v3 = "bitten",
                                timestamp = System.currentTimeMillis(), isCreatedByUser = false),
                            Word("hide", "hide", "скрывать, прятать, прятаться", "haɪd",
                                listOf("irregular", "frequent_verbs"), v2 = "hid", v3 = "hidden",
                                timestamp = System.currentTimeMillis(), isCreatedByUser = false),
                            Word("absent", "absent", "отсутствовать, отсутствующий, отсутствует, в отсутствие", "ˈæbsənt",
                                listOf("frequent_verbs"), timestamp = System.currentTimeMillis(), isCreatedByUser = false),
                            Word("beard", "beard", "борода", "bɪrd",
                                listOf("human_body"), timestamp = System.currentTimeMillis(), isCreatedByUser = false),
                            Word("claim", "claim", "запрос, требование, требовать, иск, заявка, претензия", "kleɪm",
                                listOf("tourists"), timestamp = System.currentTimeMillis(), isCreatedByUser = false),
                            Word("desire", "desire", "желание, желать, страсть", "dɪˈzaɪər",
                                listOf(), timestamp = System.currentTimeMillis(), isCreatedByUser = false),
                            Word("elk", "elk", "лось, сохатый", "elk",
                                listOf("animals"), timestamp = System.currentTimeMillis(), isCreatedByUser = false)
                        )
                        insertWords(defaultWords)
                        SharedHelper.setDefaultWordsLoaded(true)
                    }
                } else {
                    SharedHelper.setDefaultWordsLoaded(true)
                }
            }
        }
    }
    private var tagsInfo: List<TagInfo> = ArrayList()
    val tempParsedWords = ArrayList<Word>()

    fun clearDictionary() {
        wordDao.deleteAll()
    }

    fun getOwnWords() : List<Word> {
        val allWords = words.value
        val ownWords = ArrayList<Word>()

        if (allWords != null) {
            for (word in allWords) {
                if (word.isOwnCategory) {
                    ownWords.add(word)
                }
            }
        }

        return ownWords
    }

    fun getWordCategoriesForTraining() : HashSet<String> {
        val categories = HashSet<String>()
        val words = words.value ?: ArrayList()

        words.forEach {
            if (!it.isDeleted) {
                it.tags.forEach { tag -> if (tag.isNotEmpty()) categories.add(tag) }
            }
        }
        categories.add(OWN)
        categories.add(ALL_APP_WORDS)
        return categories
    }

    fun getOwnWordCategories() : HashSet<String> {
        val categories = HashSet<String>()
        val words = words.value ?: ArrayList()

        words.forEach {
            if (!it.isDeleted) {
                it.tags.forEach { tag -> if (tag != OWN && isOwnCategory(tag)) categories.add(tag) }
            }
        }
        return categories
    }

    fun getTrainingWordsByCategory(category: String, isTrainLearned: Boolean = false, trainingLanguage: Int) : ArrayList<Word> {
        val learnStageMax = SharedHelper.getTrueAnswersToLearn()
        val isEnabledSecondaryProgress = SharedHelper.isEnabledSecondaryProgress()
        val trainingWords = ArrayList<Word>()
        val words = words.value ?: ArrayList()

        for (word in words) {
            val isWordAlreadyLearned = word.isLearnedForTraining(isEnabledSecondaryProgress, trainingLanguage, learnStageMax)

            if ((word.tags.contains(category) || category == ALL_APP_WORDS || (category == OWN && word.isOwnCategory))
                    && (!isWordAlreadyLearned && !isTrainLearned || isTrainLearned && isWordAlreadyLearned) && !word.isDeleted) {
                trainingWords.add(word)
            }
        }

        return if (isTrainLearned) {
            trainingWords.sortBy {
                val wordSummaryProgress = when (trainingLanguage) {
                    TRAINING_ENG_TO_RUS -> {
                        it.learnStage
                    }
                    TRAINING_RUS_TO_ENG -> {
                        if (isEnabledSecondaryProgress) it.learnStageSecondary else it.learnStage
                    }
                    else /*MIXED*/ -> {
                        if (isEnabledSecondaryProgress) it.learnStage + it.learnStageSecondary else it.learnStage
                    }
                }
                wordSummaryProgress
            }
            if (trainingWords.size >= 10) {
                ArrayList(trainingWords.take(10).shuffled())
            } else {
                ArrayList(trainingWords.shuffled())
            }
        } else if (trainingWords.size >= 10) {
            ArrayList(trainingWords.shuffled().subList(0, 10))
        } else {
            ArrayList(trainingWords.shuffled())
        }
    }

    fun setWordLearnStage(word: Word, progress: Int, isSecondary: Boolean, isRemoteSave: Boolean = true) {
        if (isSecondary) word.learnStageSecondary = progress else word.learnStage = progress
        ioScope.launch {
            val saveWord = Word(word)

            if (isSecondary) saveWord.learnStageSecondary = progress else saveWord.learnStage = progress
            updateWord(saveWord)
        }
    }

    fun updateTagsInfo(tagsInfo: List<TagInfo>) {
        this.tagsInfo = tagsInfo
    }

    fun insertWord(word : Word, wordDao: WordDao = this.wordDao) {
        wordDao.insertAll(word)
    }

    fun updateWords(words: List<Word>) {
        val existWords = this.words.value
        val idsSet = HashSet<String>()
        var isAllExists = true

        existWords?.forEach { idsSet.add(it.id) }
        words.forEach { if (!idsSet.contains(it.id)) isAllExists = false; }

        if (existWords == null || !isAllExists || existWords.size != words.size) wordDao.deleteAll()
        words.forEach {
            wordDao.insertAll(it)
        }
    }

    fun insertWords(words: List<Word>) {
        words.forEach {
            wordDao.insertAll(it)
        }
    }

    fun updateWord(word : Word) {
        wordDao.insert(word)
    }

    fun swapProgress(onCompleted: () -> Unit) {
        ioScope.launch {
            val updateWords = words.value ?: ArrayList()
            updateWords.forEach {
                val secondaryProgress = it.learnStageSecondary
                it.learnStageSecondary = it.learnStage
                it.learnStage = secondaryProgress
            }
            wordDao.insertAll(*updateWords.toTypedArray())
            uiScope.launch {
                onCompleted()
            }
        }
    }

    //------------------------------------------------------------------------------------------------------------------
    fun insertOwnCategoryWord(wordId: String, eng: String, rus: String, transcription: String, tags: List<String>) {
        val word = Word(wordId, eng, rus, transcription, tags,
            timestamp = System.currentTimeMillis(), isCreatedByUser = true, isOwnCategory = true)
        updateWord(word)
    }

    //------------------------------------------------------------------------------------------------------------------
    suspend fun addToOwn(wordId: String) : Boolean {
        return addToOwn(Collections.singletonList(wordId))
    }

    suspend fun removeFromOwn(wordId: String) : Boolean {
        return removeFromOwn(Collections.singletonList(wordId))
    }

    private suspend fun addToOwn(wordIds: List<String>) : Boolean {
        return setIsOwnCategory(wordIds, true)
    }

    private suspend fun removeFromOwn(wordIds: List<String>) : Boolean {
        return setIsOwnCategory(wordIds, false)
    }

    private suspend fun setIsOwnCategory(wordIds: List<String>, isOwnCategory: Boolean) : Boolean {
        for (wordId in wordIds) {
            val word = wordDao.findWordById(wordId)

            if (word != null) {
                val saveWord = Word(word)
                saveWord.isOwnCategory = isOwnCategory
                getInstance(wordDao).updateWord(saveWord)
            }
        }

        return true
    }

    //------------------------------------------------------------------------------------------------------------------
    fun getWord(wordId: String) = wordDao.findWordById(wordId)

    fun removeWordFromDb(wordId: String) {
        wordDao.deleteById(wordId)
    }

    suspend fun deleteWord(wordId: String) : Boolean {
        return deleteWords(Collections.singletonList(wordId))
    }

    suspend fun deleteWords(wordIds: List<String>) : Boolean {
        for (wordId in wordIds) {
            val word = wordDao.findWordById(wordId)

            if (word != null) {
                val saveWord = Word(word)
                saveWord.isDeleted = true
                getInstance(wordDao).updateWord(saveWord)
            }
        }

        return true
    }

    suspend fun requestSaveAllWords() : Boolean {
        if (FirebaseAuth.getInstance().currentUser == null) {
            showToast(getErrorString(RESULT_ERROR_UNAUTHORIZED))
            return false
        }

        val words = ArrayList(words.value?: ArrayList()).filter { !it.isDeleted }

        if (words.isEmpty()) {
            showToast(getErrorString(RESULT_ERROR_SAVE_WORDS_EMPTY))
            return false
        }

        if (words.size > WORDS_SAVE_LIMIT) {
            showToast(getErrorString(RESULT_ERROR_SAVE_WORDS_TOO_MANY))
            return false
        }

        val firebaseUser = RepositoryCommon.getInstance().firebaseUser
        val tokenId = RepositoryCommon.getInstance().tokenId
        val answer = ApiHelper.requestSaveWords(firebaseUser, tokenId, words)
        val isSuccess = answer?.isSuccess() == true

        if (isSuccess) {
            RepositoryUser.getInstance().setSavedWordsCount(words.size)
        } else {
            showToast(getErrorString(answer?.code ?: RESULT_ERROR_SAVE_WORDS))
        }

        return isSuccess
    }
    
    suspend fun requestLoadAllWords() : Boolean {
        if (FirebaseAuth.getInstance().currentUser == null) {
            showToast(getErrorString(RESULT_ERROR_UNAUTHORIZED))
            return false
        }

        val firebaseUser = RepositoryCommon.getInstance().firebaseUser
        val tokenId = RepositoryCommon.getInstance().tokenId
        val answer = ApiHelper.requestLoadWords(firebaseUser, tokenId)

        if (answer?.result?.isSuccess() == true) {
            wordDao.deleteAll()
            updateWords(answer.words)
            return true
        } else {
            showToast(getErrorString(answer?.result?.code ?: RESULT_ERROR_LOAD_WORDS))
            return false
        }
    }
}
