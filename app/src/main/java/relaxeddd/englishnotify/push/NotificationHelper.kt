package relaxeddd.englishnotify.push

import android.annotation.SuppressLint
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.Build.VERSION
import android.os.Build.VERSION_CODES
import androidx.annotation.WorkerThread
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.core.app.RemoteInput
import androidx.work.ListenableWorker.Result
import relaxeddd.englishnotify.R
import relaxeddd.englishnotify.common.*
import relaxeddd.englishnotify.model.db.AppDatabase
import relaxeddd.englishnotify.model.preferences.SharedHelper
import relaxeddd.englishnotify.ui.main.MainActivity
import kotlin.random.Random

class NotificationHelper {

    companion object {

        @WorkerThread
        fun generateNotification(context: Context) {
            if (isNightTime(context = context) || !SharedHelper.isNotificationsEnabled()) {
                return
            }

            val notificationManager = NotificationManagerCompat.from(context)
            if (!notificationManager.areNotificationsEnabled()) {
                return
            }
            if (VERSION.SDK_INT >= VERSION_CODES.O) {
                val channelId = getAppString(R.string.default_notification_channel_id)
                val channel = notificationManager.getNotificationChannel(channelId)

                if (channel?.importance == NotificationManagerCompat.IMPORTANCE_NONE) {
                    return
                }
            }

            val tag = SharedHelper.getSelectedCategory(context)
            val isEnabledSecondaryProgress = SharedHelper.isEnabledSecondaryProgress(context)
            val languageType = SharedHelper.getLearnLanguageType(context)

            val wordDao = AppDatabase.getInstance(context).wordDao()
            var words = wordDao.getAllItemsNow()
            val sortByLearnStage = HashMap<Int, ArrayList<Word>>()

            words = words.filter { !it.isDeleted && (it.isOwnCategory || tag.isEmpty() || tag == OWN || it.tags.contains(tag)) }

            if (words.isEmpty()) {
                words = words.filter { !it.isDeleted }
            }

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
                    if (SharedHelper.isShowOnlyOneNotification(context)) {
                        notificationManager.cancelAll()
                    }
                    handleWordNotification(
                        context,
                        words[wordIx],
                        isSave = false,
                        viewType = SharedHelper.getNotificationsView(context),
                    )
                }
            }
        }

        @WorkerThread
        fun handleWordNotification(context: Context, word: Word, isSave: Boolean = true, viewType: Int,
                                   withWrongTitle: Boolean = false, notificationId: Int = -1, isShowAnswer: Boolean = false, userAnswer: String = "") {
            val languageType = SharedHelper.getLearnLanguageType(context)
            val isShowTranslation = (languageType == TYPE_PUSH_RUSSIAN && !isShowAnswer || (languageType == TYPE_PUSH_ENGLISH && isShowAnswer))
                    && word.type != EXERCISE

            val wordTitle = getWordTitle(word, isShowTranslation)
            val isLongWord = wordTitle.length > 16
            val title = if (isLongWord) "" else wordTitle

            val notificationText = if (VERSION.SDK_INT < VERSION_CODES.N
                    || viewType == SharedHelper.NOTIFICATIONS_VIEW_WITH_TRANSLATE || withWrongTitle) {
                getFullNotificationText(context, word, isShowTranslation, !isLongWord, withWrongTitle, userAnswer)
            } else if (isLongWord) wordTitle else ""

            val isShowButtons = VERSION.SDK_INT >= VERSION_CODES.N
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

        @SuppressLint("InlinedApi")
        fun showNotificationWord(ctx: Context, wordId: String, text: String, title: String,
                                 withButtons : Boolean, existsNotificationId: Int = -1) {
            val notificationId = if (existsNotificationId != -1) existsNotificationId else Random.nextInt(10000)
            val channelId = getAppString(R.string.default_notification_channel_id)
            val notificationBuilder = NotificationCompat.Builder(ctx, channelId)

            if (withButtons && VERSION.SDK_INT >= VERSION_CODES.N) {
                val knowIntent = Intent(ctx, NotificationAnswerBroadcastReceiver::class.java).apply {
                    action = NotificationAnswerBroadcastReceiver.ACTION_KNOW
                    putExtra(IS_KNOW, NotificationAnswerBroadcastReceiver.KNOW)
                    putExtra(WORD_ID, wordId)
                    putExtra(NOTIFICATION_ID, notificationId)
                }
                val notKnowIntent = Intent(ctx, NotificationAnswerBroadcastReceiver::class.java).apply {
                    action = NotificationAnswerBroadcastReceiver.ACTION_KNOW
                    putExtra(IS_KNOW, NotificationAnswerBroadcastReceiver.NOT_KNOW)
                    putExtra(WORD_ID, wordId)
                    putExtra(NOTIFICATION_ID, notificationId)
                }

                val replyLabel: String = getAppString(R.string.enter_translation)
                val remoteInput: RemoteInput = RemoteInput.Builder(NotificationAnswerBroadcastReceiver.KEY_TEXT_REPLY).run {
                    setLabel(replyLabel)
                    build()
                }
                val replyPendingIntent: PendingIntent = PendingIntent.getBroadcast(ctx, Random.nextInt(10000),
                    knowIntent, PendingIntent.FLAG_UPDATE_CURRENT + PendingIntent.FLAG_MUTABLE)
                val action: NotificationCompat.Action = NotificationCompat.Action.Builder(R.drawable.ic_dictionary,
                    getAppString(R.string.i_know_it), replyPendingIntent)
                    .addRemoteInput(remoteInput)
                    .setAllowGeneratedReplies(false)
                    .build()
                notificationBuilder.addAction(action)
                notificationBuilder.setOngoing(SharedHelper.isOngoing())

                val notKnowPendingIntent: PendingIntent =
                    PendingIntent.getBroadcast(
                        ctx,
                        Random.nextInt(10000),
                        notKnowIntent,
                        PendingIntent.FLAG_UPDATE_CURRENT + PendingIntent.FLAG_MUTABLE
                    )
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
                if (VERSION.SDK_INT < VERSION_CODES.O) {
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

        @SuppressLint("UnspecifiedImmutableFlag")
        private fun createPendingIntent(ctx: Context) : PendingIntent {
            val intent = Intent(ctx, MainActivity::class.java)
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)

            return if (VERSION.SDK_INT >= VERSION_CODES.M) {
                PendingIntent.getActivity(ctx, 0, intent, PendingIntent.FLAG_IMMUTABLE + PendingIntent.FLAG_ONE_SHOT)
            } else {
                PendingIntent.getActivity(ctx, 0, intent, PendingIntent.FLAG_ONE_SHOT)
            }
        }
    }
}
