package relaxeddd.englishnotify.ui.settings

import android.os.Build
import android.view.View
import android.widget.CompoundButton
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Observer
import kotlinx.coroutines.launch
import relaxeddd.englishnotify.common.*
import relaxeddd.englishnotify.R
import relaxeddd.englishnotify.model.repository.RepositoryCommon
import relaxeddd.englishnotify.model.repository.RepositoryUser
import relaxeddd.englishnotify.ui.dictionary.AdapterWords

class ViewModelSettings(private val repositoryUser: RepositoryUser) : ViewModelBase() {

    private val userObserver = Observer<User?> { user ->
        var subTime = user?.subscriptionTime ?: System.currentTimeMillis()
        subTime -= System.currentTimeMillis()
        if (subTime < 0) subTime = 0
        liveDataSubDays.value = (subTime / 1000 / 60 / 60 / 24).toString()
    }

    val user: LiveData<User?> = repositoryUser.liveDataUser
    val appLanguageType = MutableLiveData(0)
    val liveDataSubDays = MutableLiveData("")

    val clickListenerLanguage = View.OnClickListener {
        navigateEvent.value = Event(NAVIGATION_DIALOG_LEARN_ENGLISH)
    }
    val clickListenerAppInfo = View.OnClickListener {
        navigateEvent.value = Event(NAVIGATION_DIALOG_APP_ABOUT)
    }
    val clickListenerSubscription = View.OnClickListener {
        navigateEvent.value = Event(NAVIGATION_DIALOG_SUBSCRIPTION)
    }
    val clickListenerSendFeedback = View.OnClickListener {
        navigateEvent.value = Event(NAVIGATION_DIALOG_SEND_FEEDBACK)
    }
    val clickListenerLogout = View.OnClickListener {
        navigateEvent.value = Event(NAVIGATION_DIALOG_CONFIRM_LOGOUT)
    }
    val clickListenerRate = View.OnClickListener {
        navigateEvent.value = Event(NAVIGATION_WEB_PLAY_MARKET)
    }
    val clickListenerInfoTraining = View.OnClickListener {
        navigateEvent.value = Event(NAVIGATION_DIALOG_INFO_TRAINING)
    }
    val checkedChangeListenerLearnStage = CompoundButton.OnCheckedChangeListener { _, isChecked ->
        SharedHelper.setHideLearnStage(!isChecked)
        AdapterWords.isHideLearnStage = !isChecked
    }
    val isShowLearnStage = MutableLiveData<Boolean>(!SharedHelper.isHideLearnStage())

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

    fun onFeedbackDialogResult(feedback: String) {
        uiScope.launch {
            RepositoryCommon.getInstance().sendFeedback(feedback)
        }
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
}