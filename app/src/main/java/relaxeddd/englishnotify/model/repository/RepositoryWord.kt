package relaxeddd.englishnotify.model.repository

import relaxeddd.englishnotify.App
import relaxeddd.englishnotify.common.OWN
import relaxeddd.englishnotify.common.Word
import relaxeddd.englishnotify.model.db.AppDatabase
import relaxeddd.englishnotify.model.db.WordDao

class RepositoryWord private constructor(private val wordDao: WordDao) {

    companion object {
        @Volatile private var instance: RepositoryWord? = null
        fun getInstance(wordDao: WordDao = AppDatabase.getInstance(App.context).wordDao()) = instance ?: synchronized(this) {
            instance ?: RepositoryWord(wordDao).also { instance = it }
        }
    }

    var words = wordDao.getAll()

    fun updateWord(word : Word) {
        wordDao.insertAll(word)
    }

    fun deleteWord(word : Word) {
        wordDao.delete(word)
    }

    fun updateOwsWords(ownWords: List<Word>) {
        val cacheOwnWords = getOwnWords()

        if (ownWords.size == cacheOwnWords.size) {
            var isNotFound = false

            for (ownWord in ownWords) {
                var isFind = false

                for (cacheOwnWord in cacheOwnWords) {
                    if (ownWord.eng == cacheOwnWord.eng) {
                        isFind = true
                    }
                }
                if (!isFind) {
                    isNotFound = true
                }
            }

            if (!isNotFound) {
                return
            }
        }

        for (word in cacheOwnWords) {
            val tags = ArrayList(word.tags)

            tags.remove(OWN)
            word.saveType = Word.DICTIONARY
            word.tags = tags

            wordDao.insertAll(word)
        }
        ownWords.forEach {
            val tags = ArrayList(it.tags)

            tags.remove(OWN)
            it.tags = tags
            it.saveType = Word.OWN
            it.timestamp = System.currentTimeMillis()

            wordDao.insertAll(it)
        }
    }

    fun isOwnWordsExists() : Boolean {
        val words = this@RepositoryWord.words.value

        if (words != null) {
            for (word in words) {
                if (word.saveType != Word.DICTIONARY) {
                    return true
                }
            }
        }

        return false
    }

    fun getOwnWords() : List<Word> {
        val allWords = words.value
        val ownWords = ArrayList<Word>()

        if (allWords != null) {
            for (word in allWords) {
                if (word.saveType != Word.DICTIONARY) {
                    ownWords.add(word)
                }
            }
        }

        return ownWords
    }
}