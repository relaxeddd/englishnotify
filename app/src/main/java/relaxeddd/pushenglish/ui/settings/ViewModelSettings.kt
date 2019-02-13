package relaxeddd.pushenglish.ui.settings

import android.view.View
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import relaxeddd.pushenglish.common.*
import relaxeddd.pushenglish.model.repository.RepositoryUser

class ViewModelSettings(private val repositoryUser: RepositoryUser) : ViewModelBase() {

    val user: LiveData<User?> = repositoryUser.liveDataUser
    val appLanguageType = MutableLiveData(0)

    val clickListenerSignIn = View.OnClickListener {

    }
    val clickListenerLanguage = View.OnClickListener {}
    val clickListenerAppInfo = View.OnClickListener {
        navigateEvent.value =
                Event(NAVIGATION_DIALOG_APP_ABOUT)
    }
    val clickListenerSendFeedback = View.OnClickListener {
        navigateEvent.value =
                Event(NAVIGATION_DIALOG_SEND_FEEDBACK)
    }

    init {

    }
}