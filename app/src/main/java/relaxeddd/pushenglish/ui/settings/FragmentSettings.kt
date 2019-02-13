package relaxeddd.pushenglish.ui.settings

import relaxeddd.pushenglish.R
import relaxeddd.pushenglish.common.BaseFragment
import relaxeddd.pushenglish.common.InjectorUtils
import relaxeddd.pushenglish.common.NAVIGATION_DIALOG_APP_ABOUT
import relaxeddd.pushenglish.databinding.FragmentSettingsBinding
import relaxeddd.pushenglish.dialogs.DialogAppAbout

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