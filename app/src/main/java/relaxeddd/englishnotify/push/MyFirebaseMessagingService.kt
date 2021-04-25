package relaxeddd.englishnotify.push

import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import androidx.annotation.WorkerThread
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.core.app.RemoteInput
import relaxeddd.englishnotify.R
import relaxeddd.englishnotify.model.db.AppDatabase
import relaxeddd.englishnotify.ui.main.MainActivity
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
import org.json.JSONObject
import relaxeddd.englishnotify.common.*
import relaxeddd.englishnotify.model.preferences.SharedHelper
import java.util.*
import kotlin.collections.HashMap
import kotlin.random.Random

class MyFirebaseMessagingService : FirebaseMessagingService() {

    companion object {
        var pushToken: String = ""

        @WorkerThread
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
                val wordDao = AppDatabase.buildDatabase(context).wordDao()
                val existsWord = wordDao.findWordByIdNow(word.id)

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

                if (existsWord != null) {
                    wordDao.insertNow(existsWord)
                } else {
                    word.timestamp = System.currentTimeMillis()
                    word.isCreatedByUser = false
                    wordDao.insertNow(word)
                }
            }

            showNotificationWord(context, word.id, notificationText, title, isShowButtons, notificationId)
        }

        fun showNotificationWord(ctx: Context, wordId: String, text: String, title: String,
                                 withButtons : Boolean, existsNotificationId: Int = -1) {
            val notificationId = if (existsNotificationId != -1) existsNotificationId else Random.nextInt(10000)
            val channelId = getAppString(R.string.default_notification_channel_id)
            val notificationBuilder = NotificationCompat.Builder(ctx, channelId)

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
                val replyPendingIntent: PendingIntent = PendingIntent.getBroadcast(ctx, Random.nextInt(10000),
                    knowIntent, PendingIntent.FLAG_UPDATE_CURRENT)
                val action: NotificationCompat.Action = NotificationCompat.Action.Builder(R.drawable.ic_dictionary,
                    getAppString(R.string.i_know_it), replyPendingIntent)
                    .addRemoteInput(remoteInput)
                    .setAllowGeneratedReplies(false)
                    .build()
                notificationBuilder.addAction(action)
                notificationBuilder.setOngoing(SharedHelper.isOngoing())

                val notKnowPendingIntent: PendingIntent =
                    PendingIntent.getBroadcast(ctx, Random.nextInt(10000), notKnowIntent, PendingIntent.FLAG_UPDATE_CURRENT)
                notificationBuilder.addAction(R.drawable.ic_close, getAppString(R.string.show_translation), notKnowPendingIntent)
            }

            showNotification(ctx, notificationBuilder, title, text, notificationId, tag = wordId)
        }

        fun showNotification(ctx: Context, notificationBuilder: NotificationCompat.Builder?,
                             title: String, text: String, existsNotificationId: Int = -1, isCancelAfterTimeout: Boolean = false, tag: String? = null) {
            val builder = if (notificationBuilder != null) notificationBuilder else {
                val channelId = ctx.getString(R.string.default_notification_channel_id)
                NotificationCompat.Builder(ctx, channelId)
            }

            builder.apply {
                if (title.isNotEmpty()) {
                    setContentTitle(title)
                }
                if (text.isNotEmpty()) {
                    setContentText(text)
                    setStyle(NotificationCompat.BigTextStyle().bigText(text))
                }
                if (isCancelAfterTimeout) {
                    setTimeoutAfter(500)
                }

                setVisibility(NotificationCompat.VISIBILITY_PUBLIC)
                setAllowSystemGeneratedContextualActions(false)
                //setDefaults(NotificationCompat.DEFAULT_ALL)
                setContentIntent(createPendingIntent(ctx))
                setAutoCancel(true)
                setSmallIcon(R.drawable.ic_stat_onesignal_default)

                /*if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                    color = ContextCompat.getColor(ctx, R.color.notificationTextColor)
                }*/
                if (Build.VERSION.SDK_INT < Build.VERSION_CODES.O) {
                    priority = PushTokenHelper.notificationsPriority
                }

                val notificationId = if (existsNotificationId != -1) existsNotificationId else Random.nextInt(10000)
                NotificationManagerCompat.from(ctx.applicationContext).apply {
                    if (tag != null) {
                        notify(tag, notificationId, build())
                    } else {
                        notify(notificationId, build())
                    }
                }
            }
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

        private fun createPendingIntent(ctx: Context) : PendingIntent {
            val intent = Intent(ctx, MainActivity::class.java)
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
            return PendingIntent.getActivity(ctx, 0, intent, PendingIntent.FLAG_ONE_SHOT)
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

        val notificationManager = NotificationManagerCompat.from(applicationContext)
        if (!notificationManager.areNotificationsEnabled()) {
            return
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channelId = getAppString(R.string.default_notification_channel_id)
            val channel = notificationManager.getNotificationChannel(channelId)

            if (channel?.importance == NotificationManagerCompat.IMPORTANCE_NONE) {
                return
            }
        }

        val data = remoteMessage.data

        when (if (data.containsKey(TYPE)) data[TYPE] else "") {
            SYSTEM -> {
                val title = if (data.containsKey(TITLE)) data[TITLE] ?: "" else ""
                val text = if (data.containsKey(TEXT)) data[TEXT] ?: "" else ""

                showNotification(this, null, title, text)
            }
            OWN_WORD -> {
                if (isNightTime()) {
                    return
                }

                val tag = SharedHelper.getSelectedCategory(this)
                val isEnabledSecondaryProgress = SharedHelper.isEnabledSecondaryProgress(this)
                val languageType = SharedHelper.getLearnLanguageType(this)

                val wordDao = AppDatabase.getInstance(this).wordDao()
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
                        if (SharedHelper.isShowOnlyOneNotification(this)) {
                            notificationManager.cancelAll()
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
                    var word = parseWord(JSONObject(data[CONTENT] ?: ""))

                    if (SharedHelper.isShowOnlyOneNotification(this)) {
                        notificationManager.cancelAll()
                    }

                    val selectedTag = SharedHelper.getSelectedCategory(this)
                    if (SharedHelper.isReceiveOnlyExistWords(this) && selectedTag.isNotEmpty() && word.tags.contains(selectedTag)) {
                        extractRandomWordByTag(selectedTag)?.let {
                            word = it
                        }
                    }

                    handleWordNotification(this, word, viewType = SharedHelper.getNotificationsView(this))
                }
            }
        }
    }

    private fun extractRandomWordByTag(tag: String) : Word? {
        val isEnabledSecondaryProgress = SharedHelper.isEnabledSecondaryProgress(this)
        val languageType = SharedHelper.getLearnLanguageType(this)

        val wordDao = AppDatabase.getInstance(this).wordDao()
        var words = wordDao.getAllItemsNow()
        val sortByLearnStage = HashMap<Int, ArrayList<Word>>()

        words = words.filter { !it.isDeleted && (tag.isEmpty() || tag == OWN || it.tags.contains(tag)) }

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
                return words[wordIx]
            }
        }

        return null
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
