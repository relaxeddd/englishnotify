package relaxeddd.englishnotify.model.db

import androidx.lifecycle.LiveData
import androidx.room.*
import relaxeddd.englishnotify.common.USERS
import relaxeddd.englishnotify.common.User

@Dao
interface UserDao {

    @Query("SELECT * FROM $USERS")
    fun getAll(): LiveData<List<User?>>

    @Query("SELECT * FROM $USERS WHERE userId LIKE :userId LIMIT 1")
    fun findById(userId: String): LiveData<User?>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(vararg users: User)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(user: User)

    @Delete
    fun delete(word: User)
}