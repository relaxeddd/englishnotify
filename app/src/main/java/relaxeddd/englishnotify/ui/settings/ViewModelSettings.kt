package relaxeddd.englishnotify.ui.settings

import android.content.Context
import android.view.View
import android.widget.CompoundButton
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch
import relaxeddd.englishnotify.R
import relaxeddd.englishnotify.common.*
import relaxeddd.englishnotify.domain_words.repository.RepositoryWords
import relaxeddd.englishnotify.preferences.Preferences
import relaxeddd.englishnotify.view_base.ViewModelBase
import relaxeddd.englishnotify.view_base.models.Event
import javax.inject.Inject

class ViewModelSettings @Inject constructor(
    private val context: Context,
    private val prefs: Preferences,
    private val repositoryWords: RepositoryWords,
) : ViewModelBase() {

    var textTheme = MutableLiveData(context.resources.getStringArray(R.array.array_themes)[prefs.getAppThemeType()])
    val isBottomNavigation = MutableLiveData(prefs.isBottomNavigation())
    val isShowProgressInTraining = MutableLiveData(prefs.isShowProgressInTraining())
    val isShowVoiceInput = MutableLiveData(prefs.isShowVoiceInput())
    val isEnableSecondaryProgress = MutableLiveData(prefs.isEnabledSecondaryProgress())
    val textTrueAnswersToLearn = MutableLiveData(prefs.getTrueAnswersToLearn().toString())
    val textNotificationsLearnPoints = MutableLiveData(prefs.getNotificationLearnPoints().toString())
    
    val clickListenerAppInfo = View.OnClickListener {
        navigateEvent.value = Event(NAVIGATION_DIALOG_APP_ABOUT)
    }
    val clickListenerUpdatesHistory = View.OnClickListener {
        navigateEvent.value = Event(NAVIGATION_DIALOG_PATCH_NOTES)
    }
    val clickListenerSecondaryProgressInfo = View.OnClickListener {
        navigateEvent.value = Event(NAVIGATION_DIALOG_SECONDARY_PROGRESS_INFO)
    }
    val clickListenerRateApp = View.OnClickListener {
        navigateEvent.value = Event(NAVIGATION_WEB_PLAY_MARKET)
    }
    val clickListenerInfoTraining = View.OnClickListener {
        navigateEvent.value = Event(NAVIGATION_DIALOG_INFO_TRAINING)
    }
    val clickListenerSwapProgress = View.OnClickListener {
        if (prefs.isEnabledSecondaryProgress()) {
            navigateEvent.value = Event(NAVIGATION_DIALOG_SWAP_PROGRESS)
        } else {
            showToast(context, R.string.need_enable_secondary_progress)
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
    var checkedChangeListenerNavigationDesign = CompoundButton.OnCheckedChangeListener { _, isChecked ->
        if (prefs.isBottomNavigation() != isChecked && isBottomNavigation.value != isChecked) {
            prefs.setBottomNavigation(isChecked)
            isBottomNavigation.value = isChecked
            navigateEvent.value = Event(NAVIGATION_RECREATE_ACTIVITY)
        }
    }
    var checkedChangeListenerProgressInTraining = CompoundButton.OnCheckedChangeListener { _, isChecked ->
        if (prefs.isShowProgressInTraining() != isChecked && isShowProgressInTraining.value != isChecked) {
            prefs.setShowProgressInTraining(isChecked)
            isShowProgressInTraining.value = isChecked
        }
    }
    var checkedChangeListenerVoiceInput = CompoundButton.OnCheckedChangeListener { _, isChecked ->
        if (prefs.isShowVoiceInput() != isChecked && isShowVoiceInput.value != isChecked) {
            prefs.setShowVoiceInput(isChecked)
            isShowVoiceInput.value = isChecked
        }
    }
    var checkedChangeListenerEnabledSecondaryProgress = CompoundButton.OnCheckedChangeListener { _, isChecked ->
        if (prefs.isEnabledSecondaryProgress() != isChecked) {
            prefs.setEnabledSecondaryProgress(isChecked)
            isEnableSecondaryProgress.value = isChecked
        }
    }

    fun onSwapProgressResult(isConfirmed: Boolean) {
        if (isConfirmed) {
            viewModelScope.launch {
                repositoryWords.swapProgress()
                showToast(context, R.string.progress_swapped)
            }
        }
    }

    fun onThemeUpdate(themeIx: Int) {
        textTheme.value = context.resources.getStringArray(R.array.array_themes)[themeIx]
        prefs.setAppThemeType(themeIx)
    }

    fun onDialogTrueAnswersToLearnResult(result: Int) {
        val value = context.resources.getStringArray(R.array.array_true_answers_number_to_learn)[result]
        textTrueAnswersToLearn.value = value
        prefs.setTrueAnswersToLearn(value.toInt())
    }

    fun onDialogNotificationLearnPointsResult(result: Int) {
        val value = context.resources.getStringArray(R.array.array_notifications_learn_points)[result]
        textNotificationsLearnPoints.value = value
        prefs.setNotificationLearnPoints(value.toInt())
    }
}
