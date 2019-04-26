package relaxeddd.englishnotify.push

import android.app.Activity
import android.app.NotificationChannel
import android.app.NotificationManager
import android.os.Build
import relaxeddd.englishnotify.R

object PushTokenHelper {

    fun initChannelNotifications(activity: Activity) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channelId = activity.getString(R.string.default_notification_channel_id)
            val channelName = activity.getString(R.string.app_name)
            val notificationManager = activity.getSystemService(NotificationManager::class.java)
            val channel = NotificationChannel(channelId, channelName, NotificationManager.IMPORTANCE_HIGH)

            channel.setSound(null, null)

            notificationManager?.createNotificationChannel(channel)
        }
    }
}