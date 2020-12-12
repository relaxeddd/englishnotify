package relaxeddd.englishnotify.ui.main

import android.view.View
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Observer
import com.google.firebase.auth.FirebaseAuth
import relaxeddd.englishnotify.model.repository.RepositoryUser
import kotlinx.coroutines.launch
import relaxeddd.englishnotify.BuildConfig
import relaxeddd.englishnotify.common.*
import relaxeddd.englishnotify.R
import relaxeddd.englishnotify.model.preferences.SharedHelper
import relaxeddd.englishnotify.model.repository.RepositoryCommon
import relaxeddd.englishnotify.model.repository.RepositoryWord

class ViewModelMain(private val repositoryUser: RepositoryUser) : ViewModelBase() {

    val user = repositoryUser.liveDataUser
    val isShowGoogleAuth = MutableLiveData(false)
    val isShowLoading = MutableLiveData(false)
    val isShowHorizontalProgress = MutableLiveData(false)
    val isOldNavigation = MutableLiveData(SharedHelper.isOldNavigationDesign())
    private var isRateDialogShown = false

    private val userObserver = Observer<User?> { user ->
        isShowGoogleAuth.value = (user == null || RepositoryCommon.getInstance().firebaseUser == null) && !SharedHelper.isHideSignIn()

        val launchCount = SharedHelper.getLaunchCount()
        if (user != null && !isRateDialogShown && !SharedHelper.isCancelledRateDialog() && launchCount % 4 == 0) {
            isRateDialogShown = true
            navigateEvent.value = Event(NAVIGATION_DIALOG_RATE_APP)
            SharedHelper.setCancelledRateDialog(true)
        }
        if (user != null) {
            if (user.email.isNotEmpty()) {
                SharedHelper.setPrivacyPolicyConfirmed(true)
            }
            navigateEvent.value = Event(NAVIGATION_INIT_BILLING)
        }
    }
    private val wordsObserver = Observer<List<Word>> {
        print("Words loaded")
    }
    private val actualVersionObserver = Observer<Boolean> { isActualVersion ->
        if (!isActualVersion) {
            navigateEvent.value = Event(NAVIGATION_DIALOG_NEW_VERSION)
        }
    }

    val clickListenerGoogleAuth = View.OnClickListener {
        if (!isNetworkAvailable()) {
            showToast(getAppString(R.string.network_not_available))
            return@OnClickListener
        }
        navigateEvent.value = Event(NAVIGATION_GOOGLE_AUTH)
    }
    val clickListenerHideSignIn = View.OnClickListener {
        val isHide = !SharedHelper.isHideSignIn()
        repositoryUser.hideSignIn()
        isShowGoogleAuth.value = (user.value == null || RepositoryCommon.getInstance().firebaseUser == null) && !isHide
    }

    init {
        repositoryUser.liveDataUser.observeForever(userObserver)
        repositoryUser.liveDataIsActualVersion.observeForever(actualVersionObserver)
    }

    override fun onCleared() {
        super.onCleared()
        repositoryUser.liveDataUser.removeObserver(userObserver)
        RepositoryWord.getInstance().words.removeObserver(wordsObserver)
    }

    fun onViewCreate() {
        requestInit()

        if (!SharedHelper.isPatchNotesViewed(BuildConfig.VERSION_NAME)) {
            navigateEvent.value = Event(NAVIGATION_DIALOG_PATCH_NOTES)
            SharedHelper.setPatchNotesViewed(BuildConfig.VERSION_NAME)
        }
        isOldNavigation.value = SharedHelper.isOldNavigationDesign()
    }

    fun onViewResume() {}

    fun requestInit() {
        RepositoryWord.getInstance().words.observeForever(wordsObserver)

        if (repositoryUser.isAuthorized() && !repositoryUser.isInit()) {
            isShowGoogleAuth.value = false
            //isShowHorizontalProgress.value = true

            ioScope.launch {
                val loginEmail = FirebaseAuth.getInstance().currentUser?.email ?: ""
                val savedEmail = SharedHelper.getUserEmail()

                if (savedEmail.isNotEmpty() && savedEmail != loginEmail) {
                    RepositoryWord.getInstance().clearDictionary()
                    SharedHelper.setUserEmail(loginEmail)
                }
                repositoryUser.init(object: ListenerResult<Boolean> {
                    override fun onResult(result: Boolean) {
                        if (!result) {
                            userObserver.onChanged(null)
                        }
                        //isShowHorizontalProgress.value = false
                    }
                })
            }
        }
    }
}
