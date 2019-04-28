package relaxeddd.englishnotify.model.db

import android.content.Context
import android.database.sqlite.SQLiteException
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase
import relaxeddd.englishnotify.common.Word
import relaxeddd.englishnotify.common.DATABASE_TEST_APP

@Database(entities = [Word::class], version = 8, exportSchema = false)
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
                .addMigrations(MIGRATION_5_6)
                .addMigrations(MIGRATION_6_7)
                .addMigrations(MIGRATION_7_8)
                .fallbackToDestructiveMigration()
                .build()
        }

        val MIGRATION_5_6 = object : Migration(5, 6) {
            override fun migrate(database: SupportSQLiteDatabase) {
                database.execSQL("ALTER TABLE users ADD COLUMN selectedTag TEXT DEFAULT 'irregular' NOT NULL")
            }
        }

        val MIGRATION_6_7 = object : Migration(6, 7) {
            override fun migrate(database: SupportSQLiteDatabase) {
                try {
                    database.execSQL("ALTER TABLE users ADD COLUMN testCount INTEGER DEFAULT 0 NOT NULL")
                } catch (e: SQLiteException) {}
            }
        }

        val MIGRATION_7_8 = object : Migration(7, 8) {
            override fun migrate(database: SupportSQLiteDatabase) {
                try {
                    database.execSQL("DROP TABLE IF EXISTS users")
                } catch (e: SQLiteException) {}
            }
        }
    }
}