package relaxeddd.englishnotify.common

import android.content.Context
import relaxeddd.englishnotify.factories.DictionaryViewModelFactory
import relaxeddd.englishnotify.factories.MainViewModelFactory
import relaxeddd.englishnotify.factories.NotificationsViewModelFactory
import relaxeddd.englishnotify.factories.SettingsViewModelFactory
import relaxeddd.englishnotify.model.repository.RepositoryWord
import relaxeddd.englishnotify.model.db.AppDatabase
import relaxeddd.englishnotify.model.repository.RepositoryUser

object InjectorUtils {

    fun provideMainViewModelFactory(context: Context): MainViewModelFactory {
        val repository = RepositoryFactory.getUserRepository(context)
        return MainViewModelFactory(repository)
    }

    fun provideDictionaryViewModelFactory(context: Context): DictionaryViewModelFactory {
        val repositoryWord = RepositoryFactory.getWordRepository(context)
        val repositoryUser = RepositoryFactory.getUserRepository(context)
        return DictionaryViewModelFactory(repositoryWord, repositoryUser)
    }

    fun provideNotificationsViewModelFactory(context: Context): NotificationsViewModelFactory {
        val repository = RepositoryFactory.getUserRepository(context)
        return NotificationsViewModelFactory(repository)
    }

    fun provideSettingsViewModelFactory(context: Context): SettingsViewModelFactory {
        val repository = RepositoryFactory.getUserRepository(context)
        return SettingsViewModelFactory(repository)
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