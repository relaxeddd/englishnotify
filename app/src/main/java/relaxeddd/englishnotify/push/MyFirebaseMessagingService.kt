package relaxeddd.englishnotify.push

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import androidx.core.app.NotificationCompat
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
import relaxeddd.englishnotify.model.repository.RepositoryWord
import java.util.*
import kotlin.collections.HashMap
import kotlin.random.Random

class MyFirebaseMessagingService : FirebaseMessagingService() {

    companion object {
        var pushToken: String = ""

        fun handleWordNotification(context: Context, word: Word, isSave: Boolean = true, viewType: Int, withWrongTitle: Boolean = false) {
            val languageType = SharedHelper.getLearnLanguageType(context)

            val wordTitle = getWordTitle(word, languageType)
            val isLongWord = wordTitle.length > 16
            val title = if (withWrongTitle) context.getString(R.string.answer_incorrect) else if (isLongWord) "" else wordTitle

            val notificationText = if (Build.VERSION.SDK_INT < Build.VERSION_CODES.N
                || viewType == SharedHelper.NOTIFICATIONS_VIEW_WITH_TRANSLATE
                || withWrongTitle) {
                getFullNotificationText(word, languageType, !isLongWord && !withWrongTitle)
            } else if (isLongWord) wordTitle else ""

            val isShowButtons = Build.VERSION.SDK_INT >= Build.VERSION_CODES.N
                    && viewType == SharedHelper.NOTIFICATIONS_VIEW_WITH_QUESTION && !withWrongTitle

            if (isSave) {
                val wordDao = AppDatabase.getInstance(context).wordDao()
                val existsWord = wordDao.findWordById(word.eng)

                if (existsWord != null) {
                    existsWord.rus = word.rus
                    existsWord.transcription = word.transcription
                }
                CoroutineScope(Dispatchers.IO).launch {
                    if (existsWord != null) {
                        wordDao.insertAll(existsWord)
                    } else {
                        if (word.timestamp == 0L) {
                            word.timestamp = System.currentTimeMillis()
                        }
                        wordDao.insertAll(word)
                    }
                }
            }

            showNotificationWord(context, word.eng, notificationText, title, isShowButtons)
        }

        private fun showNotificationWord(ctx: Context, wordId: String, text: String, title: String, withButtons : Boolean) {
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

                val notKnowPendingIntent: PendingIntent =
                    PendingIntent.getBroadcast(ctx, Random.nextInt(1000), notKnowIntent, 0)
                notificationBuilder.addAction(R.drawable.ic_close, getAppString(R.string.show_translation), notKnowPendingIntent)
            }

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                notificationBuilder.setSmallIcon(R.drawable.ic_stat_onesignal_default)
                notificationBuilder.color = ContextCompat.getColor(ctx, R.color.colorPrimary)
            } else {
                notificationBuilder.setSmallIcon(R.drawable.ic_stat_onesignal_default)
            }

            val notificationManager = ctx.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager?

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                val channel = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                    NotificationChannel(channelId, ENGLISH_WORDS_NOTIFICATIONS_CHANNEL, NotificationManager.IMPORTANCE_HIGH)
                } else null

                if (channel != null) {
                    notificationManager?.createNotificationChannel(channel)
                }
            } else {
                @Suppress("DEPRECATION")
                notificationBuilder.priority = Notification.PRIORITY_HIGH
            }

            if (SharedHelper.isShowOnlyOneNotification(ctx)) {
                notificationManager?.cancelAll()
            }
            notificationManager?.notify(notificationId, notificationBuilder.build())
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
                notificationBuilder.color = ContextCompat.getColor(ctx, R.color.colorPrimary)
            } else {
                notificationBuilder.setSmallIcon(R.drawable.ic_stat_onesignal_default)
            }

            val notificationManager = ctx.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager?

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                val channel = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                    NotificationChannel(channelId, ENGLISH_WORDS_NOTIFICATIONS_CHANNEL, NotificationManager.IMPORTANCE_HIGH)
                } else null

                if (channel != null) {
                    notificationManager?.createNotificationChannel(channel)
                }
            } else {
                @Suppress("DEPRECATION")
                notificationBuilder.priority = Notification.PRIORITY_HIGH
            }

            notificationManager?.notify(notificationId, notificationBuilder.build())
        }

        private fun getFullNotificationText(word: Word, languageType: Int, withoutWordText: Boolean) : String {
            var notificationText = ""

            if (!withoutWordText) {
                if (languageType == TYPE_PUSH_RUSSIAN && word.type != EXERCISE) {
                    if (word.rus.isNotEmpty()) {
                        notificationText += word.rus + "\n"
                    }
                } else if (word.eng.isNotEmpty()) {
                    notificationText += word.eng + "\n"
                }
            }

            if (word.transcription.isNotEmpty()) {
                notificationText += if (word.type != EXERCISE) "\n[" + word.transcription + "]" + "\n" else "\n" + word.transcription + "\n"
            }

            if (word.v2.isNotEmpty() && word.v3.isNotEmpty()) {
                notificationText += "\n" + word.v2 + " - " + word.v3
            }

            if (languageType == TYPE_PUSH_RUSSIAN && word.type != EXERCISE) {
                if (word.eng.isNotEmpty()) {
                    notificationText += "\n" + word.eng
                }
            } else if (word.rus.isNotEmpty()) {
                notificationText += "\n" + word.rus
            }

            if (word.sampleEng.isNotEmpty()) {
                notificationText += "\n\n" + word.sampleEng
            }
            if (word.sampleRus.isNotEmpty()) {
                notificationText += "\n" + word.sampleRus
            }

            return notificationText
        }

        private fun getWordTitle(word: Word, languageType: Int) : String {
            return if (languageType == TYPE_PUSH_RUSSIAN && word.type != EXERCISE) word.rus else word.eng
        }
    }

    //------------------------------------------------------------------------------------------------------------------
    override fun onNewToken(p0: String?) {
        super.onNewToken(p0)
        pushToken = p0 ?: ""
        if (p0 != null && p0.isNotEmpty()) {
            SharedHelper.setPushToken(p0, this)
        }
    }

    override fun onMessageReceived(remoteMessage: RemoteMessage?) {
        super.onMessageReceived(remoteMessage)

        val data = remoteMessage?.data

        if (data != null) {
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

                    val wordDao = AppDatabase.getInstance(this).wordDao()
                    var words = RepositoryWord.getInstance(wordDao).getOwnWords()
                    val sortByLearnStage = HashMap<Int, ArrayList<Word>>()

                    if (words.isEmpty()) {
                        words = wordDao.getAllItems()
                    }
                    for (word in words) {
                        if (!sortByLearnStage.containsKey(word.learnStage)) {
                            val list = ArrayList<Word>()
                            list.add(word)
                            sortByLearnStage[word.learnStage] = list
                        } else {
                            sortByLearnStage[word.learnStage]?.add(word)
                        }
                    }
                    for (learnStage in 0..2) {
                        if (sortByLearnStage.containsKey(learnStage)) {
                            words = sortByLearnStage[learnStage] ?: ArrayList()
                            break
                        }
                    }

                    if (words.isNotEmpty()) {
                        val wordIx = (0 until words.size).random()

                        if (wordIx >= 0 && wordIx < words.size) {
                            handleWordNotification(this, words[wordIx], false, viewType = SharedHelper.getNotificationsView(this))
                        }
                    }
                }
                PUSH -> {
                    if (isNightTime()) {
                        return
                    }

                    if (data.containsKey(CONTENT) && data[CONTENT] != null) {
                        val word = parseWord(JSONObject(data[CONTENT]))
                        handleWordNotification(this, word, viewType = SharedHelper.getNotificationsView(this))
                    }
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