package relaxeddd.englishnotify.ui.settings

import android.view.View
import android.widget.CompoundButton
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch
import relaxeddd.englishnotify.App
import relaxeddd.englishnotify.R
import relaxeddd.englishnotify.common.*
import relaxeddd.englishnotify.model.preferences.SharedHelper
import relaxeddd.englishnotify.model.repository.RepositoryWord

class ViewModelSettings : ViewModelBase() {

    var textTheme: String = App.context.resources.getStringArray(R.array.array_themes)[SharedHelper.getAppThemeType()]
    val isOldNavigationDesign = MutableLiveData(SharedHelper.isOldNavigationDesign())
    val isShowProgressInTraining = MutableLiveData(SharedHelper.isShowProgressInTraining())
    val isShowVoiceInput = MutableLiveData(SharedHelper.isShowVoiceInput())
    val isEnableSecondaryProgress = MutableLiveData(SharedHelper.isEnabledSecondaryProgress())
    val textTrueAnswersToLearn = MutableLiveData(SharedHelper.getTrueAnswersToLearn().toString())
    val textNotificationsLearnPoints = MutableLiveData(SharedHelper.getNotificationLearnPoints().toString())
    
    val clickListenerAppInfo = View.OnClickListener {
        navigateEvent.value = Event(NAVIGATION_DIALOG_APP_ABOUT)
    }
    val clickListenerUpdatesHistory = View.OnClickListener {
        navigateEvent.value = Event(NAVIGATION_DIALOG_PATCH_NOTES)
    }
    val clickListenerSecondaryProgressInfo = View.OnClickListener {
        navigateEvent.value = Event(NAVIGATION_DIALOG_SECONDARY_PROGRESS_INFO)
    }
    val clickListenerRate = View.OnClickListener {
        navigateEvent.value = Event(NAVIGATION_WEB_PLAY_MARKET)
    }
    val clickListenerInfoTraining = View.OnClickListener {
        navigateEvent.value = Event(NAVIGATION_DIALOG_INFO_TRAINING)
    }
    val clickListenerSwapProgress = View.OnClickListener {
        if (SharedHelper.isEnabledSecondaryProgress()) {
            navigateEvent.value = Event(NAVIGATION_DIALOG_SWAP_PROGRESS)
        } else {
            showToast(R.string.need_enable_secondary_progress)
        }
    }
    val clickListenerStatistic = View.OnClickListener {
        navigateEvent.value = Event(NAVIGATION_FRAGMENT_STATISTIC)
    }
    val clickListenerAddMultipleWords = View.OnClickListener {
        navigateEvent.value = Event(NAVIGATION_FRAGMENT_PARSE)
    }
    val clickListenerTheme = View.OnClickListener {
        navigateEvent.value = Event(NAVIGATION_DIALOG_THEME)
    }
    val clickListenerTrueAnswersToLearn = View.OnClickListener {
        navigateEvent.value = Event(NAVIGATION_DIALOG_TRUE_ANSWERS_TO_LEARN)
    }
    val clickListenerNotificationLearnPoints = View.OnClickListener {
        navigateEvent.value = Event(NAVIGATION_DIALOG_NOTIFICATION_LEARN_POINTS)
    }
    val clickListenerGoogleAuth = View.OnClickListener {
        if (!isNetworkAvailable()) {
            showToast(getAppString(R.string.network_not_available))
            return@OnClickListener
        }
        navigateEvent.value = Event(NAVIGATION_GOOGLE_AUTH)
    }
    var checkedChangeListenerNavigationDesign = CompoundButton.OnCheckedChangeListener { _, isChecked ->
        if (SharedHelper.isOldNavigationDesign() != isChecked && isOldNavigationDesign.value != isChecked) {
            SharedHelper.setOldNavigationDesign(isChecked)
            isOldNavigationDesign.value = isChecked
            navigateEvent.value = Event(NAVIGATION_RECREATE_ACTIVITY)
        }
    }
    var checkedChangeListenerProgressInTraining = CompoundButton.OnCheckedChangeListener { _, isChecked ->
        if (SharedHelper.isShowProgressInTraining() != isChecked && isShowProgressInTraining.value != isChecked) {
            SharedHelper.setShowProgressInTraining(isChecked)
            isShowProgressInTraining.value = isChecked
        }
    }
    var checkedChangeListenerVoiceInput = CompoundButton.OnCheckedChangeListener { _, isChecked ->
        if (SharedHelper.isShowVoiceInput() != isChecked && isShowVoiceInput.value != isChecked) {
            SharedHelper.setShowVoiceInput(isChecked)
            isShowVoiceInput.value = isChecked
        }
    }
    var checkedChangeListenerEnabledSecondaryProgress = CompoundButton.OnCheckedChangeListener { _, isChecked ->
        if (SharedHelper.isEnabledSecondaryProgress() != isChecked) {
            SharedHelper.setEnabledSecondaryProgress(isChecked)
            isEnableSecondaryProgress.value = isChecked
        }
    }

    fun onSwapProgressResult(isConfirmed: Boolean) {
        if (isConfirmed) {
            viewModelScope.launch {
                RepositoryWord.getInstance().swapProgress()
                showToast(R.string.progress_swapped)
            }
        }
    }

    fun onThemeUpdate(themeIx: Int) {
        textTheme = App.context.resources.getStringArray(R.array.array_themes)[themeIx]
        SharedHelper.setAppThemeType(themeIx)
    }

    fun onDialogTrueAnswersToLearnResult(result: Int) {
        val value = App.context.resources.getStringArray(R.array.array_true_answers_number_to_learn)[result]
        textTrueAnswersToLearn.value = value
        SharedHelper.setTrueAnswersToLearn(value.toInt())
    }

    fun onDialogNotificationLearnPointsResult(result: Int) {
        val value = App.context.resources.getStringArray(R.array.array_notifications_learn_points)[result]
        textNotificationsLearnPoints.value = value
        SharedHelper.setNotificationLearnPoints(value.toInt())
    }
}
