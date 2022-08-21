package relaxeddd.englishnotify.di

import android.content.Context
import dagger.BindsInstance
import dagger.Component
import dagger.android.AndroidInjector
import dagger.android.support.AndroidSupportInjectionModule
import relaxeddd.englishnotify.App
import relaxeddd.englishnotify.domain_words.di.DomainWordsModule
import relaxeddd.englishnotify.screen_settings.ui.di.SettingsDependencies
import javax.inject.Singleton

@Singleton
@Component(
    modules = [
        AndroidSupportInjectionModule::class,
        ApplicationModule::class,
        DomainWordsModule::class,
        MainActivityModule::class,
        CategorySectionModule::class,
        CategoriesModule::class,
        DictionaryAllModule::class,
        DictionaryContainerModule::class,
        DictionaryKnowModule::class,
        NotificationsModule::class,
        ParseModule::class,
        ParsedWordsModule::class,
        StatisticModule::class,
        TimeModule::class,
        TrainingModule::class,
        TrainingSettingModule::class,
        WordModule::class,
        NotificationAnswerModule::class,
    ])
interface ApplicationComponent : AndroidInjector<App>, SettingsDependencies {

    @Component.Factory
    interface Factory {
        fun create(@BindsInstance applicationContext: Context): ApplicationComponent
    }
}
