package relaxeddd.englishnotify.push

import android.app.Activity
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.ComponentName
import android.content.pm.PackageManager
import android.os.Build
import relaxeddd.englishnotify.R
import relaxeddd.englishnotify.common.ENGLISH_WORDS_NOTIFICATIONS_CHANNEL

object PushTokenHelper {

    fun initChannelNotifications(activity: Activity) {
        val componentName = ComponentName(activity, MyFirebaseMessagingService::class.java)
        activity.packageManager.setComponentEnabledSetting(componentName, PackageManager.COMPONENT_ENABLED_STATE_ENABLED, PackageManager.DONT_KILL_APP)

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channelId = activity.getString(R.string.default_notification_channel_id)
            val notificationManager = activity.getSystemService(NotificationManager::class.java)
            val channel = NotificationChannel(channelId, ENGLISH_WORDS_NOTIFICATIONS_CHANNEL, NotificationManager.IMPORTANCE_HIGH)

            channel.setSound(null, null)

            notificationManager?.createNotificationChannel(channel)
        }
    }
}