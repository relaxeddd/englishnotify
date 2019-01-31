package com.example.vchechin.testapp.ui.main

import androidx.lifecycle.ViewModel
import com.example.vchechin.testapp.model.repository.RepositoryUser

class ViewModelMain(private val repositoryUser: RepositoryUser) : ViewModel() {
    val user = repositoryUser.liveDataUser
}