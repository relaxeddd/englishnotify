package relaxeddd.englishnotify.ui.time

import android.content.Context
import android.widget.RadioButton
import android.widget.RadioGroup
import relaxeddd.englishnotify.common.NAVIGATION_ACTIVITY_BACK
import relaxeddd.englishnotify.notifications.NotificationsWorkManagerHelper
import relaxeddd.englishnotify.preferences.Preferences
import relaxeddd.englishnotify.preferences.models.NotificationRepeatTime
import relaxeddd.englishnotify.view_base.ViewModelBase
import relaxeddd.englishnotify.view_base.models.Event
import javax.inject.Inject

class ViewModelTime @Inject constructor(
    private val context: Context,
    private val prefs: Preferences,
) : ViewModelBase() {

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
                context = context,
                prefs = prefs,
                repeatTimeInMinutes = NotificationRepeatTime.valueOf(receiveNotificationsTime).valueInMinutes,
                isForceUpdate = true,
            )
        }

        navigateEvent.value = Event(NAVIGATION_ACTIVITY_BACK)
    }
}
