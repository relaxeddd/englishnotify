package relaxeddd.englishnotify.common

import android.content.Context
import relaxeddd.englishnotify.model.db.AppDatabase
import relaxeddd.englishnotify.model.repository.RepositoryWord

object InjectorUtils {

    fun provideMainViewModelFactory() = MainViewModelFactory()
    fun provideDictionaryAllViewModelFactory(context: Context) = DictionaryAllViewModelFactory(RepositoryFactory.getWordRepository(context))
    fun provideDictionaryKnowViewModelFactory(context: Context) = DictionaryKnowViewModelFactory(RepositoryFactory.getWordRepository(context))
    fun provideNotificationsViewModelFactory() = NotificationsViewModelFactory()
    fun provideSettingsViewModelFactory() = SettingsViewModelFactory()
    fun provideWordViewModelFactory() = WordViewModelFactory()
    fun provideParseViewModelFactory() = ParseViewModelFactory()
    fun provideParsedWordsViewModelFactory() = ParsedWordsViewModelFactory()
    fun provideCategorySectionViewModelFactory() = CategorySectionViewModelFactory()
    fun provideTrainingSettingViewModelFactory(context: Context) = TrainingSettingViewModelFactory(RepositoryFactory.getWordRepository(context))
    fun provideTrainingViewModelFactory(context: Context) = TrainingViewModelFactory(RepositoryFactory.getWordRepository(context))
    fun provideStatisticViewModelFactory(context: Context) = StatisticViewModelFactory(RepositoryFactory.getWordRepository(context))
    fun provideTimeViewModelFactory() = TimeViewModelFactory()
    fun provideDictionaryContainerViewModelFactory() = DictionaryContainerViewModelFactory()
}

object RepositoryFactory {

    internal fun getWordRepository(context: Context) = RepositoryWord.getInstance(AppDatabase.getInstance(context.applicationContext).wordDao())
}
