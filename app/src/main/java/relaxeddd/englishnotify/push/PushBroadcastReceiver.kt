package relaxeddd.englishnotify.push

import android.app.NotificationManager
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import relaxeddd.englishnotify.common.NOTIFICATION_ID
import relaxeddd.englishnotify.common.WORD_ID
import relaxeddd.englishnotify.model.db.AppDatabase

class PushBroadcastReceiver : BroadcastReceiver() {

    companion object {
        const val ACTION_KNOW = "relaxeddd.englishnotify.KNOW"
        const val ACTION_ANSWER = "relaxeddd.englishnotify.ANSWER"
    }

    override fun onReceive(context: Context, intent: Intent) {
        val notificationId = intent.getIntExtra(NOTIFICATION_ID, -1)
        val wordId = intent.getStringExtra(WORD_ID)
        val notificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        if (notificationId != -1) {
            notificationManager.cancel(notificationId)
        }
        CoroutineScope(Dispatchers.IO).launch {
            val wordLiveData = AppDatabase.getInstance(context).wordDao().findById(wordId)
            val word = wordLiveData.value

            if (word != null) {
                word.isLearned = true
                AppDatabase.getInstance(context).wordDao().insertAll(word)
            }
        }
    }
}