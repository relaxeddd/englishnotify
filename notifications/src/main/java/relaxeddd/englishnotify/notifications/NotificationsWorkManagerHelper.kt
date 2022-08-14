package relaxeddd.englishnotify.notifications

import android.content.Context
import androidx.work.BackoffPolicy
import androidx.work.ExistingPeriodicWorkPolicy
import androidx.work.OneTimeWorkRequest
import androidx.work.PeriodicWorkRequestBuilder
import androidx.work.WorkManager
import relaxeddd.englishnotify.preferences.Preferences
import java.util.concurrent.TimeUnit

class NotificationsWorkManagerHelper {

    companion object {

        private const val WORK_NAME_NOTIFICATIONS = "workNameNotifications"

        fun launchWork(
            context: Context,
            prefs: Preferences,
            repeatTimeInMinutes: Long,
            isForceUpdate: Boolean,
        ) {
            if (!prefs.isNotificationsEnabled()) {
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
