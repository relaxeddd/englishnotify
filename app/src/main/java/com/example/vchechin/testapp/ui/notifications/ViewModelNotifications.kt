package com.example.vchechin.testapp.ui.notifications

import android.view.View
import android.widget.CompoundButton
import androidx.lifecycle.LiveData
import com.example.vchechin.testapp.common.Event
import com.example.vchechin.testapp.common.NAVIGATION_DIALOG_REPEAT
import com.example.vchechin.testapp.common.User
import com.example.vchechin.testapp.common.ViewModelBase
import com.example.vchechin.testapp.model.repository.RepositoryUser
import kotlinx.coroutines.launch

class ViewModelNotifications(private val repositoryUser: RepositoryUser) : ViewModelBase() {

    val user: LiveData<User> = repositoryUser.liveDataUser

    var checkedChangeListenerEnableNotifications = CompoundButton.OnCheckedChangeListener { buttonView, isChecked ->
        uiScope.launch {
            buttonView.isClickable = false
            repositoryUser.setReceiveNotifications(isChecked)
            buttonView.isClickable = true
        }
    }

    var clickListenerRepeatTime = View.OnClickListener {
        navigateEvent.value = Event(NAVIGATION_DIALOG_REPEAT)  //
    }
}