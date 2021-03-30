package relaxeddd.englishnotify.ui.time

import android.widget.RadioButton
import android.widget.RadioGroup
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch
import relaxeddd.englishnotify.R
import relaxeddd.englishnotify.common.*
import relaxeddd.englishnotify.model.repository.RepositoryUser

class ViewModelTime(private val repositoryUser: RepositoryUser) : ViewModelBase() {

    val isDisableSubTime: Boolean = (repositoryUser.liveDataUser.value?.subscriptionTime ?: 0) <= System.currentTimeMillis()
    var receiveNotificationsTime: Int = repositoryUser.liveDataUser.value?.notificationsTimeType ?: 7

    val checkedChangeListenerTime = RadioGroup.OnCheckedChangeListener { view, _ ->
        val radioButton = view.findViewById<RadioButton>(view?.checkedRadioButtonId ?: 0)
        receiveNotificationsTime = (radioButton?.tag as String).toInt()
    }

    fun onClickAccept() {
        val user = repositoryUser.liveDataUser.value ?: return

        if (receiveNotificationsTime < 7 && !user.isSubscribed()) {
            showToast(R.string.subscription_need)
        } else {
            viewModelScope.launch {
                navigateEvent.value = Event(NAVIGATION_LOADING_SHOW)

                val result = if (receiveNotificationsTime != user.notificationsTimeType) {
                    repositoryUser.setNotificationsTimeType(receiveNotificationsTime)
                } else {
                    true
                }

                navigateEvent.value = Event(NAVIGATION_LOADING_HIDE)

                if (result) {
                    navigateEvent.value = Event(NAVIGATION_ACTIVITY_BACK)
                }
            }
        }
    }
}
