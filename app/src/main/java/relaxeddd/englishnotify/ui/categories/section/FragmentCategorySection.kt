package relaxeddd.englishnotify.ui.categories.section

import android.os.Build
import androidx.core.view.updatePaddingRelative
import androidx.lifecycle.Observer
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
        viewModel.title.observe(viewLifecycleOwner, Observer {
            updateToolbarTitle(it)
        })
        viewModel.categories.observe(viewLifecycleOwner, Observer { items ->
            if (items != null && items.isNotEmpty()) {
                adapter.submitList(items)
            }
        })

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            binding.recyclerViewCategories.doOnApplyWindowInsets { v, insets, padding ->
                v.updatePaddingRelative(bottom = padding.bottom + insets.systemWindowInsetBottom)
            }
        }
    }

    fun onAcceptClicked() {
        viewModel.onClickAccept()
    }
}
