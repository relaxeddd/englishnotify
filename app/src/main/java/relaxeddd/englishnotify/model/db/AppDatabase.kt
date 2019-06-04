package relaxeddd.englishnotify.model.db

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import relaxeddd.englishnotify.common.Word
import relaxeddd.englishnotify.common.DATABASE_TEST_APP

@Database(entities = [Word::class], version = 14, exportSchema = false)
@TypeConverters(ConverterListStr::class)
abstract class AppDatabase : RoomDatabase() {

    abstract fun wordDao(): WordDao

    companion object {
        @Volatile private var instance: AppDatabase? = null

        fun getInstance(context: Context): AppDatabase {
            return instance ?: synchronized(this) {
                instance ?: buildDatabase(context).also { instance = it }
            }
        }

        private fun buildDatabase(context: Context): AppDatabase {
            return Room.databaseBuilder(context, AppDatabase::class.java, DATABASE_TEST_APP)
                .fallbackToDestructiveMigration()
                .build()
        }
    }
}