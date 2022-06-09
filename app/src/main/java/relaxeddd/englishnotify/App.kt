package relaxeddd.englishnotify

import android.annotation.SuppressLint
import android.app.Application
import android.content.Context
import relaxeddd.englishnotify.model.preferences.SharedHelper
import relaxeddd.englishnotify.model.repository.RepositoryWord
import relaxeddd.englishnotify.push.NotificationsWorkManagerHelper

class App : Application() {

    companion object {
        @SuppressLint("StaticFieldLeak")
        lateinit var context: Context
    }

    override fun onCreate() {
        super.onCreate()
        context = this
        SharedHelper.setLaunchCount(SharedHelper.getLaunchCount() + 1)
        RepositoryWord.getInstance() //TODO: Initialization workaround

        NotificationsWorkManagerHelper.launchWork(
            context = this,
            isForceUpdate = false,
        )
    }
}
