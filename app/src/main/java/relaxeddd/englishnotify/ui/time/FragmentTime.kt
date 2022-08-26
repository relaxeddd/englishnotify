package relaxeddd.englishnotify.ui.time

import android.os.Bundle
import android.view.LayoutInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.widget.RadioButton
import androidx.fragment.app.viewModels
import androidx.lifecycle.ViewModelProvider
import relaxeddd.englishnotify.R
import relaxeddd.englishnotify.common.NAVIGATION_ACTIVITY_BACK
import relaxeddd.englishnotify.databinding.FragmentTimeBinding
import relaxeddd.englishnotify.preferences.Preferences
import relaxeddd.englishnotify.view_base.BaseFragment
import javax.inject.Inject

class FragmentTime : BaseFragment<ViewModelTime, FragmentTimeBinding>() {

    @Inject
    override lateinit var prefs: Preferences

    @Inject
    lateinit var viewModelFactory: ViewModelProvider.Factory

    override val viewModel by viewModels<ViewModelTime> { viewModelFactory }

    override fun getToolbarTitleResId() = R.string.receive_notifications_time
    override fun getMenuResId() = R.menu.menu_accept
    override fun isHomeMenuButtonEnabled() = true
    override fun getHomeMenuButtonIconResId() = R.drawable.ic_back
    override fun getHomeMenuButtonListener(): () -> Unit = {
        onNavigationEvent(NAVIGATION_ACTIVITY_BACK)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        binding = FragmentTimeBinding.inflate(inflater)
        return binding?.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding?.apply {
            val checkedRadioButton = radioGroupTime.getChildAt(viewModel.receiveNotificationsTime)

            radioGroupTime.setOnCheckedChangeListener { view, _ ->
                val radioButton = view.findViewById<RadioButton>(view?.checkedRadioButtonId ?: 0)
                viewModel.onNotificationTimeChanged((radioButton?.tag as String).toInt())
            }
            if (checkedRadioButton != null) {
                radioGroupTime.check(checkedRadioButton.id)
            }
        }
    }

    override fun onOptionsItemSelected(item: MenuItem) = when (item.itemId) {
        R.id.item_menu_accept -> {
            viewModel.onClickAccept()
            true
        }
        else -> super.onOptionsItemSelected(item)
    }
}
