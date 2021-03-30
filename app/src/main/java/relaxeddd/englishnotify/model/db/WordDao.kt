@file:Suppress("unused")
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
    fun getAllItemsNow(): List<Word>

    @Query("SELECT * FROM $WORDS WHERE id LIKE :id LIMIT 1")
    fun findWordByIdNow(id: String): Word?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertNow(word: Word)

    //------------------------------------------------------------------------------------------------------------------
    @Query("SELECT * FROM $WORDS WHERE rus LIKE :rus LIMIT 1")
    suspend fun findByRus(rus: String): Word

    @Query("SELECT * FROM $WORDS WHERE eng LIKE :id LIMIT 1")
    suspend fun findById(id: String): Word

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(vararg words: Word)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(word: Word)

    @Delete
    suspend fun delete(word: Word)

    @Query("DELETE FROM $WORDS")
    suspend fun deleteAll()

    @Query("DELETE FROM $WORDS WHERE id = :id")
    suspend fun deleteById(id: String)

    @Query("SELECT * FROM $WORDS WHERE id LIKE :id LIMIT 1")
    suspend fun findWordById(id: String): Word?
}
