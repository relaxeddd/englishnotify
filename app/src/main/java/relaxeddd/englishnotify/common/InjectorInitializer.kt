package relaxeddd.englishnotify.common

import android.app.Application
import relaxeddd.englishnotify.common_di.Injector
import relaxeddd.englishnotify.di.ApplicationComponent
import relaxeddd.englishnotify.screen_settings.ui.di.SettingsComponent

object InjectorInitializer {

    fun init(application: Application, appComponent: ApplicationComponent) {
        Injector.init(
            application,
            listOf(relaxeddd.englishnotify.common_di.ComponentLazyReference(SettingsComponent::class.java) {
                SettingsComponent(appComponent)
            })
        )
    }
}
