package com.example.vchechin.testapp.factories

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.vchechin.testapp.ui.notifications.ViewModelNotifications

class NotificationsViewModelFactory : ViewModelProvider.NewInstanceFactory() {

    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return ViewModelNotifications() as T
    }
}