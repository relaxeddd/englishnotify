package relaxeddd.englishnotify.domain_words.db

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase
import relaxeddd.englishnotify.domain_words.entity.Word
import relaxeddd.englishnotify.domain_words.utils.WORDS_TABLE

@Database(entities = [Word::class], version = 15, exportSchema = false)
@TypeConverters(ConverterListStr::class)
abstract class WordsDatabase : RoomDatabase() {

    abstract fun wordDao(): WordDao
}

val MIGRATION_14_15 = object : Migration(14, 15) {
    override fun migrate(database: SupportSQLiteDatabase) {
        database.execSQL("ALTER TABLE $WORDS_TABLE ADD COLUMN learnStageSecondary INTEGER DEFAULT 0 NOT NULL")
    }
}
