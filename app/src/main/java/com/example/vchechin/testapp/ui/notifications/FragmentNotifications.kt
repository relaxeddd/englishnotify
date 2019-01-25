package com.example.vchechin.testapp.ui.notifications

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.ViewModelProviders
import com.example.vchechin.testapp.R
import com.example.vchechin.testapp.common.BaseFragment

class FragmentNotifications : BaseFragment() {

    private lateinit var viewModelNotifications: ViewModelNotifications

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        return inflater.inflate(R.layout.fragment_notifications, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        viewModelNotifications = ViewModelProviders.of(this).get(ViewModelNotifications::class.java)
        // TODO: Use the ViewModel
    }

    override fun getToolbarTitleResId() = R.string.notifications
}