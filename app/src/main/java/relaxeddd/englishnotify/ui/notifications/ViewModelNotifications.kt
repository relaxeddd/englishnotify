package relaxeddd.englishnotify.ui.notifications

import android.view.View
import android.widget.CompoundButton
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Observer
import relaxeddd.englishnotify.model.repository.RepositoryUser
import kotlinx.coroutines.launch
import relaxeddd.englishnotify.common.*
import relaxeddd.englishnotify.R

class ViewModelNotifications(private val repositoryUser: RepositoryUser) : ViewModelBase() {

    val user: LiveData<User?> = repositoryUser.liveDataUser
    val isEnableNotificationsClickable = MutableLiveData<Boolean>(false)
    val notificationsViewType = MutableLiveData<Int>(SharedHelper.getNotificationsView())
    val timeDurationOffValue = MutableLiveData<Int>(SharedHelper.getDurationHours())
    val timeStartOff = MutableLiveData<String>("20:00")
    val timeEndOff = MutableLiveData<String>("07:00")

    private val userObserver = Observer<User?> { user ->
        isEnableNotificationsClickable.value = user != null
    }

    var checkedChangeListenerEnableNotifications = CompoundButton.OnCheckedChangeListener { buttonView, isChecked ->
        if (isChecked != repositoryUser.liveDataUser.value?.receiveNotifications) {
            uiScope.launch {
                buttonView.isClickable = false
                repositoryUser.setReceiveNotifications(isChecked)
                buttonView.isClickable = true
            }
        }
    }

    val clickListenerEnableNotifications = View.OnClickListener {
        if (user.value == null) {
            showToast(R.string.please_authorize)
        }
    }

    val clickListenerLearnLanguage = View.OnClickListener {
        if (user.value != null) {
            navigateEvent.value = Event(NAVIGATION_DIALOG_LEARN_ENGLISH)
        } else {
            showToast(R.string.please_authorize)
        }
    }
    val clickListenerRepeatTime = View.OnClickListener {
        if (user.value != null) {
            navigateEvent.value = Event(NAVIGATION_DIALOG_REPEAT)
        } else {
            showToast(R.string.please_authorize)
        }
    }
    val clickListenerCheckTags = View.OnClickListener {
        if (user.value != null) {
            navigateEvent.value = Event(NAVIGATION_DIALOG_CHECK_TAGS)
        } else {
            showToast(R.string.please_authorize)
        }
    }
    val clickListenerNotificationsView = View.OnClickListener {
        if (user.value != null) {
            navigateEvent.value = Event(NAVIGATION_DIALOG_NOTIFICATIONS_VIEW)
        } else {
            showToast(R.string.please_authorize)
        }
    }
    val clickListenerNightTime = View.OnClickListener {
        if (user.value != null) {
            navigateEvent.value = Event(NAVIGATION_DIALOG_NIGHT_TIME)
        } else {
            showToast(R.string.please_authorize)
        }
    }

    init {
        repositoryUser.liveDataUser.observeForever(userObserver)

        val startHour = SharedHelper.getStartHour()
        val duration = SharedHelper.getDurationHours()
        val startHourStr = (if (startHour < 10) "0" else "") + startHour.toString() + ":00"
        val endHour = startHour + duration - (if (startHour + duration >= 24) 24 else 0)
        val endHourStr = (if (endHour < 10) "0" else "") + endHour.toString() + ":00"

        timeStartOff.value = startHourStr
        timeEndOff.value = endHourStr
    }

    override fun onCleared() {
        super.onCleared()
        repositoryUser.liveDataUser.removeObserver(userObserver)
    }

    fun onDialogLearnLanguageResult(learnEnglishType: Int) {
        if (learnEnglishType != repositoryUser.liveDataUser.value?.learnLanguageType) {
            uiScope.launch {
                repositoryUser.setLearnLanguageType(learnEnglishType)
            }
        }
    }

    fun onDialogRepeatTimeResult(receiveNotificationsTime: Int) {
        if (receiveNotificationsTime != repositoryUser.liveDataUser.value?.notificationsTimeType) {
            uiScope.launch {
                repositoryUser.setNotificationsTimeType(receiveNotificationsTime)
            }
        }
    }

    fun onDialogCheckTagsResult(checkedItems: List<String>) {
        if (!checkedItems.equalsIgnoreOrder(repositoryUser.liveDataUser.value?.tagsSelected)) {
            uiScope.launch {
                repositoryUser.setCheckedTags(checkedItems)
            }
        }
    }

    fun onDialogPushOffTimeResult(result: Pair<Int, Int>) {
        val startHourStr = (if (result.first < 10) "0" else "") + result.first.toString() + ":00"
        val endHour = result.first + result.second - (if (result.first + result.second >= 24) 24 else 0)
        val endHourStr = (if (endHour < 10) "0" else "") + endHour.toString() + ":00"

        SharedHelper.setStartHour(result.first)
        SharedHelper.setDurationHours(result.second)
        timeDurationOffValue.value = result.second
        timeStartOff.value = startHourStr
        timeEndOff.value = endHourStr
    }

    fun onDialogNotificationsViewResult(result: Int) {
        notificationsViewType.value = result
        SharedHelper.setNotificationsView(result)
    }
}