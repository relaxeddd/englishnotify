package relaxeddd.englishnotify.push

import android.app.Activity
import android.app.NotificationChannel
import android.app.NotificationManager
import android.os.Build
import androidx.core.app.NotificationCompat
import relaxeddd.englishnotify.R
import relaxeddd.englishnotify.common.ENGLISH_WORDS_NOTIFICATIONS_CHANNEL

object PushTokenHelper {

    fun initChannelNotifications(activity: Activity) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channelId = activity.getString(R.string.default_notification_channel_id)
            val notificationManager = activity.getSystemService(NotificationManager::class.java)
            val channel = NotificationChannel(channelId, ENGLISH_WORDS_NOTIFICATIONS_CHANNEL, NotificationManager.IMPORTANCE_HIGH)

            channel.setSound(null, null)
            channel.setShowBadge(false)

            notificationManager?.createNotificationChannel(channel)
        }
    }

    val notificationsPriority get() = NotificationCompat.PRIORITY_HIGH
}
