package relaxeddd.englishnotify.ui.dictionary

import android.os.Bundle
import android.view.MenuItem
import android.view.View
import androidx.lifecycle.Observer
import relaxeddd.englishnotify.R
import relaxeddd.englishnotify.dialogs.DialogCheckTags
import relaxeddd.englishnotify.dialogs.DialogSortBy
import kotlinx.android.synthetic.main.fragment_dictionary.*
import relaxeddd.englishnotify.common.*
import relaxeddd.englishnotify.databinding.FragmentDictionaryBinding

class FragmentDictionary : BaseFragment<ViewModelDictionary, FragmentDictionaryBinding>() {

    private lateinit var adapter: AdapterWords
    private var animBlock: AnimBlock = AnimBlock(false)

    private val listenerCheckTags: ListenerResult<List<String>> = object:
        ListenerResult<List<String>> {
        override fun onResult(result: List<String>) {
            viewModel.setFilterTags(result)
        }
    }

    private val listenerSortBy: ListenerResult<Int> = object:
        ListenerResult<Int> {
        override fun onResult(result: Int) {
            viewModel.onDialogSortByType(result)
        }
    }
    private val clickListenerCloseFilter = View.OnClickListener {
        animateDropdown(card_view_dictionary_filter, false, animBlock)
    }

    override fun getToolbarTitleResId() = R.string.dictionary
    override fun getLayoutResId() = R.layout.fragment_dictionary
    override fun getViewModelFactory() = InjectorUtils.provideDictionaryViewModelFactory(requireContext())
    override fun getViewModelClass() = ViewModelDictionary::class.java
    override fun getMenuResId() = R.menu.menu_filter_search

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.item_menu_filter -> {
                animateDropdown(
                    card_view_dictionary_filter, card_view_dictionary_filter.visibility == View.GONE,
                    animBlock
                )
                return true
            }
            else -> return super.onOptionsItemSelected(item)
        }
    }

    override fun configureBinding() {
        super.configureBinding()
        adapter = AdapterWords(viewModel)
        binding.viewModel = viewModel
        binding.recyclerViewDictionary.adapter = adapter
        binding.clickListenerCloseFilter = clickListenerCloseFilter
        viewModel.wordsFiltered.observe(viewLifecycleOwner, Observer { words ->
            binding.hasWords = (words != null && words.isNotEmpty())
            if (words != null && words.isNotEmpty()) adapter.submitList(words)
        })
        viewModel.user.observe(viewLifecycleOwner, Observer { user ->
            adapter.languageType = user?.learnLanguageType ?: 0
        })
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        recycler_view_dictionary.setOnTouchListener { _, _ ->
            animateDropdown(card_view_dictionary_filter, false, animBlock)
            false
        }
    }

    override fun onNavigationEvent(eventId: Int) {
        when (eventId) {
            NAVIGATION_DIALOG_CHECK_TAGS -> {
                val dialog = DialogCheckTags()
                val args = Bundle()
                args.putStringArray(ITEMS, viewModel.tags.toTypedArray())
                args.putStringArray(CHECKED_ITEMS, viewModel.filterTags.value?.toTypedArray())
                dialog.arguments = args
                dialog.listener = listenerCheckTags
                dialog.show(this@FragmentDictionary.childFragmentManager, "Check tags Dialog")
            }
            NAVIGATION_DIALOG_SORT_BY -> {
                val dialog = DialogSortBy()
                val args = Bundle()
                args.putInt(SELECTED_ITEM, viewModel.sortByType.value?.ordinal ?: 0)
                dialog.arguments = args
                dialog.listener = listenerSortBy
                dialog.show(this@FragmentDictionary.childFragmentManager, "Repeat Dialog")
            }
        }
    }

    override fun onSearchTextChanged(searchText: String) {
        super.onSearchTextChanged(searchText)
        viewModel.applySearch(searchText)
    }
}