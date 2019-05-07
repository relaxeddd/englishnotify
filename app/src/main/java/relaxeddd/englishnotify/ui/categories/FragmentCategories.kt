package relaxeddd.englishnotify.ui.categories

import android.view.MenuItem
import androidx.lifecycle.Observer
import relaxeddd.englishnotify.R
import relaxeddd.englishnotify.common.BaseFragment
import relaxeddd.englishnotify.common.InjectorUtils
import relaxeddd.englishnotify.common.NAVIGATION_ACTIVITY_BACK
import relaxeddd.englishnotify.databinding.FragmentCategoriesBinding

class FragmentCategories : BaseFragment<ViewModelCategories, FragmentCategoriesBinding>() {

    private lateinit var adapter: AdapterCategories

    override fun getLayoutResId() = R.layout.fragment_categories
    override fun getToolbarTitleResId() = R.string.word_category_select
    override fun getViewModelFactory() = InjectorUtils.provideCategoriesViewModelFactory()
    override fun getViewModelClass() = ViewModelCategories::class.java
    override fun getMenuResId() = R.menu.menu_accept
    override fun isHomeMenuButtonEnabled() = true
    override fun getHomeMenuButtonIconResId() = R.drawable.ic_back
    override fun getHomeMenuButtonListener(): () -> Unit = {
        onNavigationEvent(NAVIGATION_ACTIVITY_BACK)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.item_menu_accept -> {
                viewModel.onClickAccept()
                return true
            }
            else -> return super.onOptionsItemSelected(item)
        }
    }

    override fun configureBinding() {
        super.configureBinding()
        adapter = AdapterCategories(viewModel)
        binding.viewModel = viewModel
        binding.recyclerViewCategories.adapter = adapter
        viewModel.categories.observe(viewLifecycleOwner, Observer { items ->
            if (items != null && items.isNotEmpty()) adapter.submitList(items)
        })
    }
}