package relaxeddd.englishnotify

import androidx.work.Configuration
import androidx.work.WorkManager
import dagger.android.AndroidInjector
import dagger.android.DaggerApplication
import relaxeddd.englishnotify.common.InjectorInitializer
import relaxeddd.englishnotify.di.ApplicationComponent
import relaxeddd.englishnotify.di.DaggerApplicationComponent
import relaxeddd.englishnotify.notifications.PushTokenHelper

open class App : DaggerApplication() {

    lateinit var applicationComponent: ApplicationComponent

    protected open fun createComponent(): ApplicationComponent {
        return DaggerApplicationComponent.factory().create(applicationContext)
    }

    override fun applicationInjector(): AndroidInjector<out DaggerApplication> {
        applicationComponent = createComponent()
        return applicationComponent
    }

    override fun onCreate() {
        super.onCreate()

        val prefs = applicationComponent.prefs
        val workerFactory = applicationComponent.workerFactory
        val notificationsWorkManagerHelper = applicationComponent.notificationsWorkManagerHelper

        InjectorInitializer.init(this, applicationComponent)
        WorkManager.initialize(this, Configuration.Builder().setWorkerFactory(workerFactory).build())
        PushTokenHelper.initNotificationsChannel(this)
        notificationsWorkManagerHelper.launchWork(
            repeatTimeInMinutes = prefs.getNotificationsRepeatTime().valueInMinutes,
            isForceUpdate = false,
            isNotificationsEnabled = prefs.isNotificationsEnabled(),
        )
    }
}
