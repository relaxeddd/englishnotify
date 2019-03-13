package relaxeddd.englishnotify.ui.main

import android.view.View
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Observer
import relaxeddd.englishnotify.model.repository.RepositoryUser
import kotlinx.coroutines.launch
import relaxeddd.englishnotify.common.*
import relaxeddd.englishnotify.R
import relaxeddd.englishnotify.model.repository.RepositoryCommon
import java.util.*

class ViewModelMain(private val repositoryUser: RepositoryUser) : ViewModelBase() {

    val user = repositoryUser.liveDataUser
    val isShowWarningNotifications = MutableLiveData<Boolean>(false)
    val isShowWarningAuthorize = MutableLiveData<Boolean>(false)
    val isShowWarningSubscription = MutableLiveData<Boolean>(false)
    var authTimer: Timer? = null

    private val userObserver = Observer<User?> { user ->
        authTimer?.cancel()

        if (SharedHelper.isPrivacyPolicyConfirmed() && (user == null || RepositoryCommon.getInstance().firebaseUser == null)) {
            authTimer = Timer()
            authTimer?.schedule(object: TimerTask() {
                override fun run() {
                    uiScope.launch {
                        isShowWarningAuthorize.value = user == null || RepositoryCommon.getInstance().firebaseUser == null
                        isShowWarningNotifications.value = isShowWarningAuthorize.value == false && (user == null || user.receiveNotifications == false)
                    }
                }
            }, 5000)
        } else {
            isShowWarningAuthorize.value = user == null || RepositoryCommon.getInstance().firebaseUser == null
            isShowWarningNotifications.value = isShowWarningAuthorize.value == false && (user == null || user.receiveNotifications == false)
            isShowWarningSubscription.value = user != null && user.subscriptionTime <= System.currentTimeMillis()
        }
    }

    val clickListenerWarningNotifications = View.OnClickListener {
        navigateEvent.value = Event(NAVIGATION_FRAGMENT_NOTIFICATIONS)
    }
    val clickListenerWarningSubscription = View.OnClickListener {
        navigateEvent.value = Event(NAVIGATION_FRAGMENT_SETTINGS)
    }
    val clickListenerGoogleAuth = View.OnClickListener {
        if (!isNetworkAvailable()) {
            showToast(getString(R.string.network_not_available))
            return@OnClickListener
        }
        navigateEvent.value = Event(NAVIGATION_GOOGLE_AUTH)
    }

    init {
        repositoryUser.liveDataUser.observeForever(userObserver)
    }

    override fun onCleared() {
        super.onCleared()
        repositoryUser.liveDataUser.removeObserver(userObserver)
    }

    fun onViewCreate() {
        val isConfirmed = SharedHelper.isPrivacyPolicyConfirmed()

        if (isConfirmed) {
            requestInitUser()
        }
    }

    fun onViewResume() {
        val isConfirmed = SharedHelper.isPrivacyPolicyConfirmed()

        if (!isConfirmed) {
            navigateEvent.value = Event(NAVIGATION_DIALOG_PRIVACY_POLICY)
        }
    }

    fun onPrivacyPolicyConfirmedResult(isConfirmed: Boolean) {
        if (isConfirmed) {
            SharedHelper.setPrivacyPolicyConfirmed(true)
            requestInitUser()
        } else {
            navigateEvent.value = Event(NAVIGATION_EXIT)
        }
    }

    fun requestInitUser() {
        isShowWarningNotifications.value = false
        isShowWarningAuthorize.value = false
        isShowWarningSubscription.value = false
        if (repositoryUser.isAuthorized()) {
            ioScope.launch {
                repositoryUser.initUser()
            }
        }
    }
}