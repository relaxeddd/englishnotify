package relaxeddd.englishnotify.ui.time

import android.widget.RadioButton
import android.widget.RadioGroup
import relaxeddd.englishnotify.App
import relaxeddd.englishnotify.common.Event
import relaxeddd.englishnotify.common.NAVIGATION_ACTIVITY_BACK
import relaxeddd.englishnotify.common.NotificationRepeatTime
import relaxeddd.englishnotify.common.ViewModelBase
import relaxeddd.englishnotify.model.preferences.SharedHelper
import relaxeddd.englishnotify.push.NotificationsWorkManagerHelper

class ViewModelTime : ViewModelBase() {

    var receiveNotificationsTime: Int = SharedHelper.getNotificationsRepeatTime(App.context).ordinal

    val checkedChangeListenerTime = RadioGroup.OnCheckedChangeListener { view, _ ->
        val radioButton = view.findViewById<RadioButton>(view?.checkedRadioButtonId ?: 0)
        receiveNotificationsTime = (radioButton?.tag as String).toInt()
    }

    fun onClickAccept() {
        val currentRepeatTime = SharedHelper.getNotificationsRepeatTime(App.context)

        if (receiveNotificationsTime != currentRepeatTime.ordinal) {
            SharedHelper.setNotificationsRepeatTime(receiveNotificationsTime)
            NotificationsWorkManagerHelper.launchWork(
                context = App.context,
                repeatTimeInMinutes = NotificationRepeatTime.valueOf(receiveNotificationsTime).valueInMinutes,
                isForceUpdate = true,
            )
        }

        navigateEvent.value = Event(NAVIGATION_ACTIVITY_BACK)
    }
}
