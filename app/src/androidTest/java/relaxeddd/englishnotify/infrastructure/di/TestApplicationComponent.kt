package relaxeddd.englishnotify.infrastructure.di

import android.content.Context
import dagger.BindsInstance
import dagger.Component
import dagger.android.support.AndroidSupportInjectionModule
import relaxeddd.englishnotify.common.AppWorkerFactory
import relaxeddd.englishnotify.di.*
import relaxeddd.englishnotify.domain_words.di.DomainWordsModule
import relaxeddd.englishnotify.notifications.NotificationsWorkManagerHelper
import javax.inject.Singleton

@Singleton
@Component(
    modules = [
        AndroidSupportInjectionModule::class,
        TestApplicationModule::class,
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
interface TestApplicationComponent : ApplicationComponent {

    @Singleton
    override val notificationsWorkManagerHelper: NotificationsWorkManagerHelper

    @Singleton
    override val workerFactory: AppWorkerFactory

    @Component.Factory
    interface Factory {
        fun create(@BindsInstance applicationContext: Context): TestApplicationComponent
    }
}
