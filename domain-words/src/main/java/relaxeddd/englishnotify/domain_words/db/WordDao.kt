package relaxeddd.englishnotify.domain_words.db

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import relaxeddd.englishnotify.domain_words.entity.Word
import relaxeddd.englishnotify.domain_words.utils.WORDS_TABLE

@Dao
internal interface WordDao {

    @Query("SELECT * FROM $WORDS_TABLE")
    fun getAll(): LiveData<List<Word>>

    @Query("SELECT * FROM $WORDS_TABLE")
    fun getAllItemsNow(): List<Word>

    @Query("SELECT * FROM $WORDS_TABLE WHERE id LIKE :id LIMIT 1")
    fun findWordByIdNow(id: String): Word?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertNow(word: Word)

    //------------------------------------------------------------------------------------------------------------------
    @Query("SELECT * FROM $WORDS_TABLE WHERE rus LIKE :rus LIMIT 1")
    suspend fun findByRus(rus: String): Word

    @Query("SELECT * FROM $WORDS_TABLE WHERE eng LIKE :id LIMIT 1")
    suspend fun findById(id: String): Word

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(vararg words: Word)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(word: Word)

    @Delete
    suspend fun delete(word: Word)

    @Query("DELETE FROM $WORDS_TABLE")
    suspend fun deleteAll()

    @Query("DELETE FROM $WORDS_TABLE WHERE id = :id")
    suspend fun deleteById(id: String)

    @Query("SELECT * FROM $WORDS_TABLE WHERE id LIKE :id LIMIT 1")
    suspend fun findWordById(id: String): Word?
}
