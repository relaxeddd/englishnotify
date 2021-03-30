package relaxeddd.englishnotify.model.db

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase
import relaxeddd.englishnotify.common.Word
import relaxeddd.englishnotify.common.DATABASE_TEST_APP
import relaxeddd.englishnotify.common.WORDS

@Database(entities = [Word::class], version = 15, exportSchema = false)
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

        fun buildDatabase(context: Context): AppDatabase {
            return Room.databaseBuilder(context, AppDatabase::class.java, DATABASE_TEST_APP)
                .addMigrations(MIGRATION_14_15)
                .build()
        }
    }
}

val MIGRATION_14_15 = object : Migration(14, 15) {
    override fun migrate(database: SupportSQLiteDatabase) {
        database.execSQL("ALTER TABLE $WORDS ADD COLUMN learnStageSecondary INTEGER DEFAULT 0 NOT NULL")
    }
}
