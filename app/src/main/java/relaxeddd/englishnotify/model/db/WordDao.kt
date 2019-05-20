package relaxeddd.englishnotify.model.db

import androidx.lifecycle.LiveData
import androidx.room.*
import relaxeddd.englishnotify.common.WORDS
import relaxeddd.englishnotify.common.Word

@Dao
interface WordDao {

    @Query("SELECT * FROM $WORDS")
    fun getAll(): LiveData<List<Word>>

    @Query("SELECT * FROM $WORDS")
    fun getAllItems(): List<Word>

    @Query("SELECT * FROM $WORDS WHERE rus LIKE :rus LIMIT 1")
    fun findByRus(rus: String): LiveData<Word>

    @Query("SELECT * FROM $WORDS WHERE eng LIKE :eng LIMIT 1")
    fun findById(eng: String): LiveData<Word>

    @Query("SELECT * FROM $WORDS WHERE eng LIKE :eng LIMIT 1")
    fun findWordById(eng: String): Word?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertAll(vararg words: Word)

    @Delete
    fun delete(word: Word)

    @Query("DELETE FROM $WORDS")
    fun deleteAll()

    @Query("DELETE FROM $WORDS WHERE eng = :eng")
    fun deleteByEng(eng: String)
}