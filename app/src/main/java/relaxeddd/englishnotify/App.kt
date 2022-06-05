package relaxeddd.englishnotify

import android.annotation.SuppressLint
import android.app.Application
import android.content.Context
import com.google.firebase.FirebaseApp
import relaxeddd.englishnotify.model.preferences.SharedHelper
import relaxeddd.englishnotify.push.NotificationsWorkManagerHelper

class App : Application() {

    companion object {
        @SuppressLint("StaticFieldLeak")
        lateinit var context: Context
    }

    override fun onCreate() {
        super.onCreate()
        context = this
        FirebaseApp.initializeApp(this)
        SharedHelper.setLaunchCount(SharedHelper.getLaunchCount() + 1)

        NotificationsWorkManagerHelper.launchWork(
            context = this,
            isForceUpdate = false,
        )
    }
}
