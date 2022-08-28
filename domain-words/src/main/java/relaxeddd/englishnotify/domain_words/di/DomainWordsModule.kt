package relaxeddd.englishnotify.domain_words.di

import android.content.Context
import androidx.room.Room
import dagger.Module
import dagger.Provides
import relaxeddd.englishnotify.domain_words.db.MIGRATION_14_15
import relaxeddd.englishnotify.domain_words.db.WordsDatabase
import relaxeddd.englishnotify.domain_words.utils.DATABASE_TEST_APP
import javax.inject.Singleton

@Module
object DomainWordsModule {

    @Singleton
    @Provides
    fun provideDatabase(context: Context): WordsDatabase {
        return Room.databaseBuilder(context, WordsDatabase::class.java, DATABASE_TEST_APP)
            .addMigrations(MIGRATION_14_15)
            .build()
    }

    @Provides
    fun provideWordDao(wordsDatabase: WordsDatabase) = wordsDatabase.wordDao()
}
