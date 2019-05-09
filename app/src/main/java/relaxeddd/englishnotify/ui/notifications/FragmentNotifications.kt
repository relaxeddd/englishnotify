package relaxeddd.englishnotify.ui.notifications

import android.os.Bundle
import androidx.navigation.Navigation
import relaxeddd.englishnotify.common.*
import relaxeddd.englishnotify.R
import relaxeddd.englishnotify.databinding.FragmentNotificationsBinding
import relaxeddd.englishnotify.dialogs.*

class FragmentNotifications : BaseFragment<ViewModelNotifications, FragmentNotificationsBinding>() {

    private val listenerLearnEnglish: ListenerResult<Int> = object: ListenerResult<Int> {
        override fun onResult(result: Int) {
            viewModel.onDialogLearnLanguageResult(result)
        }
    }
    private val listenerRepeatTime: ListenerResult<Int> = object: ListenerResult<Int> {
        override fun onResult(result: Int) {
            viewModel.onDialogRepeatTimeResult(result)
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

    override fun getLayoutResId() = R.layout.fragment_notifications
    override fun getToolbarTitleResId() = R.string.notifications
    override fun getViewModelFactory() = InjectorUtils.provideNotificationsViewModelFactory()
    override fun getViewModelClass() = ViewModelNotifications::class.java

    override fun configureBinding() {
        super.configureBinding()
        binding.viewModel = viewModel
        binding.clickListenerChangeCategory = Navigation.createNavigateOnClickListener(R.id.action_fragmentNotifications_to_fragmentCategories)
        binding.listRepeatTime = resources.getStringArray(R.array.array_time_repeat)
        binding.listLearnLanguage = resources.getStringArray(R.array.array_learn_language)
        binding.listNotificationsView = resources.getStringArray(R.array.array_notifications_view)
    }

    override fun onNavigationEvent(eventId: Int) {
        when (eventId) {
            NAVIGATION_DIALOG_LEARN_ENGLISH -> {
                val dialog = DialogLearnLanguage()
                val args = Bundle()
                args.putInt(SELECTED_ITEM, viewModel.user.value?.learnLanguageType ?: 0)
                dialog.arguments = args
                dialog.listener = listenerLearnEnglish
                dialog.show(this@FragmentNotifications.childFragmentManager, "Learn Language Dialog")
            }
            NAVIGATION_DIALOG_REPEAT -> {
                val dialog = DialogSelectRepeatTime()
                val args = Bundle()
                args.putInt(SELECTED_ITEM, viewModel.user.value?.notificationsTimeType ?: 0)
                dialog.arguments = args
                dialog.listener = listenerRepeatTime
                dialog.show(this@FragmentNotifications.childFragmentManager, "Repeat Dialog")
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
            else -> super.onNavigationEvent(eventId)
        }
    }
}