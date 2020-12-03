package relaxeddd.englishnotify.push

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.core.app.RemoteInput
import androidx.core.content.ContextCompat
import relaxeddd.englishnotify.R
import relaxeddd.englishnotify.model.db.AppDatabase
import relaxeddd.englishnotify.ui.main.MainActivity
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import org.json.JSONObject
import relaxeddd.englishnotify.common.*
import relaxeddd.englishnotify.model.preferences.SharedHelper
import relaxeddd.englishnotify.model.repository.RepositoryWord
import java.util.*
import kotlin.collections.HashMap
import kotlin.random.Random

class MyFirebaseMessagingService : FirebaseMessagingService() {

    companion object {
        var pushToken: String = ""

        fun handleWordNotification(context: Context, word: Word, isSave: Boolean = true, viewType: Int,
                                   withWrongTitle: Boolean = false, notificationId: Int = -1, isShowAnswer: Boolean = false, userAnswer: String = "") {
            val languageType = SharedHelper.getLearnLanguageType(context)
            val isShowTranslation = (languageType == TYPE_PUSH_RUSSIAN && !isShowAnswer || (languageType == TYPE_PUSH_ENGLISH && isShowAnswer))
                    && word.type != EXERCISE

            val wordTitle = getWordTitle(word, isShowTranslation)
            val isLongWord = wordTitle.length > 16
            val title = if (isLongWord) "" else wordTitle

            val notificationText = if (Build.VERSION.SDK_INT < Build.VERSION_CODES.N
                    || viewType == SharedHelper.NOTIFICATIONS_VIEW_WITH_TRANSLATE || withWrongTitle) {
                getFullNotificationText(context, word, isShowTranslation, !isLongWord, withWrongTitle, userAnswer)
            } else if (isLongWord) wordTitle else ""

            val isShowButtons = Build.VERSION.SDK_INT >= Build.VERSION_CODES.N
                    && viewType == SharedHelper.NOTIFICATIONS_VIEW_WITH_QUESTION && !withWrongTitle

            if (isSave) {
                val repositoryWord = RepositoryWord.getInstance()
                val wordDao = AppDatabase.getInstance(context).wordDao()
                val existsWord = wordDao.findWordById(word.id)

                if (existsWord != null) {
                    existsWord.eng = word.eng
                    existsWord.rus = word.rus
                    existsWord.transcription = word.transcription
                    existsWord.tags = word.tags
                    existsWord.isDeleted = false
                    existsWord.timestamp = System.currentTimeMillis()
                    if (existsWord.isCreatedByUser) {
                        existsWord.isCreatedByUser = false
                    }
                }
                CoroutineScope(Dispatchers.IO).launch {
                    if (existsWord != null) {
                        repositoryWord.insertWord(existsWord, wordDao)
                    } else {
                        word.timestamp = System.currentTimeMillis()
                        word.isCreatedByUser = false
                        repositoryWord.insertWord(word, wordDao)
                    }
                }
            }

            showNotificationWord(context, word.id, notificationText, title, isShowButtons, notificationId)
        }

        fun showNotificationWord(ctx: Context, wordId: String, text: String, title: String,
                                 withButtons : Boolean, existsNotificationId: Int = -1) {
            val notificationId = if (existsNotificationId != -1) existsNotificationId else Random.nextInt(10000)
            val intent = Intent(ctx, MainActivity::class.java)
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
            val pendingIntent = PendingIntent.getActivity(ctx, 0, intent, PendingIntent.FLAG_ONE_SHOT)

            val channelId = getAppString(R.string.default_notification_channel_id)
            val notificationBuilder = NotificationCompat.Builder(ctx, channelId)
                .setAutoCancel(true)
                .setContentIntent(pendingIntent)

            if (title.isNotEmpty()) {
                notificationBuilder.setContentTitle(title)
            }
            if (text.isNotEmpty()) {
                notificationBuilder.setContentText(text)
                notificationBuilder.setStyle(NotificationCompat.BigTextStyle().bigText(text))
            }

            if (withButtons && Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                val knowIntent = Intent(ctx, PushBroadcastReceiver::class.java).apply {
                    action = PushBroadcastReceiver.ACTION_KNOW
                    putExtra(IS_KNOW, PushBroadcastReceiver.KNOW)
                    putExtra(WORD_ID, wordId)
                    putExtra(NOTIFICATION_ID, notificationId)
                }
                val notKnowIntent = Intent(ctx, PushBroadcastReceiver::class.java).apply {
                    action = PushBroadcastReceiver.ACTION_KNOW
                    putExtra(IS_KNOW, PushBroadcastReceiver.NOT_KNOW)
                    putExtra(WORD_ID, wordId)
                    putExtra(NOTIFICATION_ID, notificationId)
                }

                val replyLabel: String = getAppString(R.string.enter_translation)
                val remoteInput: RemoteInput = RemoteInput.Builder(PushBroadcastReceiver.KEY_TEXT_REPLY).run {
                    setLabel(replyLabel)
                    build()
                }
                val replyPendingIntent: PendingIntent = PendingIntent.getBroadcast(ctx, Random.nextInt(1000),
                    knowIntent, PendingIntent.FLAG_UPDATE_CURRENT)
                val action: NotificationCompat.Action = NotificationCompat.Action.Builder(R.drawable.ic_dictionary,
                    getAppString(R.string.i_know_it), replyPendingIntent)
                    .addRemoteInput(remoteInput)
                    .build()
                notificationBuilder.addAction(action)
                notificationBuilder.setOngoing(SharedHelper.isOngoing())

                val notKnowPendingIntent: PendingIntent =
                    PendingIntent.getBroadcast(ctx, Random.nextInt(1000), notKnowIntent, 0)
                notificationBuilder.addAction(R.drawable.ic_close, getAppString(R.string.show_translation), notKnowPendingIntent)
            }

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                notificationBuilder.setSmallIcon(R.drawable.ic_stat_onesignal_default)
                notificationBuilder.color = ContextCompat.getColor(ctx, R.color.notificationTextColor)
            } else {
                notificationBuilder.setSmallIcon(R.drawable.ic_stat_onesignal_default)
            }

            val notificationManager = NotificationManagerCompat.from(ctx.applicationContext)

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                val channel = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                    NotificationChannel(channelId, ENGLISH_WORDS_NOTIFICATIONS_CHANNEL, NotificationManager.IMPORTANCE_HIGH)
                } else null

