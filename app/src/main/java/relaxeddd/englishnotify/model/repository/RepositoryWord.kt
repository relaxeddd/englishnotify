package relaxeddd.englishnotify.model.repository

import relaxeddd.englishnotify.common.Word
import relaxeddd.englishnotify.model.db.WordDao

class RepositoryWord private constructor(private val wordDao: WordDao) {

    companion object {
        @Volatile private var instance: RepositoryWord? = null
        fun getInstance(wordDao: WordDao) = instance
            ?: synchronized(this) {
            instance
                ?: RepositoryWord(wordDao).also { instance = it }
        }
    }

    var words = wordDao.getAll()

    fun updateWord(word : Word) {
        wordDao.insertAll(word)
    }
}