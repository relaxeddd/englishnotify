package relaxeddd.englishnotify.ui.categories.section

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.updatePaddingRelative
import androidx.fragment.app.viewModels
import androidx.lifecycle.ViewModelProvider
import relaxeddd.englishnotify.common_ui_func.doOnApplyWindowInsets
import relaxeddd.englishnotify.databinding.FragmentCategorySectionBinding
import relaxeddd.englishnotify.preferences.Preferences
import relaxeddd.englishnotify.ui.categories.AdapterCategories
import relaxeddd.englishnotify.view_base.BaseFragment
import javax.inject.Inject

class FragmentCategorySection : BaseFragment<ViewModelCategorySection, FragmentCategorySectionBinding>() {

    @Inject
    override lateinit var prefs: Preferences

    @Inject
    lateinit var viewModelFactory: ViewModelProvider.Factory

    override val viewModel by viewModels<ViewModelCategorySection> { viewModelFactory }

    private var adapter: AdapterCategories? = null

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
