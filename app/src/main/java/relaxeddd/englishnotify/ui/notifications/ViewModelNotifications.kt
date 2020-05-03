package relaxeddd.englishnotify.ui.notifications

import android.os.Build
import android.view.View
import android.widget.CompoundButton
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Observer
import relaxeddd.englishnotify.model.repository.RepositoryUser
import kotlinx.coroutines.launch
import relaxeddd.englishnotify.App
import relaxeddd.englishnotify.common.*
import relaxeddd.englishnotify.R

class ViewModelNotifications(private val repositoryUser: RepositoryUser) : ViewModelBase() {

    val user: LiveData<User?> = repositoryUser.liveDataUser
    val isEnableNotificationsClickable = MutableLiveData(false)
    val timeDurationOffValue = MutableLiveData(SharedHelper.getDurationHours())
    val timeStartOff = MutableLiveData("20:00")
    val timeEndOff = MutableLiveData("07:00")
    val selectedTagLiveData = MutableLiveData("")
    val isVisibleNotificationsView = Build.VERSION.SDK_INT >= Build.VERSION_CODES.N

    val textRepeatTime = MutableLiveData("")
    val textLearnLanguage = MutableLiveData("")
    val textNotificationsView = MutableLiveData(App.context.resources.getStringArray(R.array.array_notifications_view)[SharedHelper.getNotificationsView()])

    val checkedChangeListenerShowOnlyOneNotification = CompoundButton.OnCheckedChangeListener { _, isChecked ->
        SharedHelper.setShowOnlyOneNotification(isChecked)
    }
    val isShowOnlyOneNotification = MutableLiveData(SharedHelper.isShowOnlyOneNotification())
    val isNotDeletable = MutableLiveData(SharedHelper.isOngoing())

    private val userObserver = Observer<User?> { user ->
        isEnableNotificationsClickable.value = user != null
        selectedTagLiveData.value = if (user != null) getStringByResName(user.selectedTag) else ""
        textLearnLanguage.value = App.context.resources.getStringArray(R.array.array_learn_language)[user?.learnLanguageType ?: 0]
        textRepeatTime.value = App.context.resources.getStringArray(R.array.array_time_repeat)[user?.notificationsTimeType ?: 0]
    }

    var checkedChangeListenerEnableNotifications = CompoundButton.OnCheckedChangeListener { buttonView, isChecked ->
        if (isChecked != repositoryUser.liveDataUser.value?.receiveNotifications) {
            if (!isChecked) {
                buttonView.isChecked = true
                navigateEvent.value = Event(NAVIGATION_DIALOG_CONFIRM_DISABLE_NOTIFICATIONS)
            } else {
                setNotificationsEnable(true)
            }
        }
    }

    var checkedChangeListenerDeletable = CompoundButton.OnCheckedChangeListener { buttonView, isChecked ->
        if (isChecked && SharedHelper.getNotificationsView() == NOTIFICATIONS_VIEW_STANDARD) {
            buttonView.isChecked = false
            showToast(R.string.enable_notification_translation_input)
        } else {
            SharedHelper.setOngoing(isChecked)
        }
    }

    val clickListenerEnableNotifications = View.OnClickListener {
        if (user.value == null) {
            showToast(R.string.please_authorize)
        }
    }

    val clickListenerSelectCategory = View.OnClickListener {
        if (user.value != null) {
            navigateEvent.value = Event(NAVIGATION_FRAGMENT_SELECT_CATEGORY)
        } else {
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
        val userValue = user.value

        if (userValue != null) {
            navigateEvent.value = Event(NAVIGATION_FRAGMENT_TIME)
        } else {
            showToast(R.string.please_authorize)
        }
    }
    val clickListenerNotificationsView = View.OnClickListener {
        when {
            Build.VERSION.SDK_INT < Build.VERSION_CODES.N -> showToast(R.string.android_version_7_required)
            user.value != null -> navigateEvent.value = Event(NAVIGATION_DIALOG_NOTIFICATIONS_VIEW)
            else -> showToast(R.string.please_authorize)
        }
    }
    val clickListenerNightTime = View.OnClickListener {
        if (user.value != null) {
            navigateEvent.value = Event(NAVIGATION_DIALOG_NIGHT_TIME)
        } else {
            showToast(R.string.please_authorize)
        }
    }
    val clickListenerTestNotifications = View.OnClickListener {
        val userValue = user.value

        if (userValue == null) {
            showToast(R.string.please_authorize)
        } else if (!userValue.receiveNotifications) {
            showToast(R.string.enable_notifications)
        } else if (userValue.testCount <= 0) {
            showToast(R.string.no_test_notifications)
        } else {
            navigateEvent.value = Event(NAVIGATION_DIALOG_TEST_NOTIFICATIONS)
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
        textNotificationsView.value = App.context.resources.getStringArray(R.array.array_notifications_view)[result]
        SharedHelper.setNotificationsView(result)
    }

    fun onDialogTestNotificationsResult(result: Boolean) {
        if (result) {
            uiScope.launch {
                repositoryUser.sendTestNotification()
            }
        }
    }

    fun onDialogDisableNotificationsResult(result: Boolean) {
        if (result) {
            setNotificationsEnable(false)
        }
    }

    private fun setNotificationsEnable(isEnabled: Boolean) {
        uiScope.launch {
            repositoryUser.setReceiveNotifications(isEnabled)
        }
    }
}