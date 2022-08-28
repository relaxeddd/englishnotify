package relaxeddd.englishnotify.di

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import dagger.Binds
import dagger.MapKey
import dagger.Module
import relaxeddd.englishnotify.notifications.NotificationsWorkManagerHelper
import relaxeddd.englishnotify.notifications.NotificationsWorkManagerHelperImpl
import relaxeddd.englishnotify.preferences.Preferences
import relaxeddd.englishnotify.preferences.PreferencesImpl
import relaxeddd.englishnotify.ui.AppViewModelFactory
import javax.inject.Singleton
import kotlin.reflect.KClass

@Module(includes = [ApplicationModuleBinds::class])
object ApplicationModule

@Module
abstract class ApplicationModuleBinds {

    @Binds
    internal abstract fun bindViewModelFactory(factory: AppViewModelFactory): ViewModelProvider.Factory

    @Singleton
    @Binds
    internal abstract fun bindNotificationsWorkManagerHelper(
        impl: NotificationsWorkManagerHelperImpl
    ): NotificationsWorkManagerHelper

    @Singleton
    @Binds
    internal abstract fun bindPreferences(prefs: PreferencesImpl): Preferences
}

@Target(
    AnnotationTarget.FUNCTION, AnnotationTarget.PROPERTY_GETTER, AnnotationTarget.PROPERTY_SETTER
)
@Retention(AnnotationRetention.RUNTIME)
@MapKey
annotation class ViewModelKey(val value: KClass<out ViewModel>)
