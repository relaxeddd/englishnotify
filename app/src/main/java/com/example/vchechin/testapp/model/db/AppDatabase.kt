package com.example.vchechin.testapp.model.db

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import androidx.sqlite.db.SupportSQLiteDatabase
import com.example.vchechin.testapp.common.*
import com.example.vchechin.testapp.common.User
import com.example.vchechin.testapp.common.Word
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

@Database(entities = [User::class, Word::class], version = 3, exportSchema = false)
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
                })
                .build()
        }

        private suspend fun addDefaultContent(db: AppDatabase) {
            val tags: ArrayList<String> = arrayListOf("noun", "adjective", "verb", "top1", "irregular")

            db.wordDao().insertAll(Word("Dog", "Собака", "dog", arrayListOf("noun", "top1"),
                "The dog follows me wherever I go.", "Собака следует за мной, куда бы я ни шёл.", 111),
                Word("Different", "Разные", "ˈdif(ə)rənt", arrayListOf("adjective", "top1"),
                    "These distinctions are important because different rules may apply to different types of securities.",
                    "Эти различия имеют важное значение, потому что к различным видам ценных бумаг могут применяться различные правила.",
                     51257),
                Word("Suspend", "Приостановить", "səˈspend", arrayListOf("verb"),
                    "The Committee should suspend its formal deliberations and complete its unfinished work.",
                    "Комитету следует приостановить свои официальные обсуждения и закончить свою незавершенную работу.", 3))
            db.userDao().insertAll(User(USER_ID_TEST, "vadim25000@yandex.ru", true, 3,
                tags, arrayListOf("top1", "adjective")))
        }
    }
}