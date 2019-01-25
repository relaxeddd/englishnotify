package com.example.vchechin.testapp.factories

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.vchechin.testapp.model.repository.RepositoryUser
import com.example.vchechin.testapp.ui.main.ViewModelMain

class MainViewModelFactory(private val repositoryUser: RepositoryUser) : ViewModelProvider.NewInstanceFactory() {

    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return ViewModelMain(repositoryUser) as T
    }
}