package relaxeddd.englishnotify.domain_words.db

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase
import relaxeddd.englishnotify.domain_words.entity.Word
import relaxeddd.englishnotify.domain_words.utils.DATABASE_TEST_APP
import relaxeddd.englishnotify.domain_words.utils.WORDS_TABLE

@Database(entities = [Word::class], version = 15, exportSchema = false)
@TypeConverters(ConverterListStr::class)
internal abstract class WordsDatabase : RoomDatabase() {

    companion object {

        @Volatile private var instance: WordsDatabase? = null

        fun getInstance(context: Context): WordsDatabase {
            return instance ?: synchronized(this) {
                instance ?: buildDatabase(context).also { instance = it }
            }
        }

        private fun buildDatabase(context: Context): WordsDatabase {
            return Room.databaseBuilder(context, WordsDatabase::class.java, DATABASE_TEST_APP)
                .addMigrations(MIGRATION_14_15)
                .build()
        }
    }

    abstract fun wordDao(): WordDao
}

val MIGRATION_14_15 = object : Migration(14, 15) {
    override fun migrate(database: SupportSQLiteDatabase) {
        database.execSQL("ALTER TABLE $WORDS_TABLE ADD COLUMN learnStageSecondary INTEGER DEFAULT 0 NOT NULL")
    }
}
