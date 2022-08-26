package relaxeddd.englishnotify

import androidx.work.Configuration
import androidx.work.WorkManager
import dagger.android.AndroidInjector
import dagger.android.DaggerApplication
import relaxeddd.englishnotify.common.AppWorkerFactory
import relaxeddd.englishnotify.common.InjectorInitializer
import relaxeddd.englishnotify.di.ApplicationComponent
import relaxeddd.englishnotify.di.DaggerApplicationComponent
import relaxeddd.englishnotify.notifications.NotificationsWorkManagerHelper
import relaxeddd.englishnotify.notifications.PushTokenHelper
import relaxeddd.englishnotify.preferences.Preferences
import javax.inject.Inject

class App : DaggerApplication() {

    @Inject
    lateinit var prefs: Preferences

    @Inject
    lateinit var workerFactory: AppWorkerFactory

    @Inject
    lateinit var notificationsWorkManagerHelper: NotificationsWorkManagerHelper

    private lateinit var applicationComponent: ApplicationComponent

    override fun applicationInjector(): AndroidInjector<out DaggerApplication> {
        applicationComponent = DaggerApplicationComponent.factory().create(applicationContext)
        return applicationComponent
    }

    override fun onCreate() {
        super.onCreate()

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
