package relaxeddd.englishnotify.notifications

import android.content.Context
import androidx.work.Worker
import androidx.work.WorkerParameters
import relaxeddd.englishnotify.common.MainActivityUsed
import relaxeddd.englishnotify.domain_words.repository.RepositoryWords
import relaxeddd.englishnotify.preferences.Preferences

class SendNotificationWorker(
    context: Context,
    workerParams: WorkerParameters,
    private val prefs: Preferences,
    private val repositoryWords: RepositoryWords,
) : Worker(context, workerParams) {

    @MainActivityUsed
    override fun doWork(): Result {
        val mainActivityClass = Class.forName("relaxeddd.englishnotify.ui.main.MainActivity")
        NotificationHelper.generateNotification(applicationContext, mainActivityClass, prefs, repositoryWords)
        return Result.success()
    }
}
