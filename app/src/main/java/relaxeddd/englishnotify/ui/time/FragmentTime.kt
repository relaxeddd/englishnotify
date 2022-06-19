package relaxeddd.englishnotify.ui.time

import android.os.Bundle
import android.view.MenuItem
import android.view.View
import androidx.fragment.app.viewModels
import relaxeddd.englishnotify.R
import relaxeddd.englishnotify.common.BaseFragment
import relaxeddd.englishnotify.common.NAVIGATION_ACTIVITY_BACK
import relaxeddd.englishnotify.databinding.FragmentTimeBinding

class FragmentTime : BaseFragment<ViewModelTime, FragmentTimeBinding>() {

    override fun getLayoutResId() = R.layout.fragment_time
    override fun getToolbarTitleResId() = R.string.receive_notifications_time
    override fun getMenuResId() = R.menu.menu_accept
    override fun isHomeMenuButtonEnabled() = true
    override fun getHomeMenuButtonIconResId() = R.drawable.ic_back
    override fun getHomeMenuButtonListener(): () -> Unit = {
        onNavigationEvent(NAVIGATION_ACTIVITY_BACK)
    }

    override val viewModel: ViewModelTime by viewModels()

    override fun subscribeToViewModel() {
        super.subscribeToViewModel()
        binding?.viewModel = viewModel
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val checkedRadioButton = binding?.radioGroupTime?.getChildAt(viewModel.receiveNotificationsTime)

        binding?.radioGroupTime?.setOnCheckedChangeListener(viewModel.checkedChangeListenerTime)
        if (checkedRadioButton != null) {
            binding?.radioGroupTime?.check(checkedRadioButton.id)
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
