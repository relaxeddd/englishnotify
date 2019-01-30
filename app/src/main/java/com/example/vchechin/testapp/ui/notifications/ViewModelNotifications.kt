package com.example.vchechin.testapp.ui.notifications

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import com.example.vchechin.testapp.common.User
import com.example.vchechin.testapp.model.repository.RepositoryUser

class ViewModelNotifications(private val repositoryUser: RepositoryUser) : ViewModel() {
    val user: LiveData<User> = repositoryUser.user
}