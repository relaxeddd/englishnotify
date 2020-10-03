package relaxeddd.englishnotify.common

import android.content.Context
import relaxeddd.englishnotify.model.repository.RepositoryWord
import relaxeddd.englishnotify.model.db.AppDatabase
import relaxeddd.englishnotify.model.repository.RepositoryUser
import relaxeddd.englishnotify.ui.categories.CategorySection

object InjectorUtils {

    fun provideMainViewModelFactory() = MainViewModelFactory(RepositoryFactory.getUserRepository())
    fun provideDictionaryAllViewModelFactory(context: Context) = DictionaryAllViewModelFactory(RepositoryFactory.getWordRepository(context), RepositoryFactory.getUserRepository())
    fun provideDictionaryOwnViewModelFactory(context: Context) = DictionaryOwnViewModelFactory(RepositoryFactory.getWordRepository(context), RepositoryFactory.getUserRepository())
    fun provideDictionaryExercisesViewModelFactory(context: Context) = DictionaryExercisesViewModelFactory(RepositoryFactory.getWordRepository(context), RepositoryFactory.getUserRepository())
    fun provideDictionaryKnowViewModelFactory(context: Context) = DictionaryKnowViewModelFactory(RepositoryFactory.getWordRepository(context), RepositoryFactory.getUserRepository())
    fun provideNotificationsViewModelFactory() = NotificationsViewModelFactory(RepositoryFactory.getUserRepository())
    fun provideSettingsViewModelFactory() = SettingsViewModelFactory(RepositoryFactory.getUserRepository())
    fun provideWordViewModelFactory() = WordViewModelFactory()
    fun provideCategorySectionViewModelFactory(type: CategorySection) = CategorySectionViewModelFactory(type, RepositoryFactory.getUserRepository())
    fun provideTrainingSettingViewModelFactory() = TrainingSettingViewModelFactory()
    fun provideTrainingViewModelFactory(context: Context) = TrainingViewModelFactory(RepositoryFactory.getWordRepository(context))
    fun provideStatisticViewModelFactory(context: Context) = StatisticViewModelFactory(RepositoryFactory.getWordRepository(context))
    fun provideTimeViewModelFactory() = TimeViewModelFactory(RepositoryFactory.getUserRepository())
    fun provideDictionaryContainerViewModelFactory() = DictionaryContainerViewModelFactory()
    /*fun provideCategoriesViewModelFactory() = CategoriesViewModelFactory()*/
}

object RepositoryFactory {

    internal fun getUserRepository() = RepositoryUser.getInstance()
    internal fun getWordRepository(context: Context) = RepositoryWord.getInstance(AppDatabase.getInstance(context.applicationContext).wordDao())
}
