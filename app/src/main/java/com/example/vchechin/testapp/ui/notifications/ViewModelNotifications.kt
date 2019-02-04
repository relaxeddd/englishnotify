package com.example.vchechin.testapp.ui.notifications

import android.view.View
import android.widget.CompoundButton
import androidx.lifecycle.LiveData
import com.example.vchechin.testapp.common.*
import com.example.vchechin.testapp.model.repository.RepositoryUser
import kotlinx.coroutines.launch

class ViewModelNotifications(private val repositoryUser: RepositoryUser) : ViewModelBase() {

    val user: LiveData<User> = repositoryUser.liveDataUser

    var checkedChangeListenerEnableNotifications = CompoundButton.OnCheckedChangeListener { buttonView, isChecked ->
        if (isChecked != repositoryUser.liveDataUser.value?.receiveNotifications) {
            uiScope.launch {
                buttonView.isClickable = false
                repositoryUser.setReceiveNotifications(isChecked)
                buttonView.isClickable = true
            }
        }
    }

    val clickListenerRepeatTime = View.OnClickListener {
        navigateEvent.value = Event(NAVIGATION_DIALOG_REPEAT)
    }
    val clickListenerCheckTags = View.OnClickListener {
        navigateEvent.value = Event(NAVIGATION_DIALOG_CHECK_TAGS)
    }

    fun onDialogRepeatTimeResult(receiveNotificationsTime: Int) {
        if (receiveNotificationsTime != repositoryUser.liveDataUser.value?.notificationsTimeType) {
            uiScope.launch {
                repositoryUser.setNotificationsTimeType(receiveNotificationsTime)
            }
        }
    }

    fun onDialogCheckTagsResult(checkedItems: List<String>) {
        if (!checkedItems.equalsIgnoeOrder(repositoryUser.liveDataUser.value?.tagsSelected)) {
            uiScope.launch {
                repositoryUser.setCheckedTags(checkedItems)
            }
        }
    }
}