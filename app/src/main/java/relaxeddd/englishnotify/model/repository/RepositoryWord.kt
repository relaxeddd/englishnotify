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

    val words = wordDao.getAll()
    private var tagsInfo: List<TagInfo> = ArrayList()
    val tempParsedWords = ArrayList<Word>()

    init {
        val mainScope = CoroutineScope(Dispatchers.Main)

        mainScope.launch {
            words.observeForever {
                if (it.isEmpty() && !SharedHelper.isDefaultWordsLoaded()) {
                    mainScope.launch {
                        val defaultWords = Func.createDefaultWords()
                        insertWords(defaultWords)
                        SharedHelper.setDefaultWordsLoaded(true)
                    }
                } else {
                    SharedHelper.setDefaultWordsLoaded(true)
                }
            }
        }
    }

    suspend fun clearDictionary() {
        wordDao.deleteAll()
    }

    fun getOwnWords() : List<Word> {
        val allWords = ArrayList(words.value ?: ArrayList())
        val ownWords = ArrayList<Word>()

        for (word in allWords) {
            if (word.isOwnCategory) {
                ownWords.add(word)
            }
        }

        return ownWords
    }

    fun getWordCategoriesForTraining() : HashSet<String> {
        val categories = HashSet<String>()
        val words = ArrayList(words.value ?: ArrayList())

        words.forEach {
            if (!it.isDeleted) {
                it.tags.forEach { tag -> if (tag.isNotEmpty()) categories.add(tag) }
            }
        }
        categories.add(OWN)
        categories.add(ALL_APP_WORDS)
        return categories
    }

    fun isEnoughLearnedWordsToRate() : Boolean {
        val learnStageMax = SharedHelper.getTrueAnswersToLearn()
        val isEnabledSecondaryProgress = SharedHelper.isEnabledSecondaryProgress()
        return ArrayList(words.value?: ArrayList()).filter { it.isLearned(isEnabledSecondaryProgress, learnStageMax) }.size >= 5
    }

    fun getOwnWordCategories() : HashSet<String> {
        val categories = HashSet<String>()
        val words = ArrayList(words.value ?: ArrayList())

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
        val words = ArrayList(words.value ?: ArrayList())

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

    suspend fun setWordLearnStage(word: Word, progress: Int, isSecondary: Boolean) {
        val saveWord = Word(word)
        if (isSecondary) saveWord.learnStageSecondary = progress else saveWord.learnStage = progress
        updateWord(saveWord)
    }

    fun updateTagsInfo(tagsInfo: List<TagInfo>) {
        this.tagsInfo = tagsInfo
    }

    suspend fun insertWord(word : Word, wordDao: WordDao = this.wordDao) {
        wordDao.insertAll(word)
    }

    suspend fun updateWords(words: List<Word>) {
        val existWords = ArrayList(this.words.value ?: ArrayList())
        val idsSet = HashSet<String>()
        var isAllExists = true

        existWords.forEach { idsSet.add(it.id) }
        words.forEach { if (!idsSet.contains(it.id)) isAllExists = false; }

        if (!isAllExists || existWords.size != words.size) wordDao.deleteAll()
        words.forEach {
            wordDao.insertAll(it)
        }
    }

    suspend fun insertWords(words: List<Word>) {
        words.forEach {
            wordDao.insertAll(it)
        }
    }

    suspend fun updateWord(word : Word) {
        wordDao.insert(word)
    }

    suspend fun swapProgress() {
        val updateWords = ArrayList(words.value ?: ArrayList())
        updateWords.forEach {
            val secondaryProgress = it.learnStageSecondary
            it.learnStageSecondary = it.learnStage
            it.learnStage = secondaryProgress
        }
        wordDao.insertAll(*updateWords.toTypedArray())
    }

    //------------------------------------------------------------------------------------------------------------------
    suspend fun insertOwnCategoryWord(wordId: String, eng: String, rus: String, transcription: String, tags: List<String>) {
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
    suspend fun getWord(wordId: String) = wordDao.findWordById(wordId)

    suspend fun removeWordFromDb(wordId: String) {
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