                if (channel != null) {
                    notificationManager.createNotificationChannel(channel)
                }
            } else {
                @Suppress("DEPRECATION")
                notificationBuilder.priority = Notification.PRIORITY_HIGH
            }

            notificationManager.notify(wordId, notificationId, notificationBuilder.build())
        }

        private fun showNotification(ctx: Context, title: String, text: String) {
            val notificationId = Random.nextInt(10000)
            val intent = Intent(ctx, MainActivity::class.java)
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
            val pendingIntent = PendingIntent.getActivity(ctx, 0, intent, PendingIntent.FLAG_ONE_SHOT)

            val channelId = getAppString(R.string.default_notification_channel_id)
            val notificationBuilder = NotificationCompat.Builder(ctx, channelId)
                .setAutoCancel(true)
                .setContentIntent(pendingIntent)

            if (title.isNotEmpty()) {
                notificationBuilder.setContentTitle(title)
            }
            if (text.isNotEmpty()) {
                notificationBuilder.setContentText(text)
                notificationBuilder.setStyle(NotificationCompat.BigTextStyle().bigText(text))
            }

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                notificationBuilder.setSmallIcon(R.drawable.ic_stat_onesignal_default)
                notificationBuilder.color = ContextCompat.getColor(ctx, R.color.notificationTextColor)
            } else {
                notificationBuilder.setSmallIcon(R.drawable.ic_stat_onesignal_default)
            }

            val notificationManager = NotificationManagerCompat.from(ctx.applicationContext)

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                val channel = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                    NotificationChannel(channelId, ENGLISH_WORDS_NOTIFICATIONS_CHANNEL, NotificationManager.IMPORTANCE_HIGH)
                } else null

                if (channel != null) {
                    notificationManager.createNotificationChannel(channel)
                }
            } else {
                @Suppress("DEPRECATION")
                notificationBuilder.priority = Notification.PRIORITY_HIGH
            }

            notificationManager.notify(notificationId, notificationBuilder.build())
        }

        private fun getFullNotificationText(context: Context, word: Word, isShowTranslation: Boolean, withoutWordText: Boolean,
                                            isIncorrectAnswer: Boolean, userAnswer: String) : String {
            var notificationText = ""

            if (!withoutWordText) {
                if (isShowTranslation) {
                    if (word.rus.isNotEmpty()) {
                        notificationText += word.rus
                    }
                } else if (word.eng.isNotEmpty()) {
                    notificationText += word.eng
                }
            }

            if (word.transcription.isNotEmpty()) {
                if (notificationText.isNotEmpty()) notificationText += "\n"
                notificationText += if (word.type != EXERCISE) "[" + word.transcription + "]" else word.transcription
            }

            if (word.v2.isNotEmpty() && word.v3.isNotEmpty()) {
                if (notificationText.isNotEmpty()) notificationText += "\n"
                notificationText += word.v2 + " - " + word.v3
            }

            if (isShowTranslation) {
                if (word.eng.isNotEmpty()) {
                    if (!isIncorrectAnswer) notificationText += "\n"
                    if (notificationText.isNotEmpty()) notificationText += "\n"
                    notificationText += word.eng
                }
            } else if (word.rus.isNotEmpty()) {
                if (!isIncorrectAnswer) notificationText += "\n"
                if (notificationText.isNotEmpty()) notificationText += "\n"
                notificationText += word.rus
            }

            if (word.sampleEng.isNotEmpty()) {
                notificationText += "\n\n" + word.sampleEng
            }
            if (word.sampleRus.isNotEmpty()) {
                notificationText += "\n" + word.sampleRus
            }
            if (isIncorrectAnswer && userAnswer.isNotEmpty()) {
                if (notificationText.isNotEmpty()) notificationText += "\n\n"
                notificationText += context.getString(R.string.your_answer, userAnswer)
            }

            return notificationText
        }

        private fun getWordTitle(word: Word, isShowTranslation: Boolean) : String {
            return if (isShowTranslation) word.rus else word.eng
        }
    }

    //------------------------------------------------------------------------------------------------------------------
    override fun onNewToken(p0: String) {
        super.onNewToken(p0)
        pushToken = p0
        if (p0.isNotEmpty()) {
            SharedHelper.setPushToken(p0, this)
        }
    }

    override fun onMessageReceived(remoteMessage: RemoteMessage) {
        super.onMessageReceived(remoteMessage)

        val data = remoteMessage.data

        when (if (data.containsKey(TYPE)) data[TYPE] else "") {
            SYSTEM -> {
                val title = if (data.containsKey(TITLE)) data[TITLE] ?: "" else ""
                val text = if (data.containsKey(TEXT)) data[TEXT] ?: "" else ""

                showNotification(this, title, text)
            }
            OWN_WORD -> {
                if (isNightTime()) {
                    return
                }

                val tag = SharedHelper.getSelectedCategory(this)
                val isEnabledSecondaryProgress = SharedHelper.isEnabledSecondaryProgress(this)
                val languageType = SharedHelper.getLearnLanguageType(this)

                val wordDao = AppDatabase.getInstance(this).wordDao()
                var words = wordDao.getAllItems()
                val sortByLearnStage = HashMap<Int, ArrayList<Word>>()

                words = words.filter { !it.isDeleted && it.isOwnCategory && (tag.isEmpty() || it.tags.contains(tag)) }

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
                        if (SharedHelper.isShowOnlyOneNotification(this)) {
                            NotificationManagerCompat.from(applicationContext).cancelAll()
                        }
                        handleWordNotification(this, words[wordIx], false, viewType = SharedHelper.getNotificationsView(this))
                    }
                }
            }
            PUSH -> {
                if (isNightTime()) {
                    return
                }

                if (data.containsKey(CONTENT) && data[CONTENT] != null) {
                    val word = parseWord(JSONObject(data[CONTENT] ?: ""))

                    if (SharedHelper.isShowOnlyOneNotification(this)) {
                        NotificationManagerCompat.from(applicationContext).cancelAll()
                    }
                    handleWordNotification(this, word, viewType = SharedHelper.getNotificationsView(this))
                }
            }
        }
    }

    private fun isNightTime() : Boolean {
        val startHour = SharedHelper.getStartHour(this)
        val durationHours = SharedHelper.getDurationHours(this)
        val endHour = if (startHour + durationHours >= 24) startHour + durationHours - 24 else startHour + durationHours
        val calendar = Calendar.getInstance()
        val currentHour = calendar.get(Calendar.HOUR_OF_DAY)

        return durationHours != 0 && ((currentHour in startHour until endHour)
                || (startHour + durationHours >= 24 && currentHour < endHour) )
    }
}
