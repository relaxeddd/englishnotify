package relaxeddd.pushenglish.ui.main

import android.content.Context
import android.view.View
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Observer
import relaxeddd.pushenglish.App
import relaxeddd.pushenglish.model.repository.RepositoryUser
import kotlinx.coroutines.launch
import relaxeddd.pushenglish.R
import relaxeddd.pushenglish.common.*

class ViewModelMain(private val repositoryUser: RepositoryUser) : ViewModelBase() {
    val user = repositoryUser.liveDataUser
    val isShowWarningNotifications = MutableLiveData<Boolean>(false)
    val isShowWarningAuthorize = MutableLiveData<Boolean>(false)

    private val userObserver = Observer<User?> { user ->
        isShowWarningNotifications.value = user == null || user.receiveNotifications == false
        isShowWarningAuthorize.value = user == null || repositoryUser.firebaseUser == null
    }

    val clickListenerWarningNotifications = View.OnClickListener {
        navigateEvent.value =
                Event(NAVIGATION_FRAGMENT_NOTIFICATIONS)
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
        val sPref = App.context.getSharedPreferences(LOGIN_DATA, Context.MODE_PRIVATE)
        val isConfirmed = sPref.getBoolean(PRIVACY_POLICY_CONFIRMED, false)

        if (isConfirmed) {
            requestInitUser()
        }
    }

    fun onViewResume() {
        val sPref = App.context.getSharedPreferences(LOGIN_DATA, Context.MODE_PRIVATE)
        val isConfirmed = sPref.getBoolean(PRIVACY_POLICY_CONFIRMED, false)

        if (!isConfirmed) {
            navigateEvent.value = Event(NAVIGATION_DIALOG_PRIVACY_POLICY)
        }
    }

    fun onPrivacyPolicyConfirmedResult(isConfirmed: Boolean) {
        if (isConfirmed) {
            val sPref = App.context.getSharedPreferences(LOGIN_DATA, Context.MODE_PRIVATE)
            sPref.edit().putBoolean(PRIVACY_POLICY_CONFIRMED, true).apply()
            requestInitUser()
        } else {
            navigateEvent.value = Event(NAVIGATION_EXIT)
        }
    }

    fun requestInitUser() {
        if (repositoryUser.isAuthorized()) {
            ioScope.launch {
                repositoryUser.initUser()
            }
        }
    }
}