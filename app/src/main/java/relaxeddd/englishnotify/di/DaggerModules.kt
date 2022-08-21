package relaxeddd.englishnotify.di

import androidx.lifecycle.ViewModel
import dagger.Binds
import dagger.Module
import dagger.android.ContributesAndroidInjector
import dagger.multibindings.IntoMap
import relaxeddd.englishnotify.notifications.NotificationAnswerBroadcastReceiver
import relaxeddd.englishnotify.ui.categories.FragmentCategories
import relaxeddd.englishnotify.ui.categories.ViewModelCategories
import relaxeddd.englishnotify.ui.categories.section.FragmentCategorySection
import relaxeddd.englishnotify.ui.categories.section.ViewModelCategorySection
import relaxeddd.englishnotify.ui.dictionary_all.FragmentDictionaryAll
import relaxeddd.englishnotify.ui.dictionary_all.ViewModelDictionaryAll
import relaxeddd.englishnotify.ui.dictionary_container.FragmentDictionaryContainer
import relaxeddd.englishnotify.ui.dictionary_container.ViewModelDictionaryContainer
import relaxeddd.englishnotify.ui.dictionary_know.FragmentDictionaryKnow
import relaxeddd.englishnotify.ui.dictionary_know.ViewModelDictionaryKnow
import relaxeddd.englishnotify.ui.main.MainActivity
import relaxeddd.englishnotify.ui.main.ViewModelMain
import relaxeddd.englishnotify.ui.notifications.FragmentNotifications
import relaxeddd.englishnotify.ui.notifications.ViewModelNotifications
import relaxeddd.englishnotify.ui.parse.FragmentParse
import relaxeddd.englishnotify.ui.parse.ViewModelParse
import relaxeddd.englishnotify.ui.parsed_words.FragmentParsedWords
import relaxeddd.englishnotify.ui.parsed_words.ViewModelParsedWords
import relaxeddd.englishnotify.ui.statistic.FragmentStatistic
import relaxeddd.englishnotify.ui.statistic.ViewModelStatistic
import relaxeddd.englishnotify.ui.time.FragmentTime
import relaxeddd.englishnotify.ui.time.ViewModelTime
import relaxeddd.englishnotify.ui.training.FragmentTraining
import relaxeddd.englishnotify.ui.training.ViewModelTraining
import relaxeddd.englishnotify.ui.training_setting.FragmentTrainingSetting
import relaxeddd.englishnotify.ui.training_setting.ViewModelTrainingSetting
import relaxeddd.englishnotify.ui.word.FragmentWord
import relaxeddd.englishnotify.ui.word.ViewModelWord

@Module
abstract class MainActivityModule {

    @ContributesAndroidInjector
    internal abstract fun activity(): MainActivity

    @Binds
    @IntoMap
    @ViewModelKey(ViewModelMain::class)
    abstract fun bindViewModel(viewModel: ViewModelMain): ViewModel
}

@Module
abstract class CategoriesModule {

    @ContributesAndroidInjector
    internal abstract fun fragmentCategories(): FragmentCategories

    @Binds
    @IntoMap
    @ViewModelKey(ViewModelCategories::class)
    abstract fun bindViewModel(viewModel: ViewModelCategories): ViewModel
}

@Module
abstract class CategorySectionModule {

    @ContributesAndroidInjector
    internal abstract fun fragmentCategorySection(): FragmentCategorySection

    @Binds
    @IntoMap
    @ViewModelKey(ViewModelCategorySection::class)
    abstract fun bindViewModel(viewModel: ViewModelCategorySection): ViewModel
}

@Module
abstract class DictionaryAllModule {

    @ContributesAndroidInjector
    internal abstract fun fragment(): FragmentDictionaryAll

    @Binds
    @IntoMap
    @ViewModelKey(ViewModelDictionaryAll::class)
    abstract fun bindViewModel(viewModel: ViewModelDictionaryAll): ViewModel
}

@Module
abstract class DictionaryContainerModule {

    @ContributesAndroidInjector
    internal abstract fun fragment(): FragmentDictionaryContainer

    @Binds
    @IntoMap
    @ViewModelKey(ViewModelDictionaryContainer::class)
    abstract fun bindViewModel(viewModel: ViewModelDictionaryContainer): ViewModel
}

@Module
abstract class DictionaryKnowModule {

    @ContributesAndroidInjector
    internal abstract fun fragment(): FragmentDictionaryKnow

    @Binds
    @IntoMap
    @ViewModelKey(ViewModelDictionaryKnow::class)
    abstract fun bindViewModel(viewModel: ViewModelDictionaryKnow): ViewModel
}

@Module
abstract class NotificationsModule {

    @ContributesAndroidInjector
    internal abstract fun fragment(): FragmentNotifications

    @Binds
    @IntoMap
    @ViewModelKey(ViewModelNotifications::class)
    abstract fun bindViewModel(viewModel: ViewModelNotifications): ViewModel
}

@Module
abstract class ParseModule {

    @ContributesAndroidInjector
    internal abstract fun fragment(): FragmentParse

    @Binds
    @IntoMap
    @ViewModelKey(ViewModelParse::class)
    abstract fun bindViewModel(viewModel: ViewModelParse): ViewModel
}

@Module
abstract class ParsedWordsModule {

    @ContributesAndroidInjector
    internal abstract fun fragment(): FragmentParsedWords

    @Binds
    @IntoMap
    @ViewModelKey(ViewModelParsedWords::class)
    abstract fun bindViewModel(viewModel: ViewModelParsedWords): ViewModel
}

@Module
abstract class StatisticModule {

    @ContributesAndroidInjector
    internal abstract fun fragment(): FragmentStatistic

    @Binds
    @IntoMap
    @ViewModelKey(ViewModelStatistic::class)
    abstract fun bindViewModel(viewModel: ViewModelStatistic): ViewModel
}

@Module
abstract class TimeModule {

    @ContributesAndroidInjector
    internal abstract fun fragment(): FragmentTime

    @Binds
    @IntoMap
    @ViewModelKey(ViewModelTime::class)
    abstract fun bindViewModel(viewModel: ViewModelTime): ViewModel
}

@Module
abstract class TrainingModule {

    @ContributesAndroidInjector
    internal abstract fun fragment(): FragmentTraining

    @Binds
    @IntoMap
    @ViewModelKey(ViewModelTraining::class)
    abstract fun bindViewModel(viewModel: ViewModelTraining): ViewModel
}

@Module
abstract class TrainingSettingModule {

    @ContributesAndroidInjector
    internal abstract fun fragment(): FragmentTrainingSetting

    @Binds
    @IntoMap
    @ViewModelKey(ViewModelTrainingSetting::class)
    abstract fun bindViewModel(viewModel: ViewModelTrainingSetting): ViewModel
}

@Module
abstract class WordModule {

    @ContributesAndroidInjector
    internal abstract fun fragment(): FragmentWord

    @Binds
    @IntoMap
    @ViewModelKey(ViewModelWord::class)
    abstract fun bindViewModel(viewModel: ViewModelWord): ViewModel
}

@Module
abstract class NotificationAnswerModule {

    @ContributesAndroidInjector
    internal abstract fun broadcastReceiver(): NotificationAnswerBroadcastReceiver
}
