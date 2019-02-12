package com.example.vchechin.testapp.ui.settings

import com.example.vchechin.testapp.R
import com.example.vchechin.testapp.common.BaseFragment
import com.example.vchechin.testapp.common.InjectorUtils
import com.example.vchechin.testapp.common.NAVIGATION_DIALOG_APP_ABOUT
import com.example.vchechin.testapp.databinding.FragmentSettingsBinding
import com.example.vchechin.testapp.dialogs.DialogAppAbout

class FragmentSettings : BaseFragment<ViewModelSettings, FragmentSettingsBinding>() {

    override fun getLayoutResId() = R.layout.fragment_settings
    override fun getToolbarTitleResId() = R.string.settings
    override fun getViewModelFactory() = InjectorUtils.provideSettingsViewModelFactory(requireContext())
    override fun getViewModelClass() = ViewModelSettings::class.java

    override fun configureBinding() {
        super.configureBinding()
        binding.viewModel = viewModel
        binding.listLanguages = resources.getStringArray(R.array.array_languages)
    }

    override fun onNavigationEvent(eventId: Int) {
        when (eventId) {
            NAVIGATION_DIALOG_APP_ABOUT -> {
                DialogAppAbout().show(this@FragmentSettings.childFragmentManager, "Learn Language Dialog")
            }
        }
    }
}