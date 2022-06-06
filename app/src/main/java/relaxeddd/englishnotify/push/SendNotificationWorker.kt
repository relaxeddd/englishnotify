package relaxeddd.englishnotify.push

import android.content.Context
import androidx.work.Worker
import androidx.work.WorkerParameters

class SendNotificationWorker(context: Context, workerParams: WorkerParameters) : Worker(context, workerParams) {

    override fun doWork(): Result {
        NotificationHelper.generateNotification(applicationContext)
        return Result.success()
    }
}
