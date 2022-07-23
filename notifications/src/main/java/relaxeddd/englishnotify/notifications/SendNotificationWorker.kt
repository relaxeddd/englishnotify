package relaxeddd.englishnotify.notifications

import android.content.Context
import androidx.work.Worker
import androidx.work.WorkerParameters
import relaxeddd.englishnotify.common.MainActivityUsed

class SendNotificationWorker(context: Context, workerParams: WorkerParameters) : Worker(context, workerParams) {

    @MainActivityUsed
    override fun doWork(): Result {
        val mainActivityClass = Class.forName("relaxeddd.englishnotify.ui.main.MainActivity")
        NotificationHelper.generateNotification(applicationContext, mainActivityClass)
        return Result.success()
    }
}
