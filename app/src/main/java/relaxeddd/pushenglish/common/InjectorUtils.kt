package relaxeddd.pushenglish.common

import android.content.Context
import relaxeddd.pushenglish.factories.DictionaryViewModelFactory
import relaxeddd.pushenglish.factories.MainViewModelFactory
import relaxeddd.pushenglish.factories.NotificationsViewModelFactory
import relaxeddd.pushenglish.factories.SettingsViewModelFactory
import relaxeddd.pushenglish.model.repository.RepositoryWord
import relaxeddd.pushenglish.model.db.AppDatabase
import relaxeddd.pushenglish.model.repository.RepositoryUser

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
        return RepositoryUser.getInstance(AppDatabase.getInstance(context.applicationContext).userDao())
    }

    internal fun getWordRepository(context: Context): RepositoryWord {
        return RepositoryWord.getInstance(AppDatabase.getInstance(context.applicationContext).wordDao())
    }
}