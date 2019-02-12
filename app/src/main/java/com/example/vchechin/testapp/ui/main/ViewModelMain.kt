package com.example.vchechin.testapp.ui.main

import android.view.View
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModel
import com.example.vchechin.testapp.common.*
import com.example.vchechin.testapp.model.repository.RepositoryUser

class ViewModelMain(private val repositoryUser: RepositoryUser) : ViewModelBase() {
    val user = repositoryUser.liveDataUser
    val isShowWarningNotifications = MutableLiveData<Boolean>(false)
    val isShowWarningAuthorize = MutableLiveData<Boolean>(false)

    private val userObserver = Observer<User?> { user ->
        isShowWarningNotifications.value = user == null || user.receiveNotifications == false
        isShowWarningAuthorize.value = user == null
    }

    val clickListenerWarningNotifications = View.OnClickListener {
        navigateEvent.value = Event(NAVIGATION_FRAGMENT_NOTIFICATIONS)
    }
    val clickListenerWarningAuthorize = View.OnClickListener {
        navigateEvent.value = Event(NAVIGATION_FRAGMENT_SETTINGS)
    }

    init {
        repositoryUser.liveDataUser.observeForever(userObserver)
    }

    override fun onCleared() {
        super.onCleared()
        repositoryUser.liveDataUser.removeObserver(userObserver)
    }
}