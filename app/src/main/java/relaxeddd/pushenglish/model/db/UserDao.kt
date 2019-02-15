package relaxeddd.pushenglish.model.db

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.room.*
import relaxeddd.pushenglish.common.USERS
import relaxeddd.pushenglish.common.User

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