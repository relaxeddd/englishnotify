package relaxeddd.englishnotify.ui.settings

import android.os.Build
import android.view.View
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Observer
import kotlinx.coroutines.launch
import relaxeddd.englishnotify.App
import relaxeddd.englishnotify.common.*
import relaxeddd.englishnotify.R
import relaxeddd.englishnotify.model.repository.RepositoryUser

class ViewModelSettings(private val repositoryUser: RepositoryUser) : ViewModelBase() {

    private val userObserver = Observer<User?> { user ->
        var subTime = user?.subscriptionTime ?: System.currentTimeMillis()
        subTime -= System.currentTimeMillis()
        if (subTime < 0) subTime = 0
        liveDataSubDays.value = (subTime / 1000 / 60 / 60 / 24).toString()
    }

    val user: LiveData<User?> = repositoryUser.liveDataUser
    val liveDataSubDays = MutableLiveData("")
    val textTheme: String = App.context.resources.getStringArray(R.array.array_themes)[SharedHelper.getAppThemeType()]
    val isVisibleReceiveHelp = Build.VERSION.SDK_INT >= Build.VERSION_CODES.M
    
    val clickListenerAppInfo = View.OnClickListener {
        navigateEvent.value = Event(NAVIGATION_DIALOG_APP_ABOUT)
    }
    val clickListenerSubscription = View.OnClickListener {
        navigateEvent.value = Event(NAVIGATION_DIALOG_SUBSCRIPTION)
    }
    val clickListenerSubscriptionInfo = View.OnClickListener {
        navigateEvent.value = Event(NAVIGATION_DIALOG_SUBSCRIPTION_INFO)
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
    val clickListenerReceiveHelp = View.OnClickListener {
        navigateEvent.value = Event(NAVIGATION_DIALOG_RECEIVE_HELP)
    }
    val clickListenerStatistic = View.OnClickListener {
        navigateEvent.value = Event(NAVIGATION_FRAGMENT_STATISTIC)
    }
    val clickListenerTheme = View.OnClickListener {
        navigateEvent.value = Event(NAVIGATION_DIALOG_THEME)
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