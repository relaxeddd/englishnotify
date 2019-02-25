package relaxeddd.pushenglish.ui.settings

import com.firebase.ui.auth.AuthUI
import relaxeddd.pushenglish.R
import relaxeddd.pushenglish.common.*
import relaxeddd.pushenglish.databinding.FragmentSettingsBinding
import relaxeddd.pushenglish.dialogs.DialogAppAbout
import relaxeddd.pushenglish.dialogs.DialogConfirmLogout
import relaxeddd.pushenglish.dialogs.DialogSendFeedback

class FragmentSettings : BaseFragment<ViewModelSettings, FragmentSettingsBinding>() {

    private val listenerConfirmLogout: ListenerResult<Boolean> = object: ListenerResult<Boolean> {
        override fun onResult(result: Boolean) {
            viewModel.onLogoutDialogResult(result)
        }
    }
    private val listenerFeedbackDialog: ListenerResult<String> = object: ListenerResult<String> {
        override fun onResult(result: String) {
            viewModel.onFeedbackDialogResult(result)
        }
    }

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
            NAVIGATION_DIALOG_CONFIRM_LOGOUT -> {
                val dialog = DialogConfirmLogout()
                dialog.setConfirmListener(listenerConfirmLogout)
                dialog.show(this@FragmentSettings.childFragmentManager, "Confirm Logout Dialog")
            }
            NAVIGATION_GOOGLE_LOGOUT -> {
                activity?.let {
                    AuthUI.getInstance().signOut(it).addOnCompleteListener { resultTask ->
                        viewModel.onLogoutResult(resultTask.isSuccessful)
                    }
                }
            }
            NAVIGATION_WEB_PLAY_MARKET -> {
                openWebApplication(activity)
            }
            NAVIGATION_DIALOG_SEND_FEEDBACK -> {
                val dialog = DialogSendFeedback()
                dialog.setConfirmListener(listenerFeedbackDialog)
                dialog.show(this@FragmentSettings.childFragmentManager, "Confirm Logout Dialog")
            }
        }
    }
}