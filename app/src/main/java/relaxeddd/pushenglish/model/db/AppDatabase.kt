package relaxeddd.pushenglish.model.db

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import androidx.sqlite.db.SupportSQLiteDatabase
import relaxeddd.pushenglish.common.User
import relaxeddd.pushenglish.common.Word
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import relaxeddd.pushenglish.common.DATABASE_TEST_APP
import relaxeddd.pushenglish.common.USER_ID_TEST

@Database(entities = [User::class, Word::class], version = 4, exportSchema = false)
@TypeConverters(ConverterListStr::class)
abstract class AppDatabase : RoomDatabase() {

    abstract fun userDao(): UserDao
    abstract fun wordDao(): WordDao

    companion object {
        @Volatile private var instance: AppDatabase? = null
        private val ioScope = CoroutineScope(Dispatchers.IO)

        fun getInstance(context: Context): AppDatabase {
            return instance ?: synchronized(this) {
                instance ?: buildDatabase(context).also { instance = it }
            }
        }

        private fun buildDatabase(context: Context): AppDatabase {
            return Room.databaseBuilder(context, AppDatabase::class.java, DATABASE_TEST_APP)
                .fallbackToDestructiveMigration()
                .addCallback(object : RoomDatabase.Callback() {
                    override fun onCreate(db: SupportSQLiteDatabase) {
                        super.onCreate(db)

                        ioScope.launch {
                            addDefaultContent(getInstance(context))
                        }
                    }
                }).build()
        }

        private suspend fun addDefaultContent(db: AppDatabase) {
            //val tags: ArrayList<String> = arrayListOf("noun", "adjective", "verb", "top1", "irregular")

            db.wordDao().insertAll(
                Word("dog", "собака", "dog", arrayListOf("animals"), timestamp = System.currentTimeMillis()
                ),
                Word("different", "разный", "ˈdif(ə)rənt", arrayListOf("frequent"),
                    timestamp = System.currentTimeMillis() + 1
                ),
                Word("suspend", "Приостановить", "səˈspend", arrayListOf("work"),
                    timestamp = System.currentTimeMillis() + 2
                )
            )
            /*db.userDao().insert(User(USER_ID_TEST, "vadim25000@yandex.ru", true, 3,
                tags, arrayListOf("top1", "adjective"))
            )*/
        }
    }
}