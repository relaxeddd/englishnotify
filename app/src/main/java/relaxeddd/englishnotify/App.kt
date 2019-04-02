package relaxeddd.englishnotify

import android.content.Context
import androidx.multidex.MultiDexApplication
import com.google.firebase.FirebaseApp

class App : MultiDexApplication() {

    companion object {
        lateinit var context: Context
    }

    override fun onCreate() {
        super.onCreate()
        context = this
        FirebaseApp.initializeApp(this)
    }
}