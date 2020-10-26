package relaxeddd.englishnotify

import android.annotation.SuppressLint
import android.content.Context
import androidx.multidex.MultiDexApplication
import com.google.firebase.FirebaseApp
import relaxeddd.englishnotify.model.preferences.SharedHelper

class App : MultiDexApplication() {

    companion object {
        @SuppressLint("StaticFieldLeak")
        lateinit var context: Context
    }

    override fun onCreate() {
        super.onCreate()
        context = this
        FirebaseApp.initializeApp(this)
        SharedHelper.setLaunchCount(SharedHelper.getLaunchCount() + 1)
    }
}
