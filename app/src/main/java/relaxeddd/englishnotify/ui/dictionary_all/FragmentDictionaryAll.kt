package relaxeddd.englishnotify.ui.dictionary_all

import androidx.lifecycle.Observer
import relaxeddd.englishnotify.R
import relaxeddd.englishnotify.common.InjectorUtils
import relaxeddd.englishnotify.databinding.FragmentDictionaryAllBinding
import relaxeddd.englishnotify.ui.dictionary.AdapterWords
import relaxeddd.englishnotify.ui.dictionary.FragmentDictionary

class FragmentDictionaryAll : FragmentDictionary<ViewModelDictionaryAll, FragmentDictionaryAllBinding>() {

    override fun getLayoutResId() = R.layout.fragment_dictionary_all
    override fun getViewModelFactory() = InjectorUtils.provideDictionaryAllViewModelFactory(requireContext())
    override fun getViewModelClass() = ViewModelDictionaryAll::class.java
    override fun getSearchMenuItemId() = R.id.item_menu_search_all

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
}