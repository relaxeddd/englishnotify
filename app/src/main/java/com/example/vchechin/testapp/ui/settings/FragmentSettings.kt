package com.example.vchechin.testapp.ui.settings

import android.view.View
import com.example.vchechin.testapp.R
import com.example.vchechin.testapp.common.BaseFragment
import com.example.vchechin.testapp.common.InjectorUtils
import com.example.vchechin.testapp.databinding.FragmentSettingsBinding
import kotlinx.android.synthetic.main.fragment_settings.*
import com.example.vchechin.testapp.common.animateDropdown

class FragmentSettings : BaseFragment<ViewModelSettings, FragmentSettingsBinding>() {

    var isOpened = false

    override fun getLayoutResId() = R.layout.fragment_settings
    override fun getToolbarTitleResId() = R.string.settings
    override fun getViewModelFactory() = InjectorUtils.provideSettingsViewModelFactory(requireContext())
    override fun getViewModelClass() = ViewModelSettings::class.java

    override fun configureBinding() {
        super.configureBinding()
        binding.clickListenerTest = View.OnClickListener {
            animateDropdown(card_view_test, !isOpened)
            isOpened = !isOpened
        }
    }
}