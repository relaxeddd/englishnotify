package relaxeddd.englishnotify.ui.notifications

import android.os.Bundle
import android.view.View
import androidx.core.view.updatePaddingRelative
import androidx.navigation.Navigation
import relaxeddd.englishnotify.common.*
import relaxeddd.englishnotify.R
import relaxeddd.englishnotify.databinding.FragmentNotificationsBinding
import relaxeddd.englishnotify.dialogs.*
import relaxeddd.englishnotify.model.preferences.SharedHelper

class FragmentNotifications : BaseFragment<ViewModelNotifications, FragmentNotificationsBinding>() {

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

    override fun getLayoutResId() = R.layout.fragment_notifications
    override fun getToolbarTitleResId() = R.string.notifications
    override fun getViewModelFactory() = InjectorUtils.provideNotificationsViewModelFactory()
    override fun getViewModelClass() = ViewModelNotifications::class.java
    override fun isTopLevelFragment() = true

    override fun configureBinding() {
        super.configureBinding()
        binding?.viewModel = viewModel
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val binding = binding ?: return
        binding.switchNotificationsEnable.setOnCheckedChangeListener(viewModel.checkedChangeListenerEnableNotifications)
        binding.switchNotificationsDeletable.setOnCheckedChangeListener(viewModel.checkedChangeListenerDeletable)
        binding.switchNotificationsShowOnlyOne.setOnCheckedChangeListener(viewModel.checkedChangeListenerShowOnlyOneNotification)

        binding.scrollViewNotifications.doOnApplyWindowInsets { v, insets, padding ->
            v.updatePaddingRelative(bottom = padding.bottom + insets.systemWindowInsetBottom)
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
                args.putInt(SELECTED_ITEM, SharedHelper.getLearnLanguageType())
                dialog.arguments = args
                dialog.listener = listenerLearnEnglish
                dialog.show(this@FragmentNotifications.childFragmentManager, "Learn Language Dialog")
            }
            NAVIGATION_DIALOG_NOTIFICATIONS_VIEW -> {
                val dialog = DialogNotificationsView()
                val args = Bundle()
                args.putInt(SELECTED_ITEM, SharedHelper.getNotificationsView())
                dialog.arguments = args
                dialog.listener = listenerNotificationsView
                dialog.show(this@FragmentNotifications.childFragmentManager, "Learn Language Dialog")
            }
            NAVIGATION_DIALOG_NIGHT_TIME -> {
                val dialog = DialogPushOffTime()
                val args = Bundle()
                args.putInt(START_HOUR, SharedHelper.getStartHour())
                args.putInt(DURATION_HOURS, SharedHelper.getDurationHours())
                dialog.arguments = args
                dialog.confirmListener = listenerNightTime
                dialog.show(this@FragmentNotifications.childFragmentManager, "Night Time Dialog")
            }
            NAVIGATION_DIALOG_TEST_NOTIFICATIONS -> {
                val dialog = DialogTestNotifications()
                val args = Bundle()
                args.putInt(COUNT, viewModel.user.value?.testCount ?: 0)
                dialog.arguments = args
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
