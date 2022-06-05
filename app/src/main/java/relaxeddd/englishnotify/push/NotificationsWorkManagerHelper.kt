package relaxeddd.englishnotify.push

import android.content.Context
import androidx.work.ExistingPeriodicWorkPolicy
import androidx.work.PeriodicWorkRequestBuilder
import androidx.work.WorkManager
import java.util.concurrent.TimeUnit

class NotificationsWorkManagerHelper {

    companion object {

        private const val WORK_NAME_NOTIFICATIONS = "workNameNotifications"

        fun launchWork(context: Context, repeatTimeInMinutes: Long, isForceUpdate: Boolean) {
            val notificationsWork = PeriodicWorkRequestBuilder<SendNotificationWorker>(
                repeatInterval = repeatTimeInMinutes,
                TimeUnit.MINUTES,
            ).setInitialDelay(repeatTimeInMinutes, TimeUnit.MINUTES).build()
            val workPolicy = if (isForceUpdate) ExistingPeriodicWorkPolicy.REPLACE else ExistingPeriodicWorkPolicy.KEEP

            WorkManager.getInstance(context).enqueueUniquePeriodicWork(
                WORK_NAME_NOTIFICATIONS,
                workPolicy,
                notificationsWork,
            )
        }
    }
}
