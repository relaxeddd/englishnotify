package relaxeddd.englishnotify.push

import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.content.Context.NOTIFICATION_SERVICE
import android.os.Build
import androidx.core.app.NotificationCompat
import relaxeddd.englishnotify.R
import relaxeddd.englishnotify.common.ENGLISH_WORDS_NOTIFICATIONS_CHANNEL

object PushTokenHelper {

    fun initNotificationsChannel(context: Context) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channelId = context.getString(R.string.default_notification_channel_id)
            val notificationManager = context.getSystemService(NOTIFICATION_SERVICE) as NotificationManager
            val channel = NotificationChannel(channelId, ENGLISH_WORDS_NOTIFICATIONS_CHANNEL, NotificationManager.IMPORTANCE_HIGH)

            channel.setSound(null, null)
            channel.setShowBadge(false)

            notificationManager.createNotificationChannel(channel)
        }
    }

    val notificationsPriority get() = NotificationCompat.PRIORITY_HIGH
}
