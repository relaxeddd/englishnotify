package relaxeddd.englishnotify.push

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.graphics.BitmapFactory
import android.graphics.Typeface
import android.media.RingtoneManager
import android.os.Build
import android.text.Spannable
import android.text.SpannableString
import android.text.style.StyleSpan
import androidx.core.app.NotificationCompat
import androidx.core.content.ContextCompat
import relaxeddd.englishnotify.R
import relaxeddd.englishnotify.model.db.AppDatabase
import relaxeddd.englishnotify.ui.main.MainActivity
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
import relaxeddd.englishnotify.common.SharedHelper
import relaxeddd.englishnotify.common.Word
import relaxeddd.englishnotify.common.parseWords
import kotlin.random.Random

class MyFirebaseMessagingService : FirebaseMessagingService() {

    companion object {
        var pushToken: String = ""
    }


    override fun onNewToken(p0: String?) {
        super.onNewToken(p0)
        pushToken = p0 ?: ""
    }

    override fun onMessageReceived(remoteMessage: RemoteMessage?) {
        val data = remoteMessage?.data

        if (data != null) {
            val words = parseWords(data)
            val word = chooseWord(words)
            AppDatabase.getInstance(this).wordDao().insertAll(word)
            var notificationText = "\n"

            if (word.transcription.isNotEmpty()) {
                notificationText += "[" + word.transcription + "]"
            }
            if (word.v2.isNotEmpty() && word.v3.isNotEmpty()) {
                if (notificationText.isNotEmpty()) {
                    notificationText += "\n"
                }
                notificationText += word.v2 + " - " + word.v3
            }
            if (word.rus.isNotEmpty()) {
                notificationText += "\n"
                notificationText += "\n"
                notificationText += word.rus
            }
            if (word.sampleEng.isNotEmpty()) {
                notificationText += "\n"
                notificationText += "\n" + word.sampleEng
            }
            if (word.sampleRus.isNotEmpty()) {
                notificationText += "\n"
                notificationText += "\n" + word.sampleRus
            }

            AppDatabase.getInstance(this).wordDao().insertAll(word)
            showNotification(notificationText, word.eng)
        }
    }

    fun showNotification(text: String, title: String = getString(R.string.app_name)) {
        val intent = Intent(this, MainActivity::class.java)
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
        val pendingIntent = PendingIntent.getActivity(this, 0, intent, PendingIntent.FLAG_ONE_SHOT)

        val spannableTitle = SpannableString(title)
        spannableTitle.setSpan(StyleSpan(Typeface.BOLD), 0, spannableTitle.length, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)

        val channelId = getString(R.string.default_notification_channel_id)
        val defaultSoundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION)
        val notificationBuilder = NotificationCompat.Builder(this, channelId)
            .setLargeIcon(BitmapFactory.decodeResource(resources, R.drawable.attention))
            .setStyle(NotificationCompat.BigTextStyle().bigText(text))
            .setContentTitle(spannableTitle)
            .setAutoCancel(true)
            .setSound(defaultSoundUri)
            .setContentIntent(pendingIntent)

        if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            notificationBuilder.setSmallIcon(R.drawable.info)
            notificationBuilder.color = ContextCompat.getColor(this, R.color.colorPrimary)
        } else {
            notificationBuilder.setSmallIcon(R.drawable.info)
        }

        val notificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                NotificationChannel(channelId, title, NotificationManager.IMPORTANCE_DEFAULT)
            } else null

            if (channel != null) notificationManager.createNotificationChannel(channel)
        }

        notificationManager.notify(Random.nextInt(1000), notificationBuilder.build())
    }

    private fun chooseWord(words: List<Word>) : Word {
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