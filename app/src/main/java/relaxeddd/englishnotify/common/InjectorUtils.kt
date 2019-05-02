package relaxeddd.englishnotify.common

import android.content.Context
import relaxeddd.englishnotify.factories.*
import relaxeddd.englishnotify.model.repository.RepositoryWord
import relaxeddd.englishnotify.model.db.AppDatabase
import relaxeddd.englishnotify.model.repository.RepositoryUser

object InjectorUtils {

    fun provideMainViewModelFactory(context: Context): MainViewModelFactory {
        val repository = RepositoryFactory.getUserRepository(context)
        return MainViewModelFactory(repository)
    }

    fun provideDictionaryAllViewModelFactory(context: Context): DictionaryAllViewModelFactory {
        val repositoryWord = RepositoryFactory.getWordRepository(context)
        val repositoryUser = RepositoryFactory.getUserRepository(context)
        return DictionaryAllViewModelFactory(repositoryWord, repositoryUser)
    }

    fun provideDictionaryOwnViewModelFactory(context: Context): DictionaryOwnViewModelFactory {
        val repositoryWord = RepositoryFactory.getWordRepository(context)
        val repositoryUser = RepositoryFactory.getUserRepository(context)
        return DictionaryOwnViewModelFactory(repositoryWord, repositoryUser)
    }

    fun provideNotificationsViewModelFactory(context: Context): NotificationsViewModelFactory {
        val repository = RepositoryFactory.getUserRepository(context)
        return NotificationsViewModelFactory(repository)
    }

    fun provideSettingsViewModelFactory(context: Context): SettingsViewModelFactory {
        val repository = RepositoryFactory.getUserRepository(context)
        return SettingsViewModelFactory(repository)
    }

    fun provideWordViewModelFactory(context: Context): WordViewModelFactory {
        return WordViewModelFactory()
    }
}

object RepositoryFactory {

    internal fun getUserRepository(context: Context): RepositoryUser {
        return RepositoryUser.getInstance()
    }

    internal fun getWordRepository(context: Context): RepositoryWord {
        return RepositoryWord.getInstance(AppDatabase.getInstance(context.applicationContext).wordDao())
    }
}