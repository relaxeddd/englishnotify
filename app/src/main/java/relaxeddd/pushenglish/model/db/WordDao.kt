package relaxeddd.pushenglish.model.db

import androidx.lifecycle.LiveData
import androidx.room.*
import relaxeddd.pushenglish.common.WORDS
import relaxeddd.pushenglish.common.Word

@Dao
interface WordDao {

    @Query("SELECT * FROM $WORDS")
    fun getAll(): LiveData<List<Word>>

    @Query("SELECT * FROM $WORDS WHERE rus LIKE :rus LIMIT 1")
    fun findByRus(rus: String): LiveData<Word>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertAll(vararg words: Word)

    @Delete
    fun delete(word: Word)
}