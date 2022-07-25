package relaxeddd.englishnotify.ui.main

import androidx.lifecycle.MutableLiveData
import relaxeddd.englishnotify.BuildConfig
import relaxeddd.englishnotify.common.NAVIGATION_DIALOG_PATCH_NOTES
import relaxeddd.englishnotify.preferences.Preferences
import relaxeddd.englishnotify.view_base.ViewModelBase
import relaxeddd.englishnotify.view_base.models.Event

class ViewModelMain : ViewModelBase() {

    private val prefs get() = Preferences.getInstance()

    val isShowLoading = MutableLiveData(false)
    val isBottomNavigation = MutableLiveData(prefs.isBottomNavigation())

    fun onViewCreate() {
        if (!prefs.isPatchNotesViewed(BuildConfig.VERSION_NAME)) {
            navigateEvent.value = Event(NAVIGATION_DIALOG_PATCH_NOTES)
            prefs.setPatchNotesViewed(BuildConfig.VERSION_NAME)
        }
        isBottomNavigation.value = prefs.isBottomNavigation()
    }
}
