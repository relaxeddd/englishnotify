package relaxeddd.englishnotify.push

import android.content.Context
import androidx.work.BackoffPolicy
import androidx.work.ExistingPeriodicWorkPolicy
import androidx.work.OneTimeWorkRequest
import androidx.work.PeriodicWorkRequestBuilder
import androidx.work.WorkManager
import relaxeddd.englishnotify.model.preferences.SharedHelper
import java.util.concurrent.TimeUnit

class NotificationsWorkManagerHelper {

    companion object {

        private const val WORK_NAME_NOTIFICATIONS = "workNameNotifications"

        fun launchWork(context: Context,
                       repeatTimeInMinutes: Long = SharedHelper.getNotificationsRepeatTime(context).valueInMinutes,
                       isForceUpdate: Boolean,
        ) {
            if (!SharedHelper.isNotificationsEnabled()) {
                cancelWork(context)
                return
            }

            val notificationsWork = PeriodicWorkRequestBuilder<SendNotificationWorker>(
                repeatInterval = repeatTimeInMinutes,
                TimeUnit.MINUTES,
            ).setBackoffCriteria(
                BackoffPolicy.LINEAR,
                OneTimeWorkRequest.MIN_BACKOFF_MILLIS,
                TimeUnit.MILLISECONDS
            ).setInitialDelay(repeatTimeInMinutes, TimeUnit.MINUTES).build()

            val workPolicy = if (isForceUpdate) ExistingPeriodicWorkPolicy.REPLACE else ExistingPeriodicWorkPolicy.KEEP

            WorkManager.getInstance(context).enqueueUniquePeriodicWork(
                WORK_NAME_NOTIFICATIONS,
                workPolicy,
                notificationsWork,
            )
        }

        fun cancelWork(context: Context) {
            WorkManager.getInstance(context).cancelUniqueWork(WORK_NAME_NOTIFICATIONS)
        }
    }
}
