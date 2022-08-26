package relaxeddd.englishnotify.ui.time

import relaxeddd.englishnotify.common.NAVIGATION_ACTIVITY_BACK
import relaxeddd.englishnotify.notifications.NotificationsWorkManagerHelper
import relaxeddd.englishnotify.preferences.Preferences
import relaxeddd.englishnotify.preferences.models.NotificationRepeatTime
import relaxeddd.englishnotify.view_base.ViewModelBase
import relaxeddd.englishnotify.view_base.models.Event
import javax.inject.Inject

class ViewModelTime @Inject constructor(
    private val prefs: Preferences,
    private val notificationsWorkManagerHelper: NotificationsWorkManagerHelper,
) : ViewModelBase() {

    var receiveNotificationsTime: Int = prefs.getNotificationsRepeatTime().ordinal

    fun onNotificationTimeChanged(notificationTime: Int) {
        receiveNotificationsTime = notificationTime
    }

    fun onClickAccept() {
        val currentRepeatTime = prefs.getNotificationsRepeatTime()

        if (receiveNotificationsTime != currentRepeatTime.ordinal) {
            prefs.setNotificationsRepeatTime(receiveNotificationsTime)
            notificationsWorkManagerHelper.launchWork(
                repeatTimeInMinutes = NotificationRepeatTime.valueOf(receiveNotificationsTime).valueInMinutes,
                isForceUpdate = true,
                isNotificationsEnabled = prefs.isNotificationsEnabled(),
            )
        }

        navigateEvent.value = Event(NAVIGATION_ACTIVITY_BACK)
    }
}
