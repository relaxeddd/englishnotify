package relaxeddd.pushenglish.ui.settings

import android.view.View
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import kotlinx.coroutines.launch
import relaxeddd.pushenglish.R
import relaxeddd.pushenglish.common.*
import relaxeddd.pushenglish.model.repository.RepositoryUser

class ViewModelSettings(private val repositoryUser: RepositoryUser) : ViewModelBase() {

    val user: LiveData<User?> = repositoryUser.liveDataUser
    val appLanguageType = MutableLiveData(0)

    val clickListenerLanguage = View.OnClickListener {}
    val clickListenerAppInfo = View.OnClickListener {
        navigateEvent.value = Event(NAVIGATION_DIALOG_APP_ABOUT)
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

    fun onLogoutDialogResult(isConfirmed: Boolean) {
        if (isConfirmed) {
            navigateEvent.value = Event(NAVIGATION_GOOGLE_LOGOUT)
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