package relaxeddd.englishnotify.ui.notifications

import android.os.Build
import android.view.View
import android.widget.CompoundButton
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import relaxeddd.englishnotify.App
import relaxeddd.englishnotify.R
import relaxeddd.englishnotify.common.*
import relaxeddd.englishnotify.domain_words.repository.RepositoryWords
import relaxeddd.englishnotify.notifications.NotificationHelper
import relaxeddd.englishnotify.notifications.NotificationsWorkManagerHelper
import relaxeddd.englishnotify.preferences.Preferences
import relaxeddd.englishnotify.preferences.utils.NOTIFICATIONS_VIEW_INPUT
import relaxeddd.englishnotify.preferences.utils.NOTIFICATIONS_VIEW_STANDARD
import relaxeddd.englishnotify.ui.main.MainActivity
import relaxeddd.englishnotify.view_base.ViewModelBase
import relaxeddd.englishnotify.view_base.models.Event

class ViewModelNotifications: ViewModelBase() {

    private val prefs get() = Preferences.getInstance()

    val timeDurationOffValue = MutableLiveData(prefs.getDurationHours())
    val timeStartOff = MutableLiveData("20:00")
    val timeEndOff = MutableLiveData("07:00")
    val selectedTagLiveData = MutableLiveData("")
    val isVisibleNotificationsView = MutableLiveData(Build.VERSION.SDK_INT >= Build.VERSION_CODES.N)

    val textRepeatTime = MutableLiveData(
        App.context.resources.getStringArray(R.array.array_time_repeat)[
                prefs.getNotificationsRepeatTime().ordinal
        ]
    )
    val textLearnLanguage = MutableLiveData("")
    val textNotificationsView = MutableLiveData(
        App.context.resources.getStringArray(R.array.array_notifications_view)[prefs.getNotificationsView()
            ?: if (Build.VERSION.SDK_INT < Build.VERSION_CODES.N) NOTIFICATIONS_VIEW_STANDARD else NOTIFICATIONS_VIEW_INPUT]
    )

    val checkedChangeListenerShowOnlyOneNotification = CompoundButton.OnCheckedChangeListener { _, isChecked ->
        prefs.setShowOnlyOneNotification(isChecked)
    }
    val isShowOnlyOneNotification = MutableLiveData(prefs.isShowOnlyOneNotification())
    val isNotDeletable = MutableLiveData(prefs.isOngoingNotification())

    val isNotificationsEnabled = MutableLiveData(prefs.isNotificationsEnabled())
    var checkedChangeListenerEnableNotifications = CompoundButton.OnCheckedChangeListener { buttonView, isChecked ->
        if (isChecked != prefs.isNotificationsEnabled()) {
            if (!isChecked) {
                buttonView.isChecked = true
                navigateEvent.value = Event(NAVIGATION_DIALOG_CONFIRM_DISABLE_NOTIFICATIONS)
            } else {
                setNotificationsEnable(true)
            }
        }
    }

    var checkedChangeListenerDeletable = CompoundButton.OnCheckedChangeListener { buttonView, isChecked ->
        if (isChecked && prefs.getNotificationsView() == NOTIFICATIONS_VIEW_STANDARD) {
            buttonView.isChecked = false
            showToast(R.string.enable_notification_translation_input)
        } else {
            prefs.setOngoingNotification(isChecked)
        }
    }

    val clickListenerSelectCategory = View.OnClickListener {
        navigateEvent.value = Event(NAVIGATION_FRAGMENT_SELECT_CATEGORY)
    }
    val clickListenerLearnLanguage = View.OnClickListener {
        navigateEvent.value = Event(NAVIGATION_DIALOG_LEARN_ENGLISH)
    }
    val clickListenerRepeatTime = View.OnClickListener {
        navigateEvent.value = Event(NAVIGATION_FRAGMENT_TIME)
    }
    val clickListenerNotificationsView = View.OnClickListener {
        when {
            Build.VERSION.SDK_INT < Build.VERSION_CODES.N -> showToast(R.string.android_version_7_required)
            else -> navigateEvent.value = Event(NAVIGATION_DIALOG_NOTIFICATIONS_VIEW)
        }
    }
    val clickListenerNightTime = View.OnClickListener {
        navigateEvent.value = Event(NAVIGATION_DIALOG_NIGHT_TIME)
    }
    val clickListenerTestNotifications = View.OnClickListener {
        navigateEvent.value = Event(NAVIGATION_DIALOG_TEST_NOTIFICATIONS)
    }

    //------------------------------------------------------------------------------------------------------------------
    init {
        viewModelScope.launch {
            prefs.selectedCategoryFlow.collect {
                selectedTagLiveData.value = getStringByResName(it).replaceFirst(OWN_KEY_SYMBOL, "")
            }
        }
        viewModelScope.launch {
            prefs.notificationsRepeatTimeFlow.collect {
                textRepeatTime.value = App.context.resources.getStringArray(R.array.array_time_repeat)[
                        prefs.getNotificationsRepeatTime().ordinal
                ]
            }
        }
        viewModelScope.launch {
            prefs.learnLanguageTypeFlow.collect {
                textLearnLanguage.value = App.context.resources.getStringArray(R.array.array_learn_language)[it]
            }
        }

        val startHour = prefs.getStartHour()
        val duration = prefs.getDurationHours()
        val startHourStr = (if (startHour < 10) "0" else "") + startHour.toString() + ":00"
        val endHour = startHour + duration - (if (startHour + duration >= 24) 24 else 0)
        val endHourStr = (if (endHour < 10) "0" else "") + endHour.toString() + ":00"

        timeStartOff.value = startHourStr
        timeEndOff.value = endHourStr
    }

    fun onDialogLearnLanguageResult(learnEnglishType: Int) {
        if (learnEnglishType != prefs.getLearnLanguageType()) {
            prefs.setLearnLanguageType(learnEnglishType)
        }
    }

    fun onDialogPushOffTimeResult(result: Pair<Int, Int>) {
        val startHourStr = (if (result.first < 10) "0" else "") + result.first.toString() + ":00"
        val endHour = result.first + result.second - (if (result.first + result.second >= 24) 24 else 0)
        val endHourStr = (if (endHour < 10) "0" else "") + endHour.toString() + ":00"

        prefs.setStartHour(result.first)
        prefs.setDurationHours(result.second)
        timeDurationOffValue.value = result.second
        timeStartOff.value = startHourStr
        timeEndOff.value = endHourStr
    }

    fun onDialogNotificationsViewResult(result: Int) {
        textNotificationsView.value = App.context.resources.getStringArray(R.array.array_notifications_view)[result]
        prefs.setNotificationsView(result)
    }

    fun onDialogTestNotificationsResult(result: Boolean) {
        if (result) {
            if (RepositoryWords.getInstance(App.context).words.value?.isNullOrEmpty() == true) {
                showToast(R.string.category_own_not_selected)
                return
            }
            viewModelScope.launch(Dispatchers.Default) {
                NotificationHelper.generateNotification(App.context, MainActivity.javaClass)
            }
        }
    }

    fun onDialogDisableNotificationsResult(result: Boolean) {
        if (result) {
            setNotificationsEnable(false)
        }
    }

    private fun setNotificationsEnable(isEnabled: Boolean) {
        prefs.setNotificationsEnabled(isEnabled)
        isNotificationsEnabled.value = isEnabled

        if (isEnabled) {
            NotificationsWorkManagerHelper.launchWork(
                context = App.context,
                repeatTimeInMinutes = prefs.getNotificationsRepeatTime().valueInMinutes,
                isForceUpdate = false
            )
        } else {
            NotificationsWorkManagerHelper.cancelWork(App.context)
        }
    }
}
