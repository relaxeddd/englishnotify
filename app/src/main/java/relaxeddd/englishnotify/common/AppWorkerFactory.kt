package relaxeddd.englishnotify.common

import android.content.Context
import androidx.work.ListenableWorker
import androidx.work.WorkerFactory
import androidx.work.WorkerParameters
import relaxeddd.englishnotify.domain_words.repository.RepositoryWords
import relaxeddd.englishnotify.notifications.SendNotificationWorker
import relaxeddd.englishnotify.preferences.Preferences
import javax.inject.Inject

class AppWorkerFactory @Inject constructor(
    private val prefs: Preferences,
    private val repositoryWords: RepositoryWords,
) : WorkerFactory() {

    override fun createWorker(
        appContext: Context,
        workerClassName: String,
        workerParameters: WorkerParameters,
    ): ListenableWorker? {
        return when(workerClassName) {
            SendNotificationWorker::class.java.name -> {
                SendNotificationWorker(appContext, workerParameters, prefs, repositoryWords)
            }
            else -> null
        }
    }
}
