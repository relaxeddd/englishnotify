package relaxeddd.englishnotify.model.repository

import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import relaxeddd.englishnotify.App
import relaxeddd.englishnotify.common.*
import relaxeddd.englishnotify.model.db.AppDatabase
import relaxeddd.englishnotify.model.db.WordDao
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
            it.observeForever { print("Observing words to init") }
        }
    }
    private var tagsInfo: List<TagInfo> = ArrayList()

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

    fun getTrainingWordsByCategory(category: String, isLearned: Boolean = false, trainingLanguage: Int) : ArrayList<Word> {
        val isEnabledSecondaryProgress = SharedHelper.isEnabledSecondaryProgress()
        val trainingWords = ArrayList<Word>()
        val words = words.value ?: ArrayList()

        for (word in words) {
            val isWordAlreadyLearned = word.isLearnedForTraining(isEnabledSecondaryProgress, trainingLanguage)

            if ((word.tags.contains(category) || category == ALL_APP_WORDS || (category == OWN && word.isOwnCategory))
                    && (!isWordAlreadyLearned && !isLearned || isLearned && isWordAlreadyLearned) && !word.isDeleted) {
                trainingWords.add(word)
            }
        }

        return if (isLearned) {
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

    /*fun calculateTagsInfo() : List<TagInfo> {
        val tagsInfo = ArrayList(this.tagsInfo)
        val tagsInfoMap: HashMap<String, TagInfo> = HashMap()
        val words = words.value ?: ArrayList()

        for (tagInfo in tagsInfo) {
            tagInfo.learned = 0
            tagInfo.received = 0
            tagsInfoMap[tagInfo.key] = tagInfo
        }

        val tagInfoOwn = tagsInfoMap[OWN] ?: TagInfo(OWN)

        tagInfoOwn.received = 0
        tagInfoOwn.learned = 0

        if (!tagsInfo.contains(tagInfoOwn)) tagsInfo.add(tagInfoOwn)

        for (word in words) {
            if (word.isCreatedByUser && !word.isDeleted) {
                tagInfoOwn.received++
                tagInfoOwn.total++
                if (word.learnStage == LEARN_STAGE_MAX) tagInfoOwn.learned++
            }
        }

        return Collections.singletonList(tagInfoOwn)
    }*/

    fun getOwnWordsTagInfo() : TagInfo {
        val isEnabledSecondaryProgress = SharedHelper.isEnabledSecondaryProgress()
        val tagInfoOwn = TagInfo(OWN)
        val words = words.value ?: ArrayList()

        tagInfoOwn.received = 0
        tagInfoOwn.learned = 0
        for (word in words) {
            if (word.isCreatedByUser && !word.isDeleted) {
                tagInfoOwn.received++
                tagInfoOwn.total++
                if (word.isLearned(isEnabledSecondaryProgress)) tagInfoOwn.learned++
            }
        }

        return tagInfoOwn
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
    fun insertOwnCategoryWord(wordId: String, eng: String, rus: String, transcription: String) {
        val word = Word(wordId, eng, rus, transcription, timestamp = System.currentTimeMillis(), isCreatedByUser = true, isOwnCategory = true)
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
        if (FirebaseAuth.getInstance().currentUser == null) {
            withContext(Dispatchers.Main) { showToast(getErrorString(RESULT_ERROR_UNAUTHORIZED)) }
            return false
        }

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
        if (FirebaseAuth.getInstance().currentUser == null) {
            withContext(Dispatchers.Main) { showToast(getErrorString(RESULT_ERROR_UNAUTHORIZED)) }
            return false
        }

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
}
