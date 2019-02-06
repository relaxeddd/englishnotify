package com.example.vchechin.testapp.ui.notifications

import android.os.Bundle
import com.example.vchechin.testapp.R
import com.example.vchechin.testapp.common.*
import com.example.vchechin.testapp.databinding.FragmentNotificationsBinding
import com.example.vchechin.testapp.dialogs.DialogCheckTags
import com.example.vchechin.testapp.dialogs.DialogSelectRepeatTime

class FragmentNotifications : BaseFragment<ViewModelNotifications, FragmentNotificationsBinding>() {

    private val listenerRepeatTime: ListenerResult<Int> = object: ListenerResult<Int> {
        override fun onResult(result: Int) {
            viewModel.onDialogRepeatTimeResult(result)
        }
    }
    private val listenerCheckTags: ListenerResult<List<String>> = object: ListenerResult<List<String>> {
        override fun onResult(result: List<String>) {
            viewModel.onDialogCheckTagsResult(result)
        }
    }

    override fun getLayoutResId() = R.layout.fragment_notifications
    override fun getToolbarTitleResId() = R.string.notifications
    override fun getViewModelFactory() = InjectorUtils.provideNotificationsViewModelFactory(requireContext())
    override fun getViewModelClass() = ViewModelNotifications::class.java

    override fun configureBinding() {
        super.configureBinding()
        binding.viewModel = viewModel
        binding.checkedChangeListenerEnableNotifications = viewModel.checkedChangeListenerEnableNotifications
        binding.clickListenerRepeatTime = viewModel.clickListenerRepeatTime
        binding.clickListenerCheckTags = viewModel.clickListenerCheckTags
        binding.listRepeatTime = resources.getStringArray(R.array.array_time_repeat)
    }

    override fun onNavigationEvent(eventId: Int) {
        when (eventId) {
            NAVIGATION_DIALOG_REPEAT -> {
                val dialog = DialogSelectRepeatTime()
                val args = Bundle()
                args.putInt(SELECTED_ITEM, viewModel.user.value?.notificationsTimeType ?: 0)
                dialog.arguments = args
                dialog.listener = listenerRepeatTime
                dialog.show(this@FragmentNotifications.childFragmentManager, "Repeat Dialog")
            }
            NAVIGATION_DIALOG_CHECK_TAGS -> {
                val dialog = DialogCheckTags()
                val args = Bundle()
                args.putStringArray(ITEMS, (viewModel.user.value?.tagsAvailable ?: ArrayList()).toTypedArray())
                args.putStringArray(CHECKED_ITEMS, (viewModel.user.value?.tagsSelected ?: ArrayList()).toTypedArray())
                dialog.arguments = args
                dialog.listener = listenerCheckTags
                dialog.show(this@FragmentNotifications.childFragmentManager, "Check tags Dialog")
            }
        }
    }
}