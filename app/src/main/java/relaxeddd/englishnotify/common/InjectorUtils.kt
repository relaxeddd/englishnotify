package relaxeddd.englishnotify.common

import android.content.Context
import relaxeddd.englishnotify.model.repository.RepositoryWord
import relaxeddd.englishnotify.model.db.AppDatabase
import relaxeddd.englishnotify.model.repository.RepositoryUser
import relaxeddd.englishnotify.ui.categories.CategorySection

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

    fun provideDictionaryExercisesViewModelFactory(context: Context): DictionaryExercisesViewModelFactory {
        val repositoryWord = RepositoryFactory.getWordRepository(context)
        val repositoryUser = RepositoryFactory.getUserRepository()
        return DictionaryExercisesViewModelFactory(repositoryWord, repositoryUser)
    }

    fun provideDictionaryKnowViewModelFactory(context: Context): DictionaryKnowViewModelFactory {
        val repositoryWord = RepositoryFactory.getWordRepository(context)
        val repositoryUser = RepositoryFactory.getUserRepository()
        return DictionaryKnowViewModelFactory(repositoryWord, repositoryUser)
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

    fun provideCategorySectionViewModelFactory(type: CategorySection): CategorySectionViewModelFactory {
        val repository = RepositoryFactory.getUserRepository()
        return CategorySectionViewModelFactory(type, repository)
    }

    fun provideTrainingSettingViewModelFactory(): TrainingSettingViewModelFactory {
        return TrainingSettingViewModelFactory()
    }

    fun provideTrainingViewModelFactory(context: Context): TrainingViewModelFactory {
        val repositoryWord = RepositoryFactory.getWordRepository(context)
        return TrainingViewModelFactory(repositoryWord)
    }

    fun provideStatisticViewModelFactory(context: Context): StatisticViewModelFactory {
        val repositoryWord = RepositoryFactory.getWordRepository(context)
        return StatisticViewModelFactory(repositoryWord)
    }

    fun provideTimeViewModelFactory(): TimeViewModelFactory {
        val repository = RepositoryFactory.getUserRepository()
        return TimeViewModelFactory(repository)
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