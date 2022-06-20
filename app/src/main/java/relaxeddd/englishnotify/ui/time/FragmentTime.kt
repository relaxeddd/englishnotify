package relaxeddd.englishnotify.ui.time

import android.os.Bundle
import android.view.LayoutInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import relaxeddd.englishnotify.R
import relaxeddd.englishnotify.common.BaseFragment
import relaxeddd.englishnotify.common.NAVIGATION_ACTIVITY_BACK
import relaxeddd.englishnotify.databinding.FragmentTimeBinding

class FragmentTime : BaseFragment<ViewModelTime, FragmentTimeBinding>() {

    override fun getToolbarTitleResId() = R.string.receive_notifications_time
    override fun getMenuResId() = R.menu.menu_accept
    override fun isHomeMenuButtonEnabled() = true
    override fun getHomeMenuButtonIconResId() = R.drawable.ic_back
    override fun getHomeMenuButtonListener(): () -> Unit = {
        onNavigationEvent(NAVIGATION_ACTIVITY_BACK)
    }

    override val viewModel: ViewModelTime by viewModels()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        binding = FragmentTimeBinding.inflate(inflater)
        return binding?.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding?.apply {
            val checkedRadioButton = radioGroupTime.getChildAt(viewModel.receiveNotificationsTime)

            radioGroupTime.setOnCheckedChangeListener(viewModel.checkedChangeListenerTime)
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
