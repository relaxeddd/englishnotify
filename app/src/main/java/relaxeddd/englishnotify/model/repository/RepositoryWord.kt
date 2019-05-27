package relaxeddd.englishnotify.model.repository

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import relaxeddd.englishnotify.App
import relaxeddd.englishnotify.common.*
import relaxeddd.englishnotify.model.db.AppDatabase
import relaxeddd.englishnotify.model.db.WordDao

class RepositoryWord private constructor(private val wordDao: WordDao) {

    companion object {
        @Volatile private var instance: RepositoryWord? = null
        fun getInstance(wordDao: WordDao = AppDatabase.getInstance(App.context).wordDao()) = instance ?: synchronized(this) {
            instance ?: RepositoryWord(wordDao).also { instance = it }
        }
    }

    private val ioScope = CoroutineScope(Dispatchers.IO)
    var words = wordDao.getAll()

    fun updateWord(word : Word) {
        wordDao.insertAll(word)
    }

    fun deleteWord(word : Word) {
        word.isDeleted = true
        updateWord(word)
    }

    fun setWordProgress(word: Word, progress: Int) {
        ioScope.launch {
            val saveWord = Word(word)
            saveWord.learnStage = progress
            updateWord(saveWord)
        }
    }

    fun updateOwsWords(ownWords: List<Word>) {
        ownWords.forEach {
            if (it.timestamp == 0L) {
                it.timestamp = System.currentTimeMillis()
            }
            if (it.tags.contains(OWN)) {
                val tags = ArrayList(it.tags)
                tags.remove(OWN)
                it.tags = tags
                it.isOwnCategory = true
                it.isCreatedByUser = true
            }

            wordDao.insertAll(it)
        }
    }

    fun isOwnWordsExists() : Boolean {
        val words = this@RepositoryWord.words.value

        if (words != null) {
            for (word in words) {
                if (word.isOwnCategory) {
                    return true
                }
            }
        }

        return false
    }

    fun isTrainingWordsExists() : Boolean {
        val words = this@RepositoryWord.words.value ?: ArrayList()

        for (word in words) {
            if (word.eng.split(" ").size <= 3 && word.learnStage != LEARN_STAGE_MAX) {
                return true
            }
        }

        return false
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

    fun getWordCategories() : HashSet<String> {
        val categories = HashSet<String>()
        words.value?.forEach { it.tags.forEach { tag -> if (tag.isNotEmpty()) categories.add(tag) } }
        categories.add(ALL_APP_WORDS)
        return categories
    }

    fun getTrainingWordsByCategory(category: String) : ArrayList<Word> {
        val trainingWords = ArrayList<Word>()
        val words = this@RepositoryWord.words.value ?: ArrayList()

        for (word in words) {
            if ((word.tags.contains(category) || category == ALL_APP_WORDS) && word.learnStage != LEARN_STAGE_MAX
                && (word.eng.split(" ").size <= 3 || word.type == EXERCISE)) {
                trainingWords.add(word)
            }
        }

        return if (trainingWords.size >= 10) ArrayList(trainingWords.shuffled().subList(0, 10)) else ArrayList(trainingWords.shuffled())
    }

    fun clearDictionary() {
        wordDao.deleteAll()
    }
}