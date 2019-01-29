package com.example.vchechin.testapp.ui.settings

import com.example.vchechin.testapp.R
import com.example.vchechin.testapp.common.BaseFragment
import com.example.vchechin.testapp.common.InjectorUtils
import com.example.vchechin.testapp.databinding.FragmentSettingsBinding

class FragmentSettings : BaseFragment<ViewModelSettings, FragmentSettingsBinding>() {

    override fun getLayoutResId() = R.layout.fragment_settings
    override fun getToolbarTitleResId() = R.string.settings
    override fun getViewModelFactory() = InjectorUtils.provideSettingsViewModelFactory(requireContext())
    override fun getViewModelClass() = ViewModelSettings::class.java
}