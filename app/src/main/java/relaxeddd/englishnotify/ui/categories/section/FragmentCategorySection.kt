package relaxeddd.englishnotify.ui.categories.section

import androidx.core.view.updatePaddingRelative
import androidx.fragment.app.viewModels
import relaxeddd.englishnotify.R
import relaxeddd.englishnotify.common.BaseFragment
import relaxeddd.englishnotify.common.doOnApplyWindowInsets
import relaxeddd.englishnotify.databinding.FragmentCategorySectionBinding
import relaxeddd.englishnotify.ui.categories.AdapterCategories

class FragmentCategorySection : BaseFragment<ViewModelCategorySection, FragmentCategorySectionBinding>() {

    private lateinit var adapter: AdapterCategories

    override fun getLayoutResId() = R.layout.fragment_category_section

    override val viewModel: ViewModelCategorySection by viewModels()

    override fun subscribeToViewModel() {
        super.subscribeToViewModel()
        adapter = AdapterCategories(viewModel)
        val binding = binding ?: return
        binding.recyclerViewCategories.adapter = adapter
        binding.recyclerViewCategories.setHasFixedSize(true)
        viewModel.title.observe(viewLifecycleOwner) {
            updateToolbarTitle(it)
        }
        viewModel.categories.observe(viewLifecycleOwner) { items ->
            if (items != null && items.isNotEmpty()) {
                adapter.submitList(items)
            }
        }

        binding.recyclerViewCategories.doOnApplyWindowInsets { v, insets, padding ->
            v.updatePaddingRelative(bottom = padding.bottom + insets.systemWindowInsetBottom)
        }
    }

    fun onAcceptClicked() {
        viewModel.onClickAccept()
    }
}
