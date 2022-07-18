package relaxeddd.englishnotify.ui.categories.section

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.updatePaddingRelative
import androidx.fragment.app.viewModels
import relaxeddd.englishnotify.common.doOnApplyWindowInsets
import relaxeddd.englishnotify.databinding.FragmentCategorySectionBinding
import relaxeddd.englishnotify.ui.categories.AdapterCategories
import relaxeddd.englishnotify.view_base.BaseFragment

class FragmentCategorySection : BaseFragment<ViewModelCategorySection, FragmentCategorySectionBinding>() {

    private var adapter: AdapterCategories? = null

    override val viewModel: ViewModelCategorySection by viewModels()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        binding = FragmentCategorySectionBinding.inflate(inflater)
        return binding?.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding?.apply {
            adapter = AdapterCategories(viewModel)

            recyclerViewCategories.adapter = adapter
            recyclerViewCategories.setHasFixedSize(true)
            recyclerViewCategories.doOnApplyWindowInsets { v, insets, padding ->
                v.updatePaddingRelative(bottom = padding.bottom + insets.systemWindowInsetBottom)
            }
        }
    }

    override fun subscribeToViewModel() {
        super.subscribeToViewModel()

        viewModel.title.observe(viewLifecycleOwner) {
            updateToolbarTitle(it)
        }
        viewModel.categories.observe(viewLifecycleOwner) { items ->
            if (items != null && items.isNotEmpty()) {
                adapter?.submitList(items)
            }
        }
    }

    fun onAcceptClicked() {
        viewModel.onClickAccept()
    }
}
