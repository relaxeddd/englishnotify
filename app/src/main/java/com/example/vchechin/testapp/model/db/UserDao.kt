package com.example.vchechin.testapp.model.db

import androidx.lifecycle.LiveData
import androidx.room.*
import com.example.vchechin.testapp.common.USERS
import com.example.vchechin.testapp.common.User

@Dao
interface UserDao {

    @Query("SELECT * FROM $USERS")
    fun getAll(): LiveData<List<User?>>

    @Query("SELECT * FROM $USERS WHERE id LIKE :id LIMIT 1")
    fun findById(id: String): LiveData<User?>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(vararg users: User)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(user: User)

    @Delete
    fun delete(word: User)
}