package relaxeddd.englishnotify.push

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.os.Build
import androidx.core.app.NotificationManagerCompat
import androidx.core.app.RemoteInput
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import relaxeddd.englishnotify.R
import relaxeddd.englishnotify.common.IS_KNOW
import relaxeddd.englishnotify.common.NOTIFICATIONS_VIEW_WITH_TRANSLATE
import relaxeddd.englishnotify.common.NOTIFICATION_ID
import relaxeddd.englishnotify.common.WORD_ID
import relaxeddd.englishnotify.common.isCorrectAnswer
import relaxeddd.englishnotify.common.showToast
import relaxeddd.englishnotify.domain_words.entity.Word
import relaxeddd.englishnotify.domain_words.repository.RepositoryWords
import relaxeddd.englishnotify.preferences.Preferences
import relaxeddd.englishnotify.preferences.utils.TYPE_PUSH_ENGLISH
import relaxeddd.englishnotify.preferences.utils.TYPE_PUSH_RUSSIAN

class NotificationAnswerBroadcastReceiver : BroadcastReceiver() {

    companion object {
        const val ACTION_KNOW = "relaxeddd.englishnotify.KNOW"
        const val KEY_TEXT_REPLY = "key_text_reply"

        const val NOT_KNOW = 0
        const val KNOW = 1
    }

    override fun onReceive(context: Context, intent: Intent) {
        val pendingResult = goAsync()
        val prefs = Preferences.getInstance()

        CoroutineScope(Dispatchers.IO).launch {
            val repositoryWords = RepositoryWords.getInstance(context)

            val notificationId = intent.getIntExtra(NOTIFICATION_ID, -1)
            val wordId = intent.getStringExtra(WORD_ID) ?: ""
            val isKnow = intent.getIntExtra(IS_KNOW, NOT_KNOW)
            val word = repositoryWords.findWord(wordId) ?: return@launch
            val isEnabledSecondaryProgress = prefs.isEnabledSecondaryProgress()
            val languageType = prefs.getLearnLanguageType()
            val learnStageMax = prefs.getTrueAnswersToLearn()
            val saveWord = Word(word)
            var learnStage = if (languageType == TYPE_PUSH_RUSSIAN && isEnabledSecondaryProgress) saveWord.learnStageSecondary else saveWord.learnStage
            var isRemove = true
            val isRemovableViaDelay = Build.VERSION.SDK_INT >= Build.VERSION_CODES.P

            if (isKnow == KNOW) {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                    val notificationLearnPoints = prefs.getNotificationLearnPoints()
                    val userText = (RemoteInput.getResultsFromIntent(intent)?.getCharSequence(KEY_TEXT_REPLY) ?: "").toString().lowercase()
                    val answer = if (languageType == TYPE_PUSH_ENGLISH) word.rus else word.eng
                    //val title = if (languageType == TYPE_PUSH_ENGLISH) word.eng else word.rus
                    val isCorrectAnswer = isCorrectAnswer(userText, answer)

                    if (isCorrectAnswer) {
                        learnStage += notificationLearnPoints
                    } else {
                        learnStage = 0
                    }

                    if (!isCorrectAnswer) isRemove = false
                    if (isCorrectAnswer) {
                        val text = context.getString(if (learnStage < learnStageMax) R.string.answer_correct else R.string.learned)

                        if (isRemovableViaDelay) {
                            //Update notification to cancel it it after that
                            NotificationHelper.showNotification(context, null, text, "", notificationId, true, wordId)
                        } else {
                            showToast(text)
                        }
                    } else {
                        showToast(R.string.answer_incorrect)
                        NotificationHelper.handleWordNotification(context, word, false,
                            NOTIFICATIONS_VIEW_WITH_TRANSLATE, withWrongTitle = true,
                            notificationId = notificationId, isShowAnswer = true, userAnswer = userText)
                    }
                }
            } else {
                learnStage = 0
                isRemove = false

                NotificationHelper.handleWordNotification(context, word, false,
                    NOTIFICATIONS_VIEW_WITH_TRANSLATE, notificationId = notificationId, isShowAnswer = true)
            }

            if (isEnabledSecondaryProgress && languageType == TYPE_PUSH_RUSSIAN) saveWord.learnStageSecondary = learnStage else saveWord.learnStage = learnStage
            repositoryWords.insertNow(saveWord)

            if (isRemove && notificationId != -1) {
                val notificationCompat = NotificationManagerCompat.from(context.applicationContext)

                if (isRemovableViaDelay) {
                    delay(700)
                    notificationCompat.cancel(wordId, notificationId)
                }
                notificationCompat.cancel(wordId, notificationId)
            }

            pendingResult.finish()
        }
    }
}
