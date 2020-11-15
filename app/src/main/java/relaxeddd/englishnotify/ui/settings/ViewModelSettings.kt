package relaxeddd.englishnotify.ui.settings

import android.os.Build
import android.view.View
import android.widget.CompoundButton
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Observer
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import relaxeddd.englishnotify.App
import relaxeddd.englishnotify.common.*
import relaxeddd.englishnotify.R
import relaxeddd.englishnotify.model.preferences.SharedHelper
import relaxeddd.englishnotify.model.repository.RepositoryUser
import relaxeddd.englishnotify.model.repository.RepositoryWord

class ViewModelSettings(private val repositoryUser: RepositoryUser) : ViewModelBase() {

    private val userObserver = Observer<User?> { user ->
        var subTime = user?.subscriptionTime ?: System.currentTimeMillis()
        subTime -= System.currentTimeMillis()
        if (subTime < 0) subTime = 0
        liveDataSubDays.value = (subTime / 1000 / 60 / 60 / 24).toString()

        val savedWordsString = getAppString(R.string.words_saved, user?.savedWordsCount?.toString() ?: "0")
        textWordsSaved.value = savedWordsString
    }

    val user: LiveData<User?> = repositoryUser.liveDataUser
    val liveDataSubDays = MutableLiveData("")
    var textTheme: String = App.context.resources.getStringArray(R.array.array_themes)[SharedHelper.getAppThemeType()]
    val isVisibleReceiveHelp = Build.VERSION.SDK_INT >= Build.VERSION_CODES.M
    val isOldNavigationDesign = MutableLiveData(SharedHelper.isOldNavigationDesign())
    val isShowVoiceInput = MutableLiveData(SharedHelper.isShowVoiceInput())
    val isEnableSecondaryProgress = MutableLiveData(SharedHelper.isEnabledSecondaryProgress())
    val textWordsSaved = MutableLiveData("")
    val textTrueAnswersToLearn = MutableLiveData(SharedHelper.getTrueAnswersToLearn().toString())
    val textNotificationsLearnPoints = MutableLiveData(SharedHelper.getNotificationLearnPoints().toString())
    
    val clickListenerAppInfo = View.OnClickListener {
        navigateEvent.value = Event(NAVIGATION_DIALOG_APP_ABOUT)
    }
    val clickListenerSubscription = View.OnClickListener {
        navigateEvent.value = Event(NAVIGATION_DIALOG_SUBSCRIPTION)
    }
    val clickListenerSubscriptionInfo = View.OnClickListener {
        navigateEvent.value = Event(NAVIGATION_DIALOG_SUBSCRIPTION_INFO)
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
    val clickListenerTheme = View.OnClickListener {
        navigateEvent.value = Event(NAVIGATION_DIALOG_THEME)
    }
    val clickListenerSaveDictionary = View.OnClickListener {
        val user = repositoryUser.liveDataUser.value

        if (user == null) {
            showToast(R.string.please_authorize)
            return@OnClickListener
        }
        if (!user.isSubscribed()) {
            navigateEvent.value = Event(NAVIGATION_DIALOG_SUBSCRIPTION_REQUIRED)
            return@OnClickListener
        }
        if (RepositoryWord.getInstance().words.value?.isEmpty() == true) {
            showToast(getErrorString(RESULT_ERROR_SAVE_WORDS_EMPTY))
            return@OnClickListener
        }

        navigateEvent.value = Event(NAVIGATION_DIALOG_CHECK_SAVE_WORDS)
    }
    val clickListenerLoadDictionary = View.OnClickListener {
        val user = repositoryUser.liveDataUser.value

        if (user == null) {
            showToast(R.string.please_authorize)
            return@OnClickListener
        }
        if (!user.isSubscribed()) {
            navigateEvent.value = Event(NAVIGATION_DIALOG_SUBSCRIPTION_REQUIRED)
            return@OnClickListener
        }
        /*if (user.savedWordsCount == 0) {
            showToast(getErrorString(RESULT_ERROR_LOAD_WORDS_EMPTY))
            return@OnClickListener
        }*/

        navigateEvent.value = Event(NAVIGATION_DIALOG_CHECK_LOAD_WORDS)
    }
    val clickListenerTrueAnswersToLearn = View.OnClickListener {
        navigateEvent.value = Event(NAVIGATION_DIALOG_TRUE_ANSWERS_TO_LEARN)
    }
    val clickListenerNotificationLearnPoints = View.OnClickListener {
        navigateEvent.value = Event(NAVIGATION_DIALOG_NOTIFICATION_LEARN_POINTS)
    }
    var checkedChangeListenerNavigationDesign = CompoundButton.OnCheckedChangeListener { _, isChecked ->
        if (SharedHelper.isOldNavigationDesign() != isChecked && isOldNavigationDesign.value != isChecked) {
            SharedHelper.setOldNavigationDesign(isChecked)
            isOldNavigationDesign.value = isChecked
            navigateEvent.value = Event(NAVIGATION_RECREATE_ACTIVITY)
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
    }

    override fun onCleared() {
        super.onCleared()
        user.removeObserver(userObserver)
    }

    fun onLogoutDialogResult(isConfirmed: Boolean) {
        if (isConfirmed) {
            navigateEvent.value = Event(NAVIGATION_GOOGLE_LOGOUT)
        }
    }

    fun onSwapProgressResult(isConfirmed: Boolean) {
        if (isConfirmed) {
            RepositoryWord.getInstance().swapProgress {
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
            ioScope.launch {
                repositoryUser.deleteUserInfo()
            }
        } else {
            showToast(R.string.logout_error)
        }
    }

    fun saveDictionary() {
        navigateEvent.value = Event(NAVIGATION_LOADING_SHOW)
        ioScope.launch {
            delay(1000)
            val isSuccess = RepositoryWord.getInstance().requestSaveAllWords()
            uiScope.launch { navigateEvent.value = Event(NAVIGATION_LOADING_HIDE) }
            if (isSuccess) {
                showToast(R.string.words_saved_success)
            }
        }
    }

    fun loadDictionary() {
        navigateEvent.value = Event(NAVIGATION_LOADING_SHOW)
        ioScope.launch {
            delay(1000)
            val isSuccess = RepositoryWord.getInstance().requestLoadAllWords()
            uiScope.launch { navigateEvent.value = Event(NAVIGATION_LOADING_HIDE) }
            if (isSuccess) {
                showToast(R.string.words_loaded)
            }
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
}
