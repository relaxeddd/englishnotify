package relaxeddd.englishnotify

import androidx.work.Configuration
import androidx.work.WorkManager
import dagger.android.AndroidInjector
import dagger.android.DaggerApplication
import relaxeddd.englishnotify.common.AppWorkerFactory
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

    override fun applicationInjector(): AndroidInjector<out DaggerApplication> {
        return DaggerApplicationComponent.factory().create(applicationContext)
    }

    override fun onCreate() {
        super.onCreate()

        WorkManager.initialize(this, Configuration.Builder().setWorkerFactory(workerFactory).build())
        PushTokenHelper.initNotificationsChannel(this)
        NotificationsWorkManagerHelper.launchWork(
            context = this,
            prefs = prefs,
            repeatTimeInMinutes = prefs.getNotificationsRepeatTime().valueInMinutes,
            isForceUpdate = false,
        )
    }
}
