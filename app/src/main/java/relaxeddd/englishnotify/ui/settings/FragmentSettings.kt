package relaxeddd.englishnotify.ui.settings

import com.firebase.ui.auth.AuthUI
import relaxeddd.englishnotify.common.*
import relaxeddd.englishnotify.R
import relaxeddd.englishnotify.databinding.FragmentSettingsBinding
import relaxeddd.englishnotify.dialogs.*
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
    override fun getViewModelFactory() = InjectorUtils.provideSettingsViewModelFactory()
    override fun getViewModelClass() = ViewModelSettings::class.java

    override fun configureBinding() {
        super.configureBinding()
        binding.viewModel = viewModel
    }

    override fun onNavigationEvent(eventId: Int) {
        when (eventId) {
            NAVIGATION_DIALOG_APP_ABOUT -> {
                if (isResumed) {
                    DialogAppAbout().show(this@FragmentSettings.childFragmentManager, "App Info Dialog")
                }
            }
            NAVIGATION_DIALOG_SUBSCRIPTION_INFO -> {
                if (isResumed) {
                    DialogSubscriptionInfo().show(this@FragmentSettings.childFragmentManager, "Sub Info Dialog")
                }
            }
            NAVIGATION_DIALOG_INFO_TRAINING -> {
                if (isResumed) {
                    DialogInfoTraining().show(this@FragmentSettings.childFragmentManager, "Info Training Dialog")
                }
            }
            NAVIGATION_DIALOG_CONFIRM_LOGOUT -> {
                if (isResumed) {
                    val dialog = DialogConfirmLogout()
                    dialog.setConfirmListener(listenerConfirmLogout)
                    dialog.show(this@FragmentSettings.childFragmentManager, "Confirm Logout Dialog")
                }
            }
            NAVIGATION_GOOGLE_LOGOUT -> {
                if (isResumed) {
                    activity?.let {
                        AuthUI.getInstance().signOut(it).addOnCompleteListener { resultTask ->
                            viewModel.onLogoutResult(resultTask.isSuccessful)
                        }
                    }
                }
            }
            NAVIGATION_WEB_PLAY_MARKET -> {
                openWebApplication(activity)
            }
            NAVIGATION_DIALOG_SEND_FEEDBACK -> {
                if (isResumed) {
                    val dialog = DialogSendFeedback()
                    dialog.setConfirmListener(listenerFeedbackDialog)
                    dialog.show(this@FragmentSettings.childFragmentManager, "Send feedback Dialog")
                }
            }
            NAVIGATION_DIALOG_SUBSCRIPTION -> {
                val activity = activity

                if (isResumed && activity is MainActivity) {
                    if (activity.isBillingInit) {
                        val dialog = DialogSubscription()
                        dialog.listener = listenerSubscription
                        dialog.show(this@FragmentSettings.childFragmentManager, "Subscription Dialog")
                    } else {
                        activity.initBilling(object: ListenerResult<Boolean> {
                            override fun onResult(result: Boolean) {
                                val freshActivity = this@FragmentSettings.activity

                                if (isResumed && freshActivity != null && freshActivity is MainActivity) {
                                    if (result) {
                                        freshActivity.isBillingInit = true

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
            else -> super.onNavigationEvent(eventId)
        }
    }
}