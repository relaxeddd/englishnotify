package relaxeddd.englishnotify.dialogs

import android.app.Dialog
import android.os.Bundle
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.DialogFragment
import relaxeddd.englishnotify.BuildConfig
import relaxeddd.englishnotify.R
import relaxeddd.englishnotify.common.*

class DialogSecondaryProgressInfo : DialogSimpleInfo() {

    override val titleResId: Int = R.string.secondary_word_progress
    override val textResId: Int = R.string.secondary_word_progress_info
}

class DialogAppAbout : DialogSimpleInfo() {

    override val titleResId: Int = R.string.about_app
    override val textResId: Int = R.string.text_app_about
    override val arg: String = BuildConfig.VERSION_NAME
}

class DialogInfoReceiveHelp : DialogSimpleInfo() {

    override val titleResId: Int = R.string.background_start
    override val textResId: Int = R.string.text_dialog_receive_help
}

class DialogInfoTraining : DialogSimpleInfo() {

    override val titleResId: Int = R.string.words_training
    override val textResId: Int = R.string.text_info_words_training
}

class DialogOwnCategory : DialogSimpleInfo() {

    override val titleResId: Int = R.string.own_category
    override val textResId: Int = R.string.text_own_category
}

class DialogPatchNotes : DialogSimpleInfo() {

    override val titleResId: Int = R.string.new_version
    override val textResId: Int = R.string.patch_notes
}

abstract class DialogSimpleInfo : DialogFragment() {

    abstract val titleResId: Int
    open val textResId: Int = EMPTY_RES
    open val positiveButtonTextResId: Int = android.R.string.ok
    open val arg: String = ""

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val builder = AlertDialog.Builder(requireContext())

        builder.setTitle(titleResId)
            .setPositiveButton(positiveButtonTextResId) { _, _ -> }
        if (textResId != EMPTY_RES) {
            if (arg.isEmpty()) {
                builder.setMessage(textResId)
            } else {
                builder.setMessage(getString(textResId, arg))
            }
        }

        return builder.create()
    }
}

//----------------------------------------------------------------------------------------------------------------------
class DialogSubscriptionInfo : DialogSimpleChoice() {

    override val titleResId: Int = R.string.sub_advantages
    override val textResId: Int = R.string.sub_advantages_info
    override val positiveButtonTextResId: Int = R.string.list
    override val negativeButtonTextResId: Int = EMPTY_RES
}

class DialogVoiceInput : DialogSimpleChoice() {

    override val textResId: Int = R.string.voice_input_error
    override val positiveButtonTextResId: Int = R.string.hide
    override val negativeButtonTextResId: Int = R.string.no
}

class DialogSwapProgress : DialogSimpleChoice() {

    override val textResId: Int = R.string.swap_main_and_secondary_progress_text
    override val positiveButtonTextResId: Int = R.string.confirm
}

class DialogConfirmDisableNotifications : DialogSimpleChoice() {

    override val textResId: Int = R.string.do_you_really_want_to_off_notifications
    override val positiveButtonTextResId: Int = R.string.yes
    override val negativeButtonTextResId: Int = R.string.no
}

class DialogConfirmLogout : DialogSimpleChoice() {

    override val textResId: Int = R.string.do_you_really_want_to_logout
    override val positiveButtonTextResId: Int = R.string.yes
    override val negativeButtonTextResId: Int = R.string.no
}

class DialogDeleteWords : DialogSimpleChoice() {

    override val textResId: Int = R.string.do_you_really_want_to_delete_words
    override val positiveButtonTextResId: Int = R.string.yes
    override val negativeButtonTextResId: Int = R.string.no
}

class DialogLikeApp : DialogSimpleChoice() {

    override val textResId: Int = R.string.you_like_app
    override val positiveButtonTextResId: Int = R.string.yes
    override val negativeButtonTextResId: Int = R.string.no
    override val isCanBeCancelled: Boolean = false
}

class DialogNewVersion : DialogSimpleChoice() {

    override val titleResId: Int = R.string.update
    override val textResId: Int = R.string.update_text
}

class DialogRateApp : DialogSimpleChoice() {

    override val titleResId: Int = R.string.rate_app_question
    override val positiveButtonTextResId: Int = R.string.rate
    override val negativeButtonTextResId: Int = R.string.no
}

class DialogRestoreWord : DialogSimpleChoice() {

    override val titleResId: Int = R.string.word_already_exists
    override val positiveButtonTextResId: Int = R.string.reset_progress
}

class DialogNeedSubscription : DialogSimpleChoice() {

    override val textResId: Int = R.string.need_subscription_desc
    override val positiveButtonTextResId: Int = R.string.sub_advantages
    override val negativeButtonTextResId: Int = android.R.string.ok
}

abstract class DialogSimpleChoice : DialogFragment() {

    open val titleResId: Int = EMPTY_RES
    open val textResId: Int = EMPTY_RES
    open val positiveButtonTextResId: Int = android.R.string.ok
    open val negativeButtonTextResId: Int = R.string.cancel
    open val isCanBeCancelled: Boolean = true

    var confirmListener: ListenerResult<Boolean>? = null

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        isCancelable = isCanBeCancelled

        val builder = AlertDialog.Builder(requireContext())

        builder.setPositiveButton(positiveButtonTextResId) { _, _ ->
                confirmListener?.onResult(true)
            }
        if (negativeButtonTextResId != EMPTY_RES) {
            builder.setNegativeButton(negativeButtonTextResId) { _, _ ->
                confirmListener?.onResult(false)
            }
        }
        if (titleResId != EMPTY_RES) {
            builder.setTitle(titleResId)
        }
        if (textResId != EMPTY_RES) {
            builder.setMessage(getString(textResId))
        }
        if (titleResId == EMPTY_RES && textResId == EMPTY_RES) {
            builder.setTitle("")
        }

        return builder.create()
    }
}

//----------------------------------------------------------------------------------------------------------------------
class DialogAppTheme : DialogSingleChoice() {

    override val arrayResId: Int = R.array.array_themes
    override val titleResId: Int = R.string.app_theme
}

class DialogLearnLanguage : DialogSingleChoice() {

    override val arrayResId: Int = R.array.array_learn_language
    override val titleResId: Int = R.string.notifications_language
}

class DialogNotificationsView : DialogSingleChoice() {

    override val arrayResId: Int = R.array.array_notifications_view
    override val titleResId: Int = R.string.notifications_view
}

abstract class DialogSingleChoice : DialogFragment() {

    abstract val arrayResId: Int
    abstract val titleResId: Int
    open val positiveButtonTextResId: Int = android.R.string.ok
    open val negativeButtonTextResId: Int = R.string.cancel

    private var selectedItemIx: Int = 0
    var listener: ListenerResult<Int>? = null

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val builder = AlertDialog.Builder(requireContext())
        selectedItemIx = arguments?.getInt(SELECTED_ITEM, 0) ?: 0

        builder.setTitle(titleResId)
            .setSingleChoiceItems(arrayResId, selectedItemIx) { _, which ->
                selectedItemIx = which
            }.setPositiveButton(positiveButtonTextResId) { _, _ ->
                listener?.onResult(selectedItemIx)
            }.setNegativeButton(negativeButtonTextResId, null)

        return builder.create()
    }
}
