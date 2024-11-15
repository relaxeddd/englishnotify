package relaxeddd.englishnotify.ui.main

import androidx.lifecycle.MutableLiveData
import relaxeddd.englishnotify.BuildConfig
import relaxeddd.englishnotify.common.NAVIGATION_DIALOG_PATCH_NOTES
import relaxeddd.englishnotify.common.NAVIGATION_REQUEST_NOTIFICATIONS_PERMISSION
import relaxeddd.englishnotify.preferences.Preferences
import relaxeddd.englishnotify.view_base.ViewModelBase
import relaxeddd.englishnotify.view_base.models.Event
import javax.inject.Inject

class ViewModelMain @Inject constructor(private val prefs: Preferences) : ViewModelBase() {

    val isShowLoading = MutableLiveData(false)
    val isBottomNavigation = MutableLiveData(prefs.isBottomNavigation())

    fun onViewCreate(hasNotificationsPermission: Boolean) {
        if (!hasNotificationsPermission) {
            navigateEvent.value = Event(NAVIGATION_REQUEST_NOTIFICATIONS_PERMISSION)
        } else if (!prefs.isPatchNotesViewed(BuildConfig.VERSION_NAME)) {
            navigateEvent.value = Event(NAVIGATION_DIALOG_PATCH_NOTES)
            prefs.setPatchNotesViewed(BuildConfig.VERSION_NAME)
        }
        isBottomNavigation.value = prefs.isBottomNavigation()
    }

    fun showPatchNotesIfNeeded() {
        if (!prefs.isPatchNotesViewed(BuildConfig.VERSION_NAME)) {
            navigateEvent.value = Event(NAVIGATION_DIALOG_PATCH_NOTES)
            prefs.setPatchNotesViewed(BuildConfig.VERSION_NAME)
        }
    }
}
