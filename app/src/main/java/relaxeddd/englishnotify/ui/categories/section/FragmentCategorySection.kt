package relaxeddd.englishnotify.ui.categories.section

import android.view.MenuItem
import androidx.lifecycle.Observer
import relaxeddd.englishnotify.R
import relaxeddd.englishnotify.common.BaseFragment
import relaxeddd.englishnotify.common.InjectorUtils
import relaxeddd.englishnotify.common.NAVIGATION_ACTIVITY_BACK
import relaxeddd.englishnotify.databinding.FragmentCategorySectionBinding
import relaxeddd.englishnotify.ui.categories.AdapterCategories
import relaxeddd.englishnotify.ui.categories.CategorySection

class FragmentCategorySection(val type: CategorySection) : BaseFragment<ViewModelCategorySection, FragmentCategorySectionBinding>() {

    private lateinit var adapter: AdapterCategories

    override fun getLayoutResId() = R.layout.fragment_category_section
    override fun getViewModelFactory() = InjectorUtils.provideCategorySectionViewModelFactory(type)
    override fun getViewModelClass() = ViewModelCategorySection::class.java
    override fun getMenuResId() = R.menu.menu_accept
    override fun getToolbarElevation() = 0f
    override fun isHomeMenuButtonEnabled() = true
    override fun getHomeMenuButtonIconResId() = R.drawable.ic_back
    override fun getHomeMenuButtonListener(): () -> Unit = {
        onNavigationEvent(NAVIGATION_ACTIVITY_BACK)
    }

    override fun configureBinding() {
        super.configureBinding()
        adapter = AdapterCategories(viewModel)
        binding.recyclerViewCategories.adapter = adapter
        binding.recyclerViewCategories.itemAnimator = null
        viewModel.title.observe(viewLifecycleOwner, Observer {
            updateToolbarTitle(it)
        })
        viewModel.categories.observe(viewLifecycleOwner, Observer { items ->
            if (items != null && items.isNotEmpty()) {
                adapter.submitList(items)
            }
        })
    }

    override fun onResume() {
        super.onResume()
        viewModel.updateCategories()
    }

    override fun onOptionsItemSelected(item: MenuItem) = when (item.itemId) {
        R.id.item_menu_accept -> {
            viewModel.onClickAccept()
            true
        }
        else -> super.onOptionsItemSelected(item)
    }
}