package relaxeddd.englishnotify.push

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.graphics.BitmapFactory
import android.graphics.Typeface
import android.os.Build
import android.text.Spannable
import android.text.SpannableString
import android.text.style.StyleSpan
import androidx.core.app.NotificationCompat
import androidx.core.app.RemoteInput
import androidx.core.content.ContextCompat
import relaxeddd.englishnotify.R
import relaxeddd.englishnotify.model.db.AppDatabase
import relaxeddd.englishnotify.ui.main.MainActivity
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
import relaxeddd.englishnotify.common.*
import java.util.*
import kotlin.random.Random

class MyFirebaseMessagingService : FirebaseMessagingService() {

    companion object {
        var pushToken: String = ""

        fun showNotificationWord(ctx: Context, wordId: String, text: String, title: String = getString(R.string.app_name), withButtons : Boolean) {
            val notificationId = Random.nextInt(1000)
            val intent = Intent(ctx, MainActivity::class.java)
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
            val pendingIntent = PendingIntent.getActivity(ctx, 0, intent, PendingIntent.FLAG_ONE_SHOT)

            val spannableTitle = SpannableString(title)
            spannableTitle.setSpan(StyleSpan(Typeface.BOLD), 0, spannableTitle.length, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)

            val channelId = getString(R.string.default_notification_channel_id)
            val notificationBuilder = NotificationCompat.Builder(ctx, channelId)
                .setStyle(NotificationCompat.BigTextStyle().bigText(text))
                .setContentTitle(spannableTitle)
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
                } else {
                    val knowPendingIntent: PendingIntent =
                        PendingIntent.getBroadcast(ctx, Random.nextInt(1000), knowIntent, 0)
                    notificationBuilder.addAction(R.drawable.ic_accept, getString(R.string.i_know_it), knowPendingIntent)
                }

                val dontKnowPendingIntent: PendingIntent =
                    PendingIntent.getBroadcast(ctx, Random.nextInt(1000), dontKnowIntent, 0)
                notificationBuilder.addAction(R.drawable.ic_close, getString(R.string.i_dont_know_it), dontKnowPendingIntent)
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
                    NotificationChannel(channelId, title, NotificationManager.IMPORTANCE_DEFAULT)
                } else null

                if (channel != null) notificationManager.createNotificationChannel(channel)
            }

            notificationManager.notify(notificationId, notificationBuilder.build())
        }

        fun showNotification(ctx: Context, title: String = getString(R.string.app_name), text: String) {
            val notificationId = Random.nextInt(1000)
            val intent = Intent(ctx, MainActivity::class.java)
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
            val pendingIntent = PendingIntent.getActivity(ctx, 0, intent, PendingIntent.FLAG_ONE_SHOT)

            val spannableTitle = SpannableString(title)
            spannableTitle.setSpan(StyleSpan(Typeface.BOLD), 0, spannableTitle.length, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)

            val channelId = getString(R.string.default_notification_channel_id)
            val notificationBuilder = NotificationCompat.Builder(ctx, channelId)
                .setLargeIcon(BitmapFactory.decodeResource(ctx.resources, R.drawable.attention))
                .setStyle(NotificationCompat.BigTextStyle().bigText(text))
                .setContentTitle(spannableTitle)
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
                    NotificationChannel(channelId, title, NotificationManager.IMPORTANCE_DEFAULT)
                } else null

                if (channel != null) notificationManager.createNotificationChannel(channel)
            }

            notificationManager.notify(notificationId, notificationBuilder.build())
        }

        fun getFullNotificationText(word: Word, languageType: Int) : String {
            var notificationText = ""

            if (word.transcription.isNotEmpty()) {
                notificationText += "[" + word.transcription + "]"
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
                notificationText += "\n"
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
            SharedHelper.setPushToken(p0)
        }
    }

    override fun onMessageReceived(remoteMessage: RemoteMessage?) {
        val data = remoteMessage?.data

        if (data != null) {
            val type = if (data.containsKey(TYPE)) data[TYPE] else ""

            when (type) {
                SYSTEM -> {
                    val title = if (data.containsKey(TITLE)) data[TITLE] ?: "" else ""
                    val text = if (data.containsKey(TEXT)) data[TEXT] ?: "" else ""

                    showNotification(this, title, text)
                }
                else -> {
                    val startHour = SharedHelper.getStartHour()
                    val durationHours = SharedHelper.getDurationHours()
                    val endHour = if (startHour + durationHours >= 24) startHour + durationHours - 24 else startHour + durationHours
                    val calendar = Calendar.getInstance()
                    val currentHour = calendar.get(Calendar.HOUR_OF_DAY)

                    if (durationHours != 0 && ((currentHour in startHour..(endHour - 1))
                                || (startHour + durationHours >= 24 && currentHour < endHour) )) {
                        return
                    }

                    val words = parseWords(data)
                    if (words.isNotEmpty()) {
                        handleWordsNotification(words)
                    }
                }
            }
        }
    }

    private fun handleWordsNotification(words : List<Word>) {
        val languageType = SharedHelper.getLearnLanguageType()
        val word = chooseWord(words)

        if (word != null) {
            val title = if (languageType == TYPE_PUSH_ENGLISH) word.eng else word.rus
            val isShowButtons = Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP
                    && SharedHelper.getNotificationsView() == SharedHelper.NOTIFICATIONS_VIEW_WITH_QUESTION
            val notificationText = if (Build.VERSION.SDK_INT < Build.VERSION_CODES.LOLLIPOP
                || SharedHelper.getNotificationsView() == SharedHelper.NOTIFICATIONS_VIEW_WITH_TRANSLATE) {
                getFullNotificationText(word, languageType)
            } else ""

            AppDatabase.getInstance(this).wordDao().insertAll(word)

            showNotificationWord(this, word.eng, notificationText, title, isShowButtons)
        }
    }

    private fun chooseWord(words: List<Word>) : Word? {
        if (words.isEmpty()) {
            return null
        }

        val userTags = SharedHelper.getSelectedTags()
        val wordsCount = words.size
        val tagsCount = userTags.size
        val tagIx = Random.nextInt(tagsCount)
        val choosenTag = userTags.toList()[tagIx]

        for (word in words) {
            if (word.tags.contains(choosenTag)) {
                return word
            }
        }

        return words[Random.nextInt(wordsCount)]
    }
}