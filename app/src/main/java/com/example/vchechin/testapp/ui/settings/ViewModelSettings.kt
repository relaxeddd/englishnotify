package com.example.vchechin.testapp.ui.settings

import android.view.View
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.example.vchechin.testapp.common.Event
import com.example.vchechin.testapp.common.NAVIGATION_DIALOG_APP_ABOUT
import com.example.vchechin.testapp.common.User
import com.example.vchechin.testapp.common.ViewModelBase
import com.example.vchechin.testapp.model.repository.RepositoryUser

class ViewModelSettings(private val repositoryUser: RepositoryUser) : ViewModelBase() {

    val user: LiveData<User?> = repositoryUser.liveDataUser
    val appLanguageType = MutableLiveData(0)

    val clickListenerSignIn = View.OnClickListener {

    }
    val clickListenerLanguage = View.OnClickListener {}
    val clickListenerAppInfo = View.OnClickListener {
        navigateEvent.value = Event(NAVIGATION_DIALOG_APP_ABOUT)
    }

    init {

    }
}