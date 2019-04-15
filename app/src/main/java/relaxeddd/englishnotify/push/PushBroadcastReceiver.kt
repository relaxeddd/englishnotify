package relaxeddd.englishnotify.push

import android.app.NotificationManager
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.os.Build
import androidx.core.app.RemoteInput
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import relaxeddd.englishnotify.R
import relaxeddd.englishnotify.common.*
import relaxeddd.englishnotify.model.db.AppDatabase

class PushBroadcastReceiver : BroadcastReceiver() {

    companion object {
        const val ACTION_KNOW = "relaxeddd.englishnotify.KNOW"
        const val ACTION_ANSWER = "relaxeddd.englishnotify.ANSWER"
        const val KEY_TEXT_REPLY = "key_text_reply"

        const val DONT_KNOW = 0
        const val KNOW = 1
    }

    override fun onReceive(context: Context, intent: Intent) {
        CoroutineScope(Dispatchers.IO).launch {
            val notificationId = intent.getIntExtra(NOTIFICATION_ID, -1)
            val wordId = intent.getStringExtra(WORD_ID)
            val isKnow = intent.getIntExtra(IS_KNOW, DONT_KNOW)
            val notificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            val word = AppDatabase.getInstance(context).wordDao().findWordById(wordId) ?: return@launch
            val languageType = SharedHelper.getLearnLanguageType()

            if (isKnow == KNOW) {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                    val userText = RemoteInput.getResultsFromIntent(intent)?.getCharSequence(KEY_TEXT_REPLY).toString().toLowerCase()
                    var answer = if (languageType == TYPE_PUSH_ENGLISH) word.rus else word.eng
                    val answerWords = answer.split(",")

                    withContext(Dispatchers.Main) {
                        for (answerWord in answerWords) {
                            if (answerWord.trim().toLowerCase() == userText) {
                                showToast(R.string.answer_correct)
                                return@withContext
                            }
                        }

                        val title = if (languageType == TYPE_PUSH_ENGLISH) word.eng else word.rus
                        val fullText = MyFirebaseMessagingService.getFullNotificationText(word, languageType)

                        showToast(R.string.answer_incorrect)
                        MyFirebaseMessagingService.showNotification(context, wordId, fullText, title, false)
                    }
                } else {
                    word.isLearned = true
                    AppDatabase.getInstance(context).wordDao().insertAll(word)
                }
            } else {
                val title = if (languageType == TYPE_PUSH_ENGLISH) word.eng else word.rus
                val fullText = MyFirebaseMessagingService.getFullNotificationText(word, languageType)
                withContext(Dispatchers.Main) {
                    MyFirebaseMessagingService.showNotification(context, wordId, fullText, title, false)
                }
            }
            if (notificationId != -1) {
                notificationManager.cancel(notificationId)
            }
        }
    }
}