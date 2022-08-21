package relaxeddd.englishnotify.screen_settings.ui

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.updatePaddingRelative
import androidx.navigation.Navigation
import relaxeddd.englishnotify.common.*
import relaxeddd.englishnotify.common_di.Injector
import relaxeddd.englishnotify.common_ui_func.doOnApplyWindowInsets
import relaxeddd.englishnotify.common_ui_func.openWebApplication
import relaxeddd.englishnotify.screen_settings.R
import relaxeddd.englishnotify.screen_settings.databinding.FragmentSettingsBinding
import relaxeddd.englishnotify.screen_settings.ui.di.SettingsComponent
import relaxeddd.englishnotify.view_base.BaseDaggerlessFragment
import relaxeddd.englishnotify.view_base.interfaces.ListenerResult
import relaxeddd.englishnotify.view_base.propertyViaViewModel
import kotlin.math.max
import kotlin.math.min

class FragmentSettings : BaseDaggerlessFragment<ViewModelSettings, FragmentSettingsBinding>() {

    private val component by propertyViaViewModel { Injector.getComponent(this, SettingsComponent::class.java) }

    override val prefs by lazy(LazyThreadSafetyMode.NONE) { component.prefs }
    override val viewModel by lazy(LazyThreadSafetyMode.NONE) { component.viewModel }

    private val listenerTheme: ListenerResult<Int> = object: ListenerResult<Int> {
        override fun onResult(result: Int) {
            if (result != prefs.getAppThemeType()) {
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

    override fun getToolbarTitleResId() = R.string.common
    override fun isTopLevelFragment() = true

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        binding = FragmentSettingsBinding.inflate(inflater)
        return binding?.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding?.apply {
            scrollViewSettings.doOnApplyWindowInsets { v, insets, padding ->
                v.updatePaddingRelative(bottom = padding.bottom + insets.systemWindowInsetBottom)
            }
            switchSettingsBottomNavigation.setOnCheckedChangeListener(viewModel.checkedChangeListenerNavigationDesign)
            switchSettingsShowProgressInTraining.setOnCheckedChangeListener(viewModel.checkedChangeListenerProgressInTraining)
            switchSettingsShowVoiceInput.setOnCheckedChangeListener(viewModel.checkedChangeListenerVoiceInput)
            switchSettingsSecondaryProgress.setOnCheckedChangeListener(viewModel.checkedChangeListenerEnabledSecondaryProgress)

            containerSettingsStatistic.setOnClickListener(viewModel.clickListenerStatistic)
            containerSettingsAddMultipleWords.setOnClickListener(viewModel.clickListenerAddMultipleWords)
            containerSettingsTrueAnswersToLearn.setOnClickListener(viewModel.clickListenerTrueAnswersToLearn)
            containerSettingsNotificationLearnPoints.setOnClickListener(viewModel.clickListenerNotificationLearnPoints)
            containerSettingsSecondaryProgressInfo.setOnClickListener(viewModel.clickListenerSecondaryProgressInfo)
            containerSettingsSwapProgress.setOnClickListener(viewModel.clickListenerSwapProgress)
            containerSettingsTheme.setOnClickListener(viewModel.clickListenerTheme)
            containerSettingsRateApp.setOnClickListener(viewModel.clickListenerRateApp)
            containerSettingsInfoTraining.setOnClickListener(viewModel.clickListenerInfoTraining)
            containerSettingsAppInfo.setOnClickListener(viewModel.clickListenerAppInfo)
            containerSettingsUpdatesHistory.setOnClickListener(viewModel.clickListenerUpdatesHistory)
        }
    }

    override fun subscribeToViewModel() {
        super.subscribeToViewModel()

        viewModel.isShowProgressInTraining.observe(viewLifecycleOwner) {
            binding?.switchSettingsShowProgressInTraining?.isChecked = it
        }
        viewModel.isEnableSecondaryProgress.observe(viewLifecycleOwner) {
            binding?.switchSettingsSecondaryProgress?.isChecked = it
        }
        viewModel.isShowVoiceInput.observe(viewLifecycleOwner) {
            binding?.switchSettingsShowVoiceInput?.isChecked = it
        }
        viewModel.isBottomNavigation.observe(viewLifecycleOwner) {
            binding?.switchSettingsBottomNavigation?.isChecked = it
        }
        viewModel.textTrueAnswersToLearn.observe(viewLifecycleOwner) {
            binding?.textSettingsTrueAnswersToLearnValue?.text = it
        }
        viewModel.textNotificationsLearnPoints.observe(viewLifecycleOwner) {
            binding?.textSettingsNotificationLearnPointsValue?.text = it
        }
        viewModel.textTheme.observe(viewLifecycleOwner) {
            binding?.textSettingsThemeValue?.text = it
        }
    }

    @SuppressLint("BatteryLife")
    override fun onNavigationEvent(eventId: Int) {
        when (eventId) {
            NAVIGATION_FRAGMENT_PARSE -> {
                Navigation.findNavController(view ?: return).navigate(
                    relaxeddd.englishnotify.view_base.R.id.action_fragmentSettings_to_fragmentParse
                )
            }
            NAVIGATION_FRAGMENT_STATISTIC -> {
                Navigation.findNavController(view ?: return).navigate(
                    relaxeddd.englishnotify.view_base.R.id.action_fragmentSettings_to_fragmentStatistic
                )
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
            NAVIGATION_DIALOG_TRUE_ANSWERS_TO_LEARN -> {
                val dialog = DialogTrueAnswersToLearn()
                val args = Bundle()
                val array = resources.getStringArray(R.array.array_true_answers_number_to_learn)
                val arrayIndex = min(max(prefs.getTrueAnswersToLearn() - 2, 0), array.size)
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
                val arrayIndex = min(max(prefs.getNotificationLearnPoints() - 1, 0), array.size)
                args.putInt(SELECTED_ITEM, arrayIndex)
                dialog.arguments = args
                dialog.listener = object : ListenerResult<Int> {
                    override fun onResult(result: Int) {
                        viewModel.onDialogNotificationLearnPointsResult(result)
                    }
                }
                dialog.show(this@FragmentSettings.childFragmentManager, "Notification learn points Dialog")
            }
            NAVIGATION_WEB_PLAY_MARKET -> {
                openWebApplication(activity)
            }
            NAVIGATION_DIALOG_THEME -> {
                if (isResumed) {
                    val dialog = DialogAppTheme()
                    val args = Bundle()
                    args.putInt(SELECTED_ITEM, prefs.getAppThemeType())
                    dialog.arguments = args
                    dialog.listener = listenerTheme
                    dialog.show(this@FragmentSettings.childFragmentManager, "Theme Dialog")
                }
            }
            else -> super.onNavigationEvent(eventId)
        }
    }
}
