package relaxeddd.englishnotify.ui.notifications

import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.core.view.updatePaddingRelative
import androidx.fragment.app.viewModels
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.Navigation
import relaxeddd.englishnotify.R
import relaxeddd.englishnotify.common.*
import relaxeddd.englishnotify.common_ui_func.doOnApplyWindowInsets
import relaxeddd.englishnotify.databinding.FragmentNotificationsBinding
import relaxeddd.englishnotify.dialogs.DialogConfirmDisableNotifications
import relaxeddd.englishnotify.dialogs.DialogLearnLanguage
import relaxeddd.englishnotify.dialogs.DialogNotificationsView
import relaxeddd.englishnotify.dialogs.DialogPushOffTime
import relaxeddd.englishnotify.dialogs.DialogTestNotifications
import relaxeddd.englishnotify.preferences.Preferences
import relaxeddd.englishnotify.preferences.utils.NOTIFICATIONS_VIEW_INPUT
import relaxeddd.englishnotify.preferences.utils.NOTIFICATIONS_VIEW_STANDARD
import relaxeddd.englishnotify.view_base.BaseFragment
import relaxeddd.englishnotify.view_base.interfaces.ListenerResult
import javax.inject.Inject

class FragmentNotifications : BaseFragment<ViewModelNotifications, FragmentNotificationsBinding>() {

    @Inject
    override lateinit var prefs: Preferences

    @Inject
    lateinit var viewModelFactory: ViewModelProvider.Factory

    override val viewModel by viewModels<ViewModelNotifications> { viewModelFactory }

    private val listenerLearnEnglish: ListenerResult<Int> = object: ListenerResult<Int> {
        override fun onResult(result: Int) {
            viewModel.onDialogLearnLanguageResult(result)
        }
    }
    private val listenerNotificationsView: ListenerResult<Int> = object: ListenerResult<Int> {
        override fun onResult(result: Int) {
            viewModel.onDialogNotificationsViewResult(result)
        }
    }
    private val listenerNightTime: ListenerResult<Pair<Int, Int>> = object: ListenerResult<Pair<Int, Int>> {
        override fun onResult(result: Pair<Int, Int>) {
            viewModel.onDialogPushOffTimeResult(result)
        }
    }
    private val listenerTestNotifications: ListenerResult<Boolean> = object: ListenerResult<Boolean> {
        override fun onResult(result: Boolean) {
            viewModel.onDialogTestNotificationsResult(result)
        }
    }
    private val listenerConfirmDisableNotifications: ListenerResult<Boolean> = object: ListenerResult<Boolean> {
        override fun onResult(result: Boolean) {
            viewModel.onDialogDisableNotificationsResult(result)
        }
    }

