package relaxeddd.englishnotify.ui.main

import android.view.View
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Observer
import relaxeddd.englishnotify.model.repository.RepositoryUser
import kotlinx.coroutines.launch
import relaxeddd.englishnotify.common.*
import relaxeddd.englishnotify.R
import relaxeddd.englishnotify.dialogs.DialogPatchNotes
import relaxeddd.englishnotify.model.repository.RepositoryCommon
import relaxeddd.englishnotify.model.repository.RepositoryWord

class ViewModelMain(private val repositoryUser: RepositoryUser) : ViewModelBase() {

    val user = repositoryUser.liveDataUser
    val isShowWarningNotifications = MutableLiveData<Boolean>(false)
    val isShowGoogleAuth = MutableLiveData<Boolean>(false)
    val isShowWarningSubscription = MutableLiveData<Boolean>(false)
    val isShowLoading = MutableLiveData<Boolean>(false)
    val isShowHorizontalProgress = MutableLiveData<Boolean>(false)
    val isVisibleSecondaryBottomNavigationView = MutableLiveData<Boolean>(true)
    private var isRateDialogShown = false
    private var isFirstLoad = true

    private val userObserver = Observer<User?> { user ->
        isShowGoogleAuth.value = user == null || RepositoryCommon.getInstance().firebaseUser == null
        isShowWarningNotifications.value = isShowGoogleAuth.value == false && (user == null || user.receiveNotifications == false)
        isShowWarningSubscription.value = user != null && user.subscriptionTime <= System.currentTimeMillis()

        val launchCount = SharedHelper.getLaunchCount()
        if (user != null && !isRateDialogShown && !SharedHelper.isCancelledRateDialog() && launchCount % 3 == 0) {
            isRateDialogShown = true
            navigateEvent.value = Event(NAVIGATION_DIALOG_RATE_APP)
        }
        if (user != null) {
            if (user.email.isNotEmpty()) {
                SharedHelper.setPrivacyPolicyConfirmed(true)
            }
            navigateEvent.value = Event(NAVIGATION_INIT_BILLING)
            if (isFirstLoad) {
                updateOwnWords()
                isFirstLoad = false
            }
        }
    }
    private val actualVersionObserver = Observer<Boolean> { isActualVersion ->
        if (!isActualVersion) {
            navigateEvent.value = Event(NAVIGATION_DIALOG_NEW_VERSION)
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
    val clickListenerLoading = View.OnClickListener {}

    init {
        repositoryUser.liveDataUser.observeForever(userObserver)
        repositoryUser.liveDataIsActualVersion.observeForever(actualVersionObserver)
    }

    override fun onCleared() {
        super.onCleared()
        repositoryUser.liveDataUser.removeObserver(userObserver)
    }

    fun onViewCreate() {
        requestInitUser()

        if (!SharedHelper.isPatchNotesViewed(DialogPatchNotes.VERSION)) {
            navigateEvent.value = Event(NAVIGATION_DIALOG_PATCH_NOTES)
            SharedHelper.setPatchNotesViewed(DialogPatchNotes.VERSION)
        }
    }

    fun onViewResume() {}

    fun requestInitUser() {
        if (repositoryUser.isAuthorized()) {
            isShowWarningNotifications.value = false
            isShowGoogleAuth.value = false
            isShowWarningSubscription.value = false
            isShowHorizontalProgress.value = true
            ioScope.launch {
                repositoryUser.initUser(object: ListenerResult<Boolean> {
                    override fun onResult(result: Boolean) {
                        if (!result) {
                            userObserver.onChanged(null)
                        }
                        isShowHorizontalProgress.value = false
                    }
                })
            }
        }
    }

    private fun updateOwnWords() {
        ioScope.launch {
            val words = RepositoryWord.getInstance().words.value
            if (words?.isEmpty() == true) {
                repositoryUser.requestOwnWords()
            }
        }
    }
}