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
import relaxeddd.englishnotify.model.repository.RepositoryWord

class PushBroadcastReceiver : BroadcastReceiver() {

    companion object {
        const val ACTION_KNOW = "relaxeddd.englishnotify.KNOW"
        const val KEY_TEXT_REPLY = "key_text_reply"

        const val NOT_KNOW = 0
        const val KNOW = 1
    }

    override fun onReceive(context: Context, intent: Intent) {
        CoroutineScope(Dispatchers.IO).launch {
            val wordDao = AppDatabase.getInstance(context).wordDao()
            val notificationId = intent.getIntExtra(NOTIFICATION_ID, -1)
            val wordId = intent.getStringExtra(WORD_ID)
            val isKnow = intent.getIntExtra(IS_KNOW, NOT_KNOW)
            val notificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            val word = wordDao.findWordById(wordId) ?: return@launch
            val languageType = SharedHelper.getLearnLanguageType()
            val saveWord = Word(word)
            var learnStage = saveWord.learnStage

            if (isKnow == KNOW) {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                    val userText = RemoteInput.getResultsFromIntent(intent)?.getCharSequence(KEY_TEXT_REPLY).toString().toLowerCase()
                    val answer = if (languageType == TYPE_PUSH_ENGLISH) word.rus else word.eng
                    val isCorrectAnswer = isCorrectAnswer(userText, answer)

                    if (isCorrectAnswer && learnStage != LEARN_STAGE_MAX) {
                        learnStage++
                    } else if (learnStage != LEARN_STAGE_MAX) {
                        learnStage = 0
                    }

                    withContext(Dispatchers.Main) {
                        if (isCorrectAnswer) {
                            showToast(R.string.answer_correct)
                        } else {
                            showToast(R.string.answer_incorrect)
                            MyFirebaseMessagingService.handleWordNotification(context, word, false,
                                SharedHelper.NOTIFICATIONS_VIEW_WITH_TRANSLATE, withWrongTitle = true)
                        }
                    }
                }
            } else {
                if (learnStage != LEARN_STAGE_MAX) {
                    learnStage = 0
                }

                withContext(Dispatchers.Main) {
                    MyFirebaseMessagingService.handleWordNotification(context, word, false, SharedHelper.NOTIFICATIONS_VIEW_WITH_TRANSLATE)
                }
            }
            if (saveWord.learnStage != learnStage) {
                RepositoryWord.getInstance().setWordLearnStage(saveWord, learnStage, false)
            }
            if (notificationId != -1) {
                notificationManager.cancel(notificationId)
            }
        }
    }
}