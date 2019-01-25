package com.example.vchechin.testapp.model.db

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.sqlite.db.SupportSQLiteDatabase
import com.example.vchechin.testapp.common.*
import com.example.vchechin.testapp.common.User
import com.example.vchechin.testapp.common.Word
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

@Database(entities = arrayOf(User::class, Word::class), version = 2, exportSchema = false)
abstract class AppDatabase : RoomDatabase() {

    abstract fun userDao(): UserDao
    abstract fun wordDao(): WordDao

    companion object {
        @Volatile private var instance: AppDatabase? = null
        private val uiScope = CoroutineScope(Dispatchers.Main)

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

                        uiScope.launch {
                            addDefaultWords(getInstance(context))
                        }
                    }
                })
                .build()
        }

        private suspend fun addDefaultWords(db: AppDatabase) {
            withContext(Dispatchers.IO) {
                db.wordDao().insertAll(Word("Dog", "Собака", "dog"),
                    Word("Different", "Разные", "ˈdif(ə)rənt"),
                    Word("Suspend", "Приостановить", "səˈspend"))
                db.userDao().insertAll(User(USER_ID_TEST, "vadim25000@yandex.ru"))
            }
        }
    }
}