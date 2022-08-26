package relaxeddd.englishnotify.notifications

import android.content.Context
import androidx.work.BackoffPolicy
import androidx.work.ExistingPeriodicWorkPolicy
import androidx.work.OneTimeWorkRequest
import androidx.work.PeriodicWorkRequestBuilder
import androidx.work.WorkManager
import java.util.concurrent.TimeUnit
import javax.inject.Inject
import javax.inject.Singleton

interface NotificationsWorkManagerHelper {

    fun launchWork(repeatTimeInMinutes: Long, isForceUpdate: Boolean, isNotificationsEnabled: Boolean)

    fun cancelWork()
}

@Singleton
class NotificationsWorkManagerHelperImpl @Inject constructor(
    private val context: Context,
) : NotificationsWorkManagerHelper {

    companion object {

        private const val WORK_NAME_NOTIFICATIONS = "workNameNotifications"
    }

    override fun launchWork(repeatTimeInMinutes: Long, isForceUpdate: Boolean, isNotificationsEnabled: Boolean) {
        if (!isNotificationsEnabled) {
            cancelWork()
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

    override fun cancelWork() {
        WorkManager.getInstance(context).cancelUniqueWork(WORK_NAME_NOTIFICATIONS)
    }
}
