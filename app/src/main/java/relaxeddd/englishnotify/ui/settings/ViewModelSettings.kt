package relaxeddd.englishnotify.ui.settings

import android.os.Build
import android.view.View
import android.widget.CompoundButton
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Observer
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch
import relaxeddd.englishnotify.App
import relaxeddd.englishnotify.R
import relaxeddd.englishnotify.common.*
import relaxeddd.englishnotify.model.preferences.SharedHelper
import relaxeddd.englishnotify.model.repository.RepositoryCommon
import relaxeddd.englishnotify.model.repository.RepositoryUser
import relaxeddd.englishnotify.model.repository.RepositoryWord

class ViewModelSettings(private val repositoryUser: RepositoryUser) : ViewModelBase() {

    private val userObserver = Observer<User?> { user ->
        updateSignInVisibility()
        if (user != null && user.email.isNotEmpty()) {
            isShowPrivacyPolicy.value = false
        }
    }
    private val isHideSignInObserver = Observer<Boolean> {
        updateSignInVisibility()
    }
    private val isInitInProgressObserver = Observer<Boolean> {
        updateSignInVisibility()
    }

    val user: LiveData<User?> = repositoryUser.liveDataUser
    private val isHideSignIn = repositoryUser.liveDataHideSignIn
    private val isInitInProgress = repositoryUser.liveDataIsInitInProgress
    var textTheme: String = App.context.resources.getStringArray(R.array.array_themes)[SharedHelper.getAppThemeType()]
    val isVisibleReceiveHelp = Build.VERSION.SDK_INT >= Build.VERSION_CODES.M
    val isOldNavigationDesign = MutableLiveData(SharedHelper.isOldNavigationDesign())
    val isShowProgressInTraining = MutableLiveData(SharedHelper.isShowProgressInTraining())
    val isShowVoiceInput = MutableLiveData(SharedHelper.isShowVoiceInput())
    val isEnableSecondaryProgress = MutableLiveData(SharedHelper.isEnabledSecondaryProgress())
    val isShowGoogleAuth = MutableLiveData(false)
    val isShowPrivacyPolicy = MutableLiveData(!SharedHelper.isPrivacyPolicyConfirmed())
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
    val clickListenerLogout = View.OnClickListener {
        navigateEvent.value = Event(NAVIGATION_DIALOG_CONFIRM_LOGOUT)
    }
    val clickListenerRate = View.OnClickListener {
        navigateEvent.value = Event(NAVIGATION_WEB_PLAY_MARKET)
    }
    val clickListenerInfoOwnWords = View.OnClickListener {
        navigateEvent.value = Event(NAVIGATION_DIALOG_OWN_CATEGORY)
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
    val clickListenerReceiveHelp = View.OnClickListener {
        navigateEvent.value = Event(NAVIGATION_DIALOG_RECEIVE_HELP)
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

    init {
        user.observeForever(userObserver)
        repositoryUser.liveDataHideSignIn.observeForever(isHideSignInObserver)
        isInitInProgress.observeForever(isInitInProgressObserver)
    }

    override fun onCleared() {
        super.onCleared()
        user.removeObserver(userObserver)
        repositoryUser.liveDataHideSignIn.removeObserver(isHideSignInObserver)
        isInitInProgress.removeObserver(isInitInProgressObserver)
    }

    fun onLogoutDialogResult(isConfirmed: Boolean) {
        if (isConfirmed) {
            navigateEvent.value = Event(NAVIGATION_LOADING_SHOW)
            viewModelScope.launch {
                repositoryUser.logout()
                navigateEvent.value = Event(NAVIGATION_LOADING_HIDE)
                navigateEvent.value = Event(NAVIGATION_GOOGLE_LOGOUT)
            }
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

    fun onLogoutResult(isSuccess: Boolean) {
        if (isSuccess) {
            viewModelScope.launch {
                repositoryUser.deleteUserInfo()
            }
        } else {
            showToast(R.string.logout_error)
        }
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

    private fun updateSignInVisibility() {
        isShowGoogleAuth.value = (user.value == null || RepositoryCommon.getInstance().firebaseUser == null) && isHideSignIn.value == true
                && isInitInProgress.value == false
    }
}
