package relaxeddd.englishnotify.common

import android.content.Context
import relaxeddd.englishnotify.model.repository.RepositoryWord
import relaxeddd.englishnotify.model.db.AppDatabase
import relaxeddd.englishnotify.model.repository.RepositoryUser

object InjectorUtils {

    fun provideMainViewModelFactory(): MainViewModelFactory {
        val repository = RepositoryFactory.getUserRepository()
        return MainViewModelFactory(repository)
    }

    fun provideDictionaryAllViewModelFactory(context: Context): DictionaryAllViewModelFactory {
        val repositoryWord = RepositoryFactory.getWordRepository(context)
        val repositoryUser = RepositoryFactory.getUserRepository()
        return DictionaryAllViewModelFactory(repositoryWord, repositoryUser)
    }

    fun provideDictionaryOwnViewModelFactory(context: Context): DictionaryOwnViewModelFactory {
        val repositoryWord = RepositoryFactory.getWordRepository(context)
        val repositoryUser = RepositoryFactory.getUserRepository()
        return DictionaryOwnViewModelFactory(repositoryWord, repositoryUser)
    }

    fun provideNotificationsViewModelFactory(): NotificationsViewModelFactory {
        val repository = RepositoryFactory.getUserRepository()
        return NotificationsViewModelFactory(repository)
    }

    fun provideSettingsViewModelFactory(): SettingsViewModelFactory {
        val repository = RepositoryFactory.getUserRepository()
        return SettingsViewModelFactory(repository)
    }

    fun provideWordViewModelFactory(): WordViewModelFactory {
        return WordViewModelFactory()
    }

    fun provideCategoriesViewModelFactory(): CategoriesViewModelFactory {
        val repository = RepositoryFactory.getUserRepository()
        return CategoriesViewModelFactory(repository)
    }
}

object RepositoryFactory {

    internal fun getUserRepository(): RepositoryUser {
        return RepositoryUser.getInstance()
    }

    internal fun getWordRepository(context: Context): RepositoryWord {
        return RepositoryWord.getInstance(AppDatabase.getInstance(context.applicationContext).wordDao())
    }
}