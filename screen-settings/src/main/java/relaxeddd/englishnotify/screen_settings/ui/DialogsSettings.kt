package relaxeddd.englishnotify.screen_settings.ui

import relaxeddd.englishnotify.screen_settings.BuildConfig
import relaxeddd.englishnotify.screen_settings.R
import relaxeddd.englishnotify.view_base.dialog.DialogSimpleChoice
import relaxeddd.englishnotify.view_base.dialog.DialogSimpleInfo
import relaxeddd.englishnotify.view_base.dialog.DialogSingleChoice

class DialogAppAbout : DialogSimpleInfo() {

    override val titleResId: Int = R.string.about_app
    override val textResId: Int = R.string.text_app_about
    override val arg: String = BuildConfig.VERSION_NAME
}

class DialogSecondaryProgressInfo : DialogSimpleInfo() {

    override val titleResId: Int = R.string.secondary_word_progress
    override val textResId: Int = R.string.secondary_word_progress_info
}

class DialogInfoReceiveHelp : DialogSimpleInfo() {

    override val titleResId: Int = R.string.background_start
    override val textResId: Int = R.string.text_dialog_receive_help
}

class DialogInfoTraining : DialogSimpleInfo() {

    override val titleResId: Int = R.string.words_training
    override val textResId: Int = R.string.text_info_words_training
}

class DialogSwapProgress : DialogSimpleChoice() {

    override val textResId: Int = R.string.swap_main_and_secondary_progress_text
    override val positiveButtonTextResId: Int = R.string.confirm
}

class DialogTrueAnswersToLearn : DialogSingleChoice() {

    override val arrayResId: Int = R.array.array_true_answers_number_to_learn
    override val titleResId: Int = R.string.true_answers_number_to_learn_desc
}

class DialogNotificationLearnPoints : DialogSingleChoice() {

    override val arrayResId: Int = R.array.array_notifications_learn_points
    override val titleResId: Int = R.string.notification_learn_points_desc
}

class DialogAppTheme : DialogSingleChoice() {

    override val arrayResId: Int = R.array.array_themes
    override val titleResId: Int = R.string.app_theme
}
