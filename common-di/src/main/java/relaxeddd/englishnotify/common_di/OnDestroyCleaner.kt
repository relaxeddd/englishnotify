package relaxeddd.englishnotify.common_di

import android.app.Activity
import android.app.Application
import android.os.Bundle
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.fragment.app.FragmentManager

class OnDestroyCleaner(private val injector: Injector) : Application.ActivityLifecycleCallbacks {

    override fun onActivityCreated(activity: Activity, savedInstanceState: Bundle?) {
        if (activity is FragmentActivity) {
            val fragmentManager = activity.supportFragmentManager
            fragmentManager.registerFragmentLifecycleCallbacks(object : FragmentManager.FragmentLifecycleCallbacks() {

                override fun onFragmentDestroyed(fm: FragmentManager, fragment: Fragment) {
                    injector.remove(fragment)
                }
            }, true)
        }
    }

    override fun onActivityDestroyed(activity: Activity) {
        injector.remove(activity)
    }

    override fun onActivityStarted(p0: Activity) = Unit

    override fun onActivityResumed(p0: Activity) = Unit

    override fun onActivityPaused(p0: Activity) = Unit

    override fun onActivityStopped(p0: Activity) = Unit

    override fun onActivitySaveInstanceState(p0: Activity, p1: Bundle) = Unit
}


