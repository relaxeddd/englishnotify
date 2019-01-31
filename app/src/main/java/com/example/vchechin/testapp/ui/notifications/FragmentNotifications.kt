package com.example.vchechin.testapp.ui.notifications

import com.example.vchechin.testapp.R
import com.example.vchechin.testapp.common.BaseFragment
import com.example.vchechin.testapp.common.InjectorUtils
import com.example.vchechin.testapp.common.NAVIGATION_DIALOG_REPEAT
import com.example.vchechin.testapp.databinding.FragmentNotificationsBinding
import com.example.vchechin.testapp.dialogs.DialogSelectRepeatTime

class FragmentNotifications : BaseFragment<ViewModelNotifications, FragmentNotificationsBinding>() {

    override fun getLayoutResId() = R.layout.fragment_notifications
    override fun getToolbarTitleResId() = R.string.notifications
    override fun getViewModelFactory() = InjectorUtils.provideNotificationsViewModelFactory(requireContext())
    override fun getViewModelClass() = ViewModelNotifications::class.java
    override fun getMenuResId() = R.menu.menu_accept
    override fun isHomeMenuButtonEnabled() = true
    override fun getHomeMenuButtonIconResId() = R.drawable.ic_close

    override fun configureBinding() {
        super.configureBinding()
        binding.viewModel = viewModel
        binding.checkedChangeListenerEnableNotifications = viewModel.checkedChangeListenerEnableNotifications
        binding.clickListenerRepeatTime = viewModel.clickListenerRepeatTime
        binding.listRepeatTime = resources.getStringArray(R.array.array_time_repeat)
    }

    override fun onNavigationEvent(eventId: Int) {
        if (eventId == NAVIGATION_DIALOG_REPEAT) {
            DialogSelectRepeatTime().show(this@FragmentNotifications.childFragmentManager, "Repeat Dialog")
        }
    }
}