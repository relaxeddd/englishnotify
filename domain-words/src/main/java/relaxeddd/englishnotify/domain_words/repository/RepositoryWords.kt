package relaxeddd.englishnotify.domain_words.repository

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import relaxeddd.englishnotify.common.TRAINING_ENG_TO_RUS
import relaxeddd.englishnotify.common.TRAINING_RUS_TO_ENG
import relaxeddd.englishnotify.domain_words.db.WordDao
import relaxeddd.englishnotify.domain_words.entity.Word
import relaxeddd.englishnotify.domain_words.utils.createDefaultWords
import relaxeddd.englishnotify.preferences.Preferences
import relaxeddd.englishnotify.preferences.utils.ALL_APP_WORDS
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class RepositoryWords @Inject constructor(private val wordDao: WordDao, private val prefs: Preferences) {

    val words = wordDao.getAll()
    val tempParsedWords = arrayListOf<Word>()

    init {
        val mainScope = CoroutineScope(Dispatchers.Main)

        mainScope.launch {
            words.observeForever {
                if (it.isEmpty() && !prefs.isDefaultWordsLoaded()) {
                    mainScope.launch {
                        val defaultWords = createDefaultWords()
                        insertWords(defaultWords)
                        prefs.setDefaultWordsLoaded(true)
                    }
                } else {
                    prefs.setDefaultWordsLoaded(true)
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
        categories.add(ALL_APP_WORDS)
        return categories
    }

    fun isEnoughLearnedWordsToRate() : Boolean {
        val learnStageMax = prefs.getTrueAnswersToLearn()
        val isEnabledSecondaryProgress = prefs.isEnabledSecondaryProgress()
        return ArrayList(words.value ?: emptyList()).filter { it.isLearned(isEnabledSecondaryProgress, learnStageMax) }.size >= 5
    }

    fun getOwnWordCategories() : HashSet<String> {
        val categories = HashSet<String>()
        val words = ArrayList(words.value ?: emptyList())

        words.forEach {
            if (!it.isDeleted) {
                it.tags.forEach { tag -> categories.add(tag) }
            }
        }
        return categories
    }

    fun getTrainingWordsByCategory(category: String, isTrainLearned: Boolean = false, trainingLanguage: Int) : ArrayList<Word> {
        val learnStageMax = prefs.getTrueAnswersToLearn()
        val isEnabledSecondaryProgress = prefs.isEnabledSecondaryProgress()
        val trainingWords = ArrayList<Word>()
        val words = ArrayList(words.value ?: ArrayList())

        for (word in words) {
            val isWordAlreadyLearned = word.isLearnedForTraining(isEnabledSecondaryProgress, trainingLanguage, learnStageMax)

            if ((word.tags.contains(category) || category == ALL_APP_WORDS)
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

    /*fun updateTagsInfo(tagsInfo: List<TagInfo>) {
        this.tagsInfo = tagsInfo
    }*/

    /*suspend fun insertWord(word : Word, wordDao: WordDao = this.wordDao) {
        wordDao.insertAll(word)
    }*/

    private suspend fun updateWords(words: List<Word>) {
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

    // TODO: switch required coroutine context here
    fun insertNow(word: Word) {
        wordDao.insertNow(word)
    }

    suspend fun findWord(id : String) = wordDao.findWordById(id)

    fun findWordNow(id : String) = wordDao.findWordByIdNow(id)

    fun getWordsNow() = wordDao.getAllItemsNow()

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
    suspend fun getWord(wordId: String) = wordDao.findWordById(wordId)

    suspend fun removeWordFromDb(wordId: String) {
        wordDao.deleteById(wordId)
    }

    suspend fun deleteWord(wordId: String) : Boolean {
        return deleteWords(listOf(wordId))
    }

    suspend fun deleteWords(wordIds: List<String>) : Boolean {
        for (wordId in wordIds) {
            val word = wordDao.findWordById(wordId)

            if (word != null) {
                val saveWord = Word(word)
                saveWord.isDeleted = true
                updateWord(saveWord)
            }
        }

        return true
    }
}
