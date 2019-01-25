package com.example.vchechin.testapp.model.db

import androidx.lifecycle.LiveData
import androidx.room.*
import com.example.vchechin.testapp.common.WORDS
import com.example.vchechin.testapp.common.Word

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