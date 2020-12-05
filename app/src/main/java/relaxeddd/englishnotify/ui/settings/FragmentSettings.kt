package relaxeddd.englishnotify.ui.settings

import android.annotation.SuppressLint
import androidx.navigation.Navigation
import com.firebase.ui.auth.AuthUI
import relaxeddd.englishnotify.common.*
import relaxeddd.englishnotify.R
import relaxeddd.englishnotify.databinding.FragmentSettingsBinding
import relaxeddd.englishnotify.dialogs.*
import android.content.Intent
import android.net.Uri
import android.provider.Settings
import android.os.PowerManager
import androidx.core.content.ContextCompat.getSystemService
import android.os.Build
import android.os.Bundle
import android.view.View
import androidx.core.view.updatePaddingRelative
import com.judemanutd.autostarter.AutoStartPermissionHelper
import relaxeddd.englishnotify.model.preferences.SharedHelper
import java.lang.Exception
import kotlin.math.max
import kotlin.math.min

class FragmentSettings : BaseFragment<ViewModelSettings, FragmentSettingsBinding>() {

    private val listenerConfirmLogout: ListenerResult<Boolean> = object: ListenerResult<Boolean> {
        override fun onResult(result: Boolean) {
            viewModel.onLogoutDialogResult(result)
        }
    }

    private val listenerTheme: ListenerResult<Int> = object: ListenerResult<Int> {
        override fun onResult(result: Int) {
            if (result != SharedHelper.getAppThemeType()) {
                viewModel.onThemeUpdate(result)
                activity?.recreate()
            }
        }
    }

    private val listenerSwapProgress: ListenerResult<Boolean> = object: ListenerResult<Boolean> {
        override fun onResult(result: Boolean) {
            viewModel.onSwapProgressResult(result)
        }
    }

    override fun getLayoutResId() = R.layout.fragment_settings
    override fun getToolbarTitleResId() = R.string.common
    override fun getViewModelFactory() = InjectorUtils.provideSettingsViewModelFactory()
    override fun getViewModelClass() = ViewModelSettings::class.java
    override fun isTopLevelFragment() = true

    override fun configureBinding() {
        super.configureBinding()
        binding.viewModel = viewModel
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.scrollViewSettings.doOnApplyWindowInsets { v, insets, padding ->
            v.updatePaddingRelative(bottom = padding.bottom + insets.systemWindowInsetBottom)
        }
        binding.switchSettingsDesign.setOnCheckedChangeListener(viewModel.checkedChangeListenerNavigationDesign)
        binding.switchSettingsProgressInTraining.setOnCheckedChangeListener(viewModel.checkedChangeListenerProgressInTraining)
        binding.switchSettingsVoiceInput.setOnCheckedChangeListener(viewModel.checkedChangeListenerVoiceInput)
        binding.switchSettingsSecondaryProgress.setOnCheckedChangeListener(viewModel.checkedChangeListenerEnabledSecondaryProgress)
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
            NAVIGATION_DIALOG_SECONDARY_PROGRESS_INFO -> {
                if (isResumed) {
                    DialogSecondaryProgressInfo().show(this@FragmentSettings.childFragmentManager, "Secondary Progress Info Dialog")
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
            NAVIGATION_DIALOG_SWAP_PROGRESS -> {
                if (isResumed) {
                    val dialog = DialogSwapProgress()
                    dialog.confirmListener = listenerSwapProgress
                    dialog.show(this@FragmentSettings.childFragmentManager, "Swap Progress Dialog")
                }
            }
            NAVIGATION_DIALOG_CHECK_SAVE_WORDS -> {
                if (isResumed) {
                    val dialog = DialogCheckSaveWords()
                    dialog.confirmListener = object : ListenerResult<Boolean> {
                        override fun onResult(result: Boolean) {
                            if (result) {
                                viewModel.saveDictionary()
                            }
                        }
                    }
                    dialog.show(this@FragmentSettings.childFragmentManager, "Check save words Dialog")
                }
            }
            NAVIGATION_DIALOG_CHECK_LOAD_WORDS -> {
                if (isResumed) {
                    val dialog = DialogCheckLoadWords()
                    dialog.confirmListener = object : ListenerResult<Boolean> {
                        override fun onResult(result: Boolean) {
                            if (result) {
                                viewModel.loadDictionary()
                            }
                        }
                    }
                    dialog.show(this@FragmentSettings.childFragmentManager, "Check load words Dialog")
                }
            }
            NAVIGATION_DIALOG_TRUE_ANSWERS_TO_LEARN -> {
                val dialog = DialogTrueAnswersToLearn()
                val args = Bundle()
                val array = resources.getStringArray(R.array.array_true_answers_number_to_learn)
                val arrayIndex = min(max(SharedHelper.getTrueAnswersToLearn() - 2, 0), array.size)
                args.putInt(SELECTED_ITEM, arrayIndex)
                dialog.arguments = args
                dialog.listener = object : ListenerResult<Int> {
                    override fun onResult(result: Int) {
                        viewModel.onDialogTrueAnswersToLearnResult(result)
                    }
                }
                dialog.show(this@FragmentSettings.childFragmentManager, "True answers to learn Dialog")
            }
            NAVIGATION_DIALOG_NOTIFICATION_LEARN_POINTS -> {
                val dialog = DialogNotificationLearnPoints()
                val args = Bundle()
                val array = resources.getStringArray(R.array.array_notifications_learn_points)
                val arrayIndex = min(max(SharedHelper.getNotificationLearnPoints() - 1, 0), array.size)
                args.putInt(SELECTED_ITEM, arrayIndex)
                dialog.arguments = args
                dialog.listener = object : ListenerResult<Int> {
                    override fun onResult(result: Int) {
                        viewModel.onDialogNotificationLearnPointsResult(result)
                    }
                }
                dialog.show(this@FragmentSettings.childFragmentManager, "Notification learn points Dialog")
            }
            NAVIGATION_DIALOG_RECEIVE_HELP -> {
                val ctx = context ?: return

                DialogNotificationsNotShow().apply {
                    this.confirmListener = object : ListenerResult<Boolean> {
                        override fun onResult(result: Boolean) {
                            if (!result) {
                                return
                            }

                            if (AutoStartPermissionHelper.getInstance().isAutoStartPermissionAvailable(ctx)) {
                                val isStarted = AutoStartPermissionHelper.getInstance().getAutoStartPermission(ctx)

                                if (!isStarted) {
                                    showToast(R.string.settings_not_found)
                                }
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
                    }
                    show(this@FragmentSettings.childFragmentManager, "Notifications not Show Dialog")
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
                    val args = Bundle()
                    args.putInt(SELECTED_ITEM, SharedHelper.getAppThemeType())
                    dialog.arguments = args
                    dialog.listener = listenerTheme
                    dialog.show(this@FragmentSettings.childFragmentManager, "Theme Dialog")
                }
            }
            else -> super.onNavigationEvent(eventId)
        }
    }
}
