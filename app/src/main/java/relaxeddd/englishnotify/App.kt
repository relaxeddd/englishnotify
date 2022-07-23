package relaxeddd.englishnotify

import android.annotation.SuppressLint
import android.app.Application
import android.content.Context
import relaxeddd.englishnotify.domain_words.repository.RepositoryWords
import relaxeddd.englishnotify.notifications.NotificationsWorkManagerHelper
import relaxeddd.englishnotify.notifications.PushTokenHelper
import relaxeddd.englishnotify.preferences.Preferences

class App : Application() {

    companion object {
        @SuppressLint("StaticFieldLeak")
        lateinit var context: Context
    }

    override fun onCreate() {
        super.onCreate()
        context = this
        Preferences.init(this)
        RepositoryWords.getInstance(this) //TODO: Initialization workaround

        PushTokenHelper.initNotificationsChannel(this)
        NotificationsWorkManagerHelper.launchWork(
            context = this,
            repeatTimeInMinutes = Preferences.getInstance().getNotificationsRepeatTime().valueInMinutes,
            isForceUpdate = false,
        )
    }
}
