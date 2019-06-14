package relaxeddd.englishnotify.ui.rating

import android.os.Bundle
import android.os.Handler
import android.view.MenuItem
import android.view.View
import androidx.recyclerview.widget.LinearLayoutManager
import kotlinx.android.synthetic.main.fragment_rating.*
import relaxeddd.englishnotify.R
import relaxeddd.englishnotify.common.BaseFragment
import relaxeddd.englishnotify.common.InjectorUtils
import relaxeddd.englishnotify.common.NAVIGATION_ACTIVITY_BACK
import relaxeddd.englishnotify.databinding.FragmentRatingBinding
import relaxeddd.englishnotify.dialogs.DialogInfoRating

class FragmentRating : BaseFragment<ViewModelRating, FragmentRatingBinding>() {

    private lateinit var adapter: AdapterRating

    override fun getLayoutResId() = R.layout.fragment_rating
    override fun getToolbarTitleResId() = R.string.rating_users
    override fun getViewModelFactory() = InjectorUtils.provideRatingViewModelFactory()
    override fun getViewModelClass() = ViewModelRating::class.java
    override fun getMenuResId() = R.menu.menu_fragment_rating
    override fun isHomeMenuButtonEnabled() = true
    override fun getHomeMenuButtonIconResId() = R.drawable.ic_back
    override fun getHomeMenuButtonListener(): () -> Unit = { onNavigationEvent(NAVIGATION_ACTIVITY_BACK) }

    override fun configureBinding() {
        super.configureBinding()
        binding.viewModel = viewModel
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        adapter = AdapterRating()
        recycler_view_rating.layoutManager = LinearLayoutManager(context)
        recycler_view_rating.isNestedScrollingEnabled = false
        recycler_view_rating.adapter = adapter
        Handler().postDelayed({ adapter.submitList(viewModel.rating) }, 400)
    }

    override fun onOptionsItemSelected(item: MenuItem) = when (item.itemId) {
        R.id.item_menu_rating_info -> {
            DialogInfoRating().show(this@FragmentRating.childFragmentManager, "Rating Info Dialog")
            true
        }
        else -> super.onOptionsItemSelected(item)
    }
}