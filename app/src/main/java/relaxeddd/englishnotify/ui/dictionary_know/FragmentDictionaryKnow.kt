package relaxeddd.englishnotify.ui.dictionary_know

import androidx.lifecycle.Observer
import relaxeddd.englishnotify.R
import relaxeddd.englishnotify.common.InjectorUtils
import relaxeddd.englishnotify.databinding.FragmentDictionaryKnowBinding
import relaxeddd.englishnotify.ui.dictionary.AdapterDictionary
import relaxeddd.englishnotify.ui.dictionary.FragmentDictionary

class FragmentDictionaryKnow : FragmentDictionary<ViewModelDictionaryKnow, FragmentDictionaryKnowBinding, AdapterDictionary>() {

    override fun getLayoutResId() = R.layout.fragment_dictionary_know
    override fun getToolbarTitleResId() = R.string.already_know
    override fun getViewModelFactory() = InjectorUtils.provideDictionaryKnowViewModelFactory(requireContext())
    override fun getViewModelClass() = ViewModelDictionaryKnow::class.java
    override fun getMenuResId() = R.menu.menu_fragment_dictionary_know
    override fun getSearchMenuItemId() = R.id.item_menu_search_know

    override fun configureBinding() {
        super.configureBinding()
        adapter = AdapterDictionary(viewModel)
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
}