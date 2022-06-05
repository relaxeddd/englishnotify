package relaxeddd.englishnotify.push

import android.content.Context
import android.os.Build
import androidx.core.app.NotificationManagerCompat
import androidx.work.Worker
import androidx.work.WorkerParameters
import relaxeddd.englishnotify.R
import relaxeddd.englishnotify.common.OWN
import relaxeddd.englishnotify.common.TYPE_PUSH_RUSSIAN
import relaxeddd.englishnotify.common.Word
import relaxeddd.englishnotify.common.getAppString
import relaxeddd.englishnotify.common.isNightTime
import relaxeddd.englishnotify.model.db.AppDatabase
import relaxeddd.englishnotify.model.preferences.SharedHelper
import java.util.ArrayList

class SendNotificationWorker(context: Context, workerParams: WorkerParameters) : Worker(context, workerParams) {

    override fun doWork(): Result {
        if (isNightTime(context = applicationContext)) {
            return Result.success()
        }

        val notificationManager = NotificationManagerCompat.from(applicationContext)
        if (!notificationManager.areNotificationsEnabled()) {
            return Result.success()
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channelId = getAppString(R.string.default_notification_channel_id)
            val channel = notificationManager.getNotificationChannel(channelId)

            if (channel?.importance == NotificationManagerCompat.IMPORTANCE_NONE) {
                return Result.success()
            }
        }

        val tag = SharedHelper.getSelectedCategory(applicationContext)
        val isEnabledSecondaryProgress = SharedHelper.isEnabledSecondaryProgress(applicationContext)
        val languageType = SharedHelper.getLearnLanguageType(applicationContext)

        val wordDao = AppDatabase.getInstance(applicationContext).wordDao()
        var words = wordDao.getAllItemsNow()
        val sortByLearnStage = HashMap<Int, ArrayList<Word>>()

        words = words.filter { !it.isDeleted && it.isOwnCategory && (tag.isEmpty() || tag == OWN || it.tags.contains(tag)) }

        for (word in words) {
            val wordLearnStage = if (languageType == TYPE_PUSH_RUSSIAN && isEnabledSecondaryProgress) word.learnStageSecondary else word.learnStage

            if (!sortByLearnStage.containsKey(wordLearnStage)) {
                val list = ArrayList<Word>()
                list.add(word)
                sortByLearnStage[wordLearnStage] = list
            } else {
                sortByLearnStage[wordLearnStage]?.add(word)
            }
        }
        for (learnStage in 0..2) {
            if (sortByLearnStage.containsKey(learnStage)) {
                words = sortByLearnStage[learnStage] ?: ArrayList()
                break
            }
        }

        if (words.isNotEmpty()) {
            val wordIx = (words.indices).random()

            if (wordIx >= 0 && wordIx < words.size) {
                if (SharedHelper.isShowOnlyOneNotification(applicationContext)) {
                    notificationManager.cancelAll()
                }
                NotificationHelper.handleWordNotification(
                    applicationContext,
                    words[wordIx],
                    isSave = false,
                    viewType = SharedHelper.getNotificationsView(applicationContext),
                )
            }
        }

        return Result.success()
    }
}
