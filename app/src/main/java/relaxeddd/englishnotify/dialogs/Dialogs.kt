package relaxeddd.englishnotify.dialogs

import relaxeddd.englishnotify.R
import relaxeddd.englishnotify.view_base.dialog.DialogSimpleChoice
import relaxeddd.englishnotify.view_base.dialog.DialogSimpleInfo
import relaxeddd.englishnotify.view_base.dialog.DialogSingleChoice

class DialogPatchNotes : DialogSimpleInfo() {

    override val titleResId: Int = R.string.new_version
    override val textResId: Int = R.string.patch_notes
}

//----------------------------------------------------------------------------------------------------------------------
class DialogVoiceInput : DialogSimpleChoice() {

    override val textResId: Int = R.string.voice_input_error
    override val positiveButtonTextResId: Int = R.string.hide
    override val negativeButtonTextResId: Int = R.string.no
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

class DialogNewVersion : DialogSimpleChoice() {

    override val titleResId: Int = R.string.update
    override val textResId: Int = R.string.update_text
}

class DialogRateApp : DialogSimpleChoice() {

    override val textResId: Int = R.string.rate_app_question
    override val positiveButtonTextResId: Int = R.string.rate
    override val negativeButtonTextResId: Int = R.string.no
}

class DialogRestoreWord : DialogSimpleChoice() {

    override val titleResId: Int = R.string.word_already_exists
    override val positiveButtonTextResId: Int = R.string.reset_progress
}

//----------------------------------------------------------------------------------------------------------------------
class DialogLearnLanguage : DialogSingleChoice() {

    override val arrayResId: Int = R.array.array_learn_language
    override val titleResId: Int = R.string.notifications_language
}

class DialogNotificationsView : DialogSingleChoice() {

    override val arrayResId: Int = R.array.array_notifications_view
    override val titleResId: Int = R.string.notifications_view
}