    override fun getToolbarTitleResId() = R.string.notifications
    override fun isTopLevelFragment() = true

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        binding = FragmentNotificationsBinding.inflate(inflater)
        return binding?.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding?.apply {
            switchNotificationsEnable.setOnCheckedChangeListener(viewModel.checkedChangeListenerEnableNotifications)
            switchNotificationsDeletable.setOnCheckedChangeListener(viewModel.checkedChangeListenerDeletable)
            switchNotificationsShowOnlyOne.setOnCheckedChangeListener(viewModel.checkedChangeListenerShowOnlyOneNotification)

            scrollViewNotifications.doOnApplyWindowInsets { v, insets, padding ->
                v.updatePaddingRelative(bottom = padding.bottom + insets.systemWindowInsetBottom)
            }
            containerNotificationsRepeatTime.setOnClickListener(viewModel.clickListenerRepeatTime)
            containerNotificationsSelectCategory.setOnClickListener(viewModel.clickListenerSelectCategory)
            containerNotificationsLearnLanguage.setOnClickListener(viewModel.clickListenerLearnLanguage)
            containerNotificationsViewType.setOnClickListener(viewModel.clickListenerNotificationsView)
            containerNotificationsNightTime.setOnClickListener(viewModel.clickListenerNightTime)
            containerNotificationsTest.setOnClickListener(viewModel.clickListenerTestNotifications)
        }
    }

    override fun subscribeToViewModel() {
        super.subscribeToViewModel()

        viewModel.isNotificationsEnabled.observe(viewLifecycleOwner) {
            binding?.switchNotificationsEnable?.isChecked = it
        }
        viewModel.selectedTagLiveData.observe(viewLifecycleOwner) {
            binding?.textNotificationsCategoriesValue?.isVisible = it.isNotBlank()
            binding?.textNotificationsCategoriesValue?.text = it
        }
        viewModel.textRepeatTime.observe(viewLifecycleOwner) {
            binding?.textView7?.isVisible = it.isNotBlank()
            binding?.textView7?.text = it
        }
        viewModel.textLearnLanguage.observe(viewLifecycleOwner) {
            binding?.textNotificationLearnLanguageValue?.text = it
        }
        viewModel.textNotificationsView.observe(viewLifecycleOwner) {
            binding?.textNotificationsViewValue?.text = it
        }
        viewModel.isVisibleNotificationsView.observe(viewLifecycleOwner) {
            binding?.containerNotificationsDeletable?.isVisible = it
        }
        viewModel.isNotDeletable.observe(viewLifecycleOwner) {
            binding?.switchNotificationsDeletable?.isChecked = it
        }
        viewModel.isShowOnlyOneNotification.observe(viewLifecycleOwner) {
            binding?.switchNotificationsShowOnlyOne?.isChecked = it
        }
        viewModel.timeDurationOffValue.observe(viewLifecycleOwner) {
            binding?.textNotificationsNightTimeValue?.isVisible = it > 0
        }
        viewModel.timeStartOff.observe(viewLifecycleOwner) {
            binding?.textNotificationsNightTimeValue?.text = getString(R.string.night_time_value, it, viewModel.timeEndOff.value)
        }
        viewModel.timeEndOff.observe(viewLifecycleOwner) {
            binding?.textNotificationsNightTimeValue?.text = getString(R.string.night_time_value, viewModel.timeStartOff.value, it)
        }
    }

    override fun onNavigationEvent(eventId: Int) {
        when (eventId) {
            NAVIGATION_FRAGMENT_SELECT_CATEGORY -> {
                Navigation.findNavController(view ?: return).myNavigate(R.id.action_fragmentNotifications_to_fragmentCategories)
            }
            NAVIGATION_FRAGMENT_TIME -> {
                Navigation.findNavController(view ?: return).myNavigate(R.id.action_fragmentNotifications_to_fragmentTime)
            }
            NAVIGATION_DIALOG_LEARN_ENGLISH -> {
                val dialog = DialogLearnLanguage()
                val args = Bundle()
                args.putInt(SELECTED_ITEM, prefs.getLearnLanguageType())
                dialog.arguments = args
                dialog.listener = listenerLearnEnglish
                dialog.show(this@FragmentNotifications.childFragmentManager, "Learn Language Dialog")
            }
            NAVIGATION_DIALOG_NOTIFICATIONS_VIEW -> {
                val dialog = DialogNotificationsView()
                val args = Bundle()
                val defaultNotificationsView = if (Build.VERSION.SDK_INT < Build.VERSION_CODES.N) {
                    NOTIFICATIONS_VIEW_STANDARD
                } else {
                    NOTIFICATIONS_VIEW_INPUT
                }
                args.putInt(SELECTED_ITEM, prefs.getNotificationsView() ?: defaultNotificationsView)
                dialog.arguments = args
                dialog.listener = listenerNotificationsView
                dialog.show(this@FragmentNotifications.childFragmentManager, "Learn Language Dialog")
            }
            NAVIGATION_DIALOG_NIGHT_TIME -> {
                val dialog = DialogPushOffTime()
                val args = Bundle()
                args.putInt(START_HOUR, prefs.getStartHour())
                args.putInt(DURATION_HOURS, prefs.getDurationHours())
                dialog.arguments = args
                dialog.confirmListener = listenerNightTime
                dialog.show(this@FragmentNotifications.childFragmentManager, "Night Time Dialog")
            }
            NAVIGATION_DIALOG_TEST_NOTIFICATIONS -> {
                val dialog = DialogTestNotifications()
                dialog.confirmListener = listenerTestNotifications
                dialog.show(this@FragmentNotifications.childFragmentManager, "Test Notifications Dialog")
            }
            NAVIGATION_DIALOG_CONFIRM_DISABLE_NOTIFICATIONS -> {
                if (isResumed) {
                    val dialog = DialogConfirmDisableNotifications()
                    dialog.confirmListener = listenerConfirmDisableNotifications
                    dialog.show(this@FragmentNotifications.childFragmentManager, "Confirm disable push Dialog")
                }
            }
            else -> super.onNavigationEvent(eventId)
        }
    }
}
