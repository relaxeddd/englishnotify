package relaxeddd.englishnotify.infrastructure.di

import androidx.lifecycle.ViewModelProvider
import dagger.Binds
import dagger.Module
import relaxeddd.englishnotify.infrastructure.FakePreferences
import relaxeddd.englishnotify.notifications.NotificationsWorkManagerHelper
import relaxeddd.englishnotify.notifications.NotificationsWorkManagerHelperImpl
import relaxeddd.englishnotify.preferences.Preferences
import relaxeddd.englishnotify.ui.AppViewModelFactory
import javax.inject.Singleton

@Module(includes = [TestApplicationModuleBinds::class])
object TestApplicationModule

@Module
abstract class TestApplicationModuleBinds {

    @Binds
    internal abstract fun bindViewModelFactory(factory: AppViewModelFactory): ViewModelProvider.Factory

    @Singleton
    @Binds
    internal abstract fun bindNotificationsWorkManagerHelper(
        impl: NotificationsWorkManagerHelperImpl
    ): NotificationsWorkManagerHelper

    @Singleton
    @Binds
    internal abstract fun bindPreferences(prefs: FakePreferences): Preferences
}
