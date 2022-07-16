package relaxeddd.englishnotify.ui.main

import androidx.lifecycle.MutableLiveData
import relaxeddd.englishnotify.BuildConfig
import relaxeddd.englishnotify.common.Event
import relaxeddd.englishnotify.common.NAVIGATION_DIALOG_PATCH_NOTES
import relaxeddd.englishnotify.common.ViewModelBase
import relaxeddd.englishnotify.model.preferences.SharedHelper

class ViewModelMain : ViewModelBase() {

    val isShowLoading = MutableLiveData(false)
    val isBottomNavigation = MutableLiveData(SharedHelper.isOldNavigationDesign())

    fun onViewCreate() {
        if (!SharedHelper.isPatchNotesViewed(BuildConfig.VERSION_NAME)) {
            navigateEvent.value = Event(NAVIGATION_DIALOG_PATCH_NOTES)
            SharedHelper.setPatchNotesViewed(BuildConfig.VERSION_NAME)
        }
        isBottomNavigation.value = SharedHelper.isOldNavigationDesign()
    }
}
