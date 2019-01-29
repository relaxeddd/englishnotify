package com.example.vchechin.testapp.ui.notifications

import com.example.vchechin.testapp.R
import com.example.vchechin.testapp.common.BaseFragment
import com.example.vchechin.testapp.common.InjectorUtils
import com.example.vchechin.testapp.databinding.FragmentNotificationsBinding

class FragmentNotifications : BaseFragment<ViewModelNotifications, FragmentNotificationsBinding>() {

    override fun getLayoutResId() = R.layout.fragment_notifications
    override fun getToolbarTitleResId() = R.string.notifications
    override fun getViewModelFactory() = InjectorUtils.provideNotificationsViewModelFactory(requireContext())
    override fun getViewModelClass() = ViewModelNotifications::class.java
}