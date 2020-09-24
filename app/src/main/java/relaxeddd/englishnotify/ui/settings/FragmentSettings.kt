package relaxeddd.englishnotify.ui.settings

import android.annotation.SuppressLint
import androidx.navigation.Navigation
import com.firebase.ui.auth.AuthUI
import relaxeddd.englishnotify.common.*
import relaxeddd.englishnotify.R
import relaxeddd.englishnotify.databinding.FragmentSettingsBinding
import relaxeddd.englishnotify.dialogs.*
import relaxeddd.englishnotify.ui.main.MainActivity
import android.content.Intent
import android.net.Uri
import android.provider.Settings
import android.os.PowerManager
import androidx.core.content.ContextCompat.getSystemService
import android.os.Build
import com.judemanutd.autostarter.AutoStartPermissionHelper
import java.lang.Exception

class FragmentSettings : BaseFragment<ViewModelSettings, FragmentSettingsBinding>() {

    private val listenerConfirmLogout: ListenerResult<Boolean> = object: ListenerResult<Boolean> {
        override fun onResult(result: Boolean) {
            viewModel.onLogoutDialogResult(result)
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
    override fun getToolbarTitleResId() = R.string.common
    override fun getViewModelFactory() = InjectorUtils.provideSettingsViewModelFactory()
    override fun getViewModelClass() = ViewModelSettings::class.java

    override fun configureBinding() {
        super.configureBinding()
        binding.viewModel = viewModel
    }

    @SuppressLint("BatteryLife")
    override fun onNavigationEvent(eventId: Int) {
        when (eventId) {
            NAVIGATION_FRAGMENT_STATISTIC -> {
                Navigation.findNavController(view ?: return).myNavigate(R.id.action_fragmentSettings_to_fragmentStatistic)
            }
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
            NAVIGATION_DIALOG_OWN_CATEGORY -> {
                if (isResumed) {
                    DialogOwnCategory().show(this@FragmentSettings.childFragmentManager, "Check tags Dialog")
                }
            }
            NAVIGATION_DIALOG_INFO_TRAINING -> {
                if (isResumed) {
                    DialogInfoTraining().show(this@FragmentSettings.childFragmentManager, "Info Training Dialog")
                }
            }
            NAVIGATION_DIALOG_RECEIVE_HELP -> {
                val ctx = context ?: return

                if (AutoStartPermissionHelper.getInstance().isAutoStartPermissionAvailable(ctx)) {
                    AutoStartPermissionHelper.getInstance().getAutoStartPermission(ctx)
                } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    val pkg = ctx.packageName
                    val pm = getSystemService(ctx, PowerManager::class.java) ?: return

                    if (!pm.isIgnoringBatteryOptimizations(pkg)) {
                        try {
                            startActivity(Intent(Settings.ACTION_REQUEST_IGNORE_BATTERY_OPTIMIZATIONS).setData(Uri.parse("package:$pkg")))
                        } catch (e: Exception) {}
                    } else {
                        DialogInfoReceiveHelp().show(this@FragmentSettings.childFragmentManager, "Receive help Dialog")
                    }
                }
            }
            NAVIGATION_DIALOG_CONFIRM_LOGOUT -> {
                if (isResumed) {
                    val dialog = DialogConfirmLogout()
                    dialog.confirmListener = listenerConfirmLogout
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
            NAVIGATION_DIALOG_THEME -> {
                if (isResumed) {
                    val dialog = DialogAppTheme()
                    dialog.show(this@FragmentSettings.childFragmentManager, "Theme Dialog")
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