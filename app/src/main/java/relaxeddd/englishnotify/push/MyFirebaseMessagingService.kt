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
import org.json.JSONObject
import relaxeddd.englishnotify.common.*
import relaxeddd.englishnotify.model.repository.RepositoryWord
import java.util.*
import kotlin.collections.HashMap
import kotlin.random.Random

class MyFirebaseMessagingService : FirebaseMessagingService() {

    companion object {
        var pushToken: String = ""

        fun showNotificationWord(ctx: Context, wordId: String, text: String, title: String = getString(R.string.app_name), withButtons : Boolean) {
            val notificationId = Random.nextInt(1000)
            val intent = Intent(ctx, MainActivity::class.java)
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
            val pendingIntent = PendingIntent.getActivity(ctx, 0, intent, PendingIntent.FLAG_ONE_SHOT)

            val channelId = getString(R.string.default_notification_channel_id)
            val notificationBuilder = NotificationCompat.Builder(ctx, channelId)
                .setContentTitle(title)
                .setContentText(text)
                .setStyle(NotificationCompat.BigTextStyle().bigText(text))
                .setAutoCancel(true)
                .setContentIntent(pendingIntent)

            if (withButtons && Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                val knowIntent = Intent(ctx, PushBroadcastReceiver::class.java).apply {
                    action = PushBroadcastReceiver.ACTION_KNOW
                    putExtra(IS_KNOW, PushBroadcastReceiver.KNOW)
                    putExtra(WORD_ID, wordId)
                    putExtra(NOTIFICATION_ID, notificationId)
                }
                val dontKnowIntent = Intent(ctx, PushBroadcastReceiver::class.java).apply {
                    action = PushBroadcastReceiver.ACTION_KNOW
                    putExtra(IS_KNOW, PushBroadcastReceiver.DONT_KNOW)
                    putExtra(WORD_ID, wordId)
                    putExtra(NOTIFICATION_ID, notificationId)
                }

                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                    val replyLabel: String = getString(R.string.enter_translation)
                    val remoteInput: RemoteInput = RemoteInput.Builder(PushBroadcastReceiver.KEY_TEXT_REPLY).run {
                        setLabel(replyLabel)
                        build()
                    }
                    val replyPendingIntent: PendingIntent = PendingIntent.getBroadcast(ctx, Random.nextInt(1000),
                        knowIntent, PendingIntent.FLAG_UPDATE_CURRENT)
                    val action: NotificationCompat.Action = NotificationCompat.Action.Builder(R.drawable.ic_dictionary,
                        getString(R.string.i_know_it), replyPendingIntent)
                            .addRemoteInput(remoteInput)
                            .build()
                    notificationBuilder.addAction(action)
                }

                val dontKnowPendingIntent: PendingIntent =
                    PendingIntent.getBroadcast(ctx, Random.nextInt(1000), dontKnowIntent, 0)
                notificationBuilder.addAction(R.drawable.ic_close, getString(R.string.show_translation), dontKnowPendingIntent)
            }

            if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                notificationBuilder.setSmallIcon(R.drawable.ic_stat_onesignal_default)
                notificationBuilder.color = ContextCompat.getColor(ctx, R.color.colorPrimary)
            } else {
                notificationBuilder.setSmallIcon(R.drawable.ic_stat_onesignal_default)
            }

            val notificationManager = ctx.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                val channel = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                    NotificationChannel(channelId, ENGLISH_WORDS_NOTIFICATIONS_CHANNEL, NotificationManager.IMPORTANCE_HIGH)
                } else null

                if (channel != null) {
                    notificationManager.createNotificationChannel(channel)
                }
            } else {
                notificationBuilder.priority = Notification.PRIORITY_HIGH
            }

            notificationManager.notify(notificationId, notificationBuilder.build())
        }

        fun showNotification(ctx: Context, title: String = getString(R.string.app_name), text: String) {
            val notificationId = Random.nextInt(1000)
            val intent = Intent(ctx, MainActivity::class.java)
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
            val pendingIntent = PendingIntent.getActivity(ctx, 0, intent, PendingIntent.FLAG_ONE_SHOT)

            val channelId = getString(R.string.default_notification_channel_id)
            val notificationBuilder = NotificationCompat.Builder(ctx, channelId)
                .setContentTitle(title)
                .setContentText(text)
                .setStyle(NotificationCompat.BigTextStyle().bigText(text))
                .setAutoCancel(true)
                .setContentIntent(pendingIntent)

            if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                notificationBuilder.setSmallIcon(R.drawable.ic_stat_onesignal_default)
                notificationBuilder.color = ContextCompat.getColor(ctx, R.color.colorPrimary)
            } else {
                notificationBuilder.setSmallIcon(R.drawable.ic_stat_onesignal_default)
            }

            val notificationManager = ctx.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                val channel = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                    NotificationChannel(channelId, ENGLISH_WORDS_NOTIFICATIONS_CHANNEL, NotificationManager.IMPORTANCE_HIGH)
                } else null

                if (channel != null) {
                    notificationManager.createNotificationChannel(channel)
                }
            } else {
                notificationBuilder.priority = Notification.PRIORITY_HIGH
            }

            notificationManager.notify(notificationId, notificationBuilder.build())
        }

        fun getFullNotificationText(word: Word, languageType: Int) : String {
            var notificationText = ""

            if (languageType == TYPE_PUSH_ENGLISH && word.rus.isNotEmpty()) {
                notificationText += word.eng + "\n"
            } else if (languageType == TYPE_PUSH_RUSSIAN && word.eng.isNotEmpty()) {
                notificationText += word.rus + "\n"
            }
            if (word.transcription.isNotEmpty()) {
                notificationText += "\n[" + word.transcription + "]"
            }
            if (word.v2.isNotEmpty() && word.v3.isNotEmpty()) {
                if (notificationText.isNotEmpty()) {
                    notificationText += "\n"
                }
                notificationText += word.v2 + " - " + word.v3
            }
            if (languageType == TYPE_PUSH_ENGLISH && word.rus.isNotEmpty()) {
                notificationText += "\n"
                notificationText += word.rus
            } else if (languageType == TYPE_PUSH_RUSSIAN && word.eng.isNotEmpty()) {
                notificationText += "\n"
                notificationText += word.eng
            }
            if (word.sampleEng.isNotEmpty()) {
                notificationText += "\n"
                notificationText += "\n" + word.sampleEng
            }
            if (word.sampleRus.isNotEmpty()) {
                notificationText += "\n" + word.sampleRus
            }

            return notificationText
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
            val type = if (data.containsKey(TYPE)) data[TYPE] else ""

            when (type) {
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
                            handleWordNotification(words[wordIx], false)
                        }
                    }
                }
                PUSH -> {
                    if (isNightTime()) {
                        return
                    }

                    if (data.containsKey(CONTENT) && data[CONTENT] != null) {
                        val word = parseWord(JSONObject(data[CONTENT]))
                        handleWordNotification(word)
                    }
                }
            }
        }
    }

    private fun handleWordNotification(word: Word, isSave: Boolean = true) {
        val languageType = SharedHelper.getLearnLanguageType(this)
        val isShowButtons = Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP
                && SharedHelper.getNotificationsView(this) == SharedHelper.NOTIFICATIONS_VIEW_WITH_QUESTION
        var notificationText = ""
        var title = getStringByResName(SharedHelper.getSelectedCategory(this))

        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.LOLLIPOP
            || SharedHelper.getNotificationsView(this) == SharedHelper.NOTIFICATIONS_VIEW_WITH_TRANSLATE) {
            notificationText = getFullNotificationText(word, languageType)
        } else {
            notificationText = if (languageType == TYPE_PUSH_ENGLISH) word.eng else word.rus
        }

        if (isSave) {
            val wordDao = AppDatabase.getInstance(this).wordDao()
            val existsWord = wordDao.findWordById(word.eng)

            if (existsWord == null) {
                wordDao.insertAll(word)
            } else if (existsWord.saveType == Word.DICTIONARY) {
                word.learnStage = existsWord.learnStage
                wordDao.insertAll(word)
            }
        }

        showNotificationWord(this, word.eng, notificationText, title, isShowButtons)
    }

    private fun isNightTime() : Boolean {
        val startHour = SharedHelper.getStartHour(this)
        val durationHours = SharedHelper.getDurationHours(this)
        val endHour = if (startHour + durationHours >= 24) startHour + durationHours - 24 else startHour + durationHours
        val calendar = Calendar.getInstance()
        val currentHour = calendar.get(Calendar.HOUR_OF_DAY)

        return durationHours != 0 && ((currentHour in startHour..(endHour - 1))
                || (startHour + durationHours >= 24 && currentHour < endHour) )
    }
}