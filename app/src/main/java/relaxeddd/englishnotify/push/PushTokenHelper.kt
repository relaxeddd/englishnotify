package relaxeddd.englishnotify.push

import android.app.Activity
import android.app.NotificationChannel
import android.app.NotificationManager
import android.os.Build
import relaxeddd.englishnotify.R
import com.google.android.gms.common.GoogleApiAvailability

object PushTokenHelper {

    fun initChannelNotifications(activity: Activity) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channelId = activity.getString(R.string.default_notification_channel_id)
            val channelName = activity.getString(R.string.app_name)
            val notificationManager = activity.getSystemService(NotificationManager::class.java)

            notificationManager?.createNotificationChannel(NotificationChannel(channelId, channelName, NotificationManager.IMPORTANCE_LOW))
        }
        GoogleApiAvailability.getInstance().makeGooglePlayServicesAvailable(activity)
    }
}