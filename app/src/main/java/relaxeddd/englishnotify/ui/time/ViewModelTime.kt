package relaxeddd.englishnotify.ui.time

import android.widget.RadioButton
import android.widget.RadioGroup
import relaxeddd.englishnotify.App
import relaxeddd.englishnotify.common.NAVIGATION_ACTIVITY_BACK
import relaxeddd.englishnotify.notifications.NotificationsWorkManagerHelper
import relaxeddd.englishnotify.preferences.Preferences
import relaxeddd.englishnotify.preferences.models.NotificationRepeatTime
import relaxeddd.englishnotify.view_base.ViewModelBase
import relaxeddd.englishnotify.view_base.models.Event

class ViewModelTime : ViewModelBase() {

    private val prefs get() = Preferences.getInstance()

    var receiveNotificationsTime: Int = prefs.getNotificationsRepeatTime().ordinal

    val checkedChangeListenerTime = RadioGroup.OnCheckedChangeListener { view, _ ->
        val radioButton = view.findViewById<RadioButton>(view?.checkedRadioButtonId ?: 0)
        receiveNotificationsTime = (radioButton?.tag as String).toInt()
    }

    fun onClickAccept() {
        val currentRepeatTime = prefs.getNotificationsRepeatTime()

        if (receiveNotificationsTime != currentRepeatTime.ordinal) {
            prefs.setNotificationsRepeatTime(receiveNotificationsTime)
            NotificationsWorkManagerHelper.launchWork(
                context = App.context,
                repeatTimeInMinutes = NotificationRepeatTime.valueOf(receiveNotificationsTime).valueInMinutes,
                isForceUpdate = true,
            )
        }

        navigateEvent.value = Event(NAVIGATION_ACTIVITY_BACK)
    }
}
