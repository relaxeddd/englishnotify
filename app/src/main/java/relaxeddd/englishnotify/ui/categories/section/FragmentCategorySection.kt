package relaxeddd.englishnotify.ui.categories.section

import androidx.core.view.updatePaddingRelative
import relaxeddd.englishnotify.R
import relaxeddd.englishnotify.common.BaseFragment
import relaxeddd.englishnotify.common.InjectorUtils
import relaxeddd.englishnotify.common.doOnApplyWindowInsets
import relaxeddd.englishnotify.databinding.FragmentCategorySectionBinding
import relaxeddd.englishnotify.ui.categories.AdapterCategories
import relaxeddd.englishnotify.ui.categories.CategorySection

class FragmentCategorySection(val type: CategorySection) : BaseFragment<ViewModelCategorySection, FragmentCategorySectionBinding>() {

    private lateinit var adapter: AdapterCategories

    override fun getLayoutResId() = R.layout.fragment_category_section
    override fun getViewModelFactory() = InjectorUtils.provideCategorySectionViewModelFactory(type)
    override fun getViewModelClass() = ViewModelCategorySection::class.java

    override fun configureBinding() {
        super.configureBinding()
        adapter = AdapterCategories(viewModel)
        binding.recyclerViewCategories.adapter = adapter
        binding.recyclerViewCategories.setHasFixedSize(true)
        viewModel.title.observe(viewLifecycleOwner, {
            updateToolbarTitle(it)
        })
        viewModel.categories.observe(viewLifecycleOwner, { items ->
            if (items != null && items.isNotEmpty()) {
                adapter.submitList(items)
            }
        })

        binding.recyclerViewCategories.doOnApplyWindowInsets { v, insets, padding ->
            v.updatePaddingRelative(bottom = padding.bottom + insets.systemWindowInsetBottom)
        }
    }

    fun onAcceptClicked() {
        viewModel.onClickAccept()
    }
}
