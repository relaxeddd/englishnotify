package relaxeddd.englishnotify.ui.settings

import com.firebase.ui.auth.AuthUI
import relaxeddd.englishnotify.common.*
import relaxeddd.englishnotify.R
import relaxeddd.englishnotify.databinding.FragmentSettingsBinding
import relaxeddd.englishnotify.dialogs.DialogAppAbout
import relaxeddd.englishnotify.dialogs.DialogConfirmLogout
import relaxeddd.englishnotify.dialogs.DialogSendFeedback
import relaxeddd.englishnotify.dialogs.DialogSubscription
import relaxeddd.englishnotify.ui.main.MainActivity

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
    private val listenerSubscription: ListenerResult<Int> = object: ListenerResult<Int> {
        override fun onResult(result: Int) {
            val activity = activity

            if (activity != null && activity is MainActivity) {
                activity.onChooseSub(result)
            }
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
                dialog.show(this@FragmentSettings.childFragmentManager, "Send feedback Dialog")
            }
            NAVIGATION_DIALOG_SUBSCRIPTION -> {
                val activity = activity

                if (activity is MainActivity) {
                    activity.setLoadingVisible(true)
                    activity.initBilling(object: ListenerResult<Boolean> {
                        override fun onResult(result: Boolean) {
                            val freshActivity = this@FragmentSettings.activity

                            if (freshActivity != null && freshActivity is MainActivity) {
                                activity.setLoadingVisible(false)
                                if (result) {
                                    val dialog = DialogSubscription()
                                    dialog.listener = listenerSubscription
                                    dialog.show(this@FragmentSettings.childFragmentManager, "Subscription Dialog")
                                } else {
                                    showToast(R.string.error_purchase)
                                }
                            }
                        }
                    })
                }
            }
        }
    }
}