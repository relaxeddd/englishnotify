package relaxeddd.englishnotify.ui.dictionary_own

import androidx.lifecycle.Observer
import relaxeddd.englishnotify.R
import relaxeddd.englishnotify.common.InjectorUtils
import relaxeddd.englishnotify.databinding.FragmentDictionaryOwnBinding
import relaxeddd.englishnotify.ui.dictionary.AdapterWords
import relaxeddd.englishnotify.ui.dictionary.FragmentDictionary

class FragmentDictionaryOwn : FragmentDictionary<ViewModelDictionaryOwn, FragmentDictionaryOwnBinding>() {

    override fun getLayoutResId() = R.layout.fragment_dictionary_own
    override fun getViewModelFactory() = InjectorUtils.provideDictionaryOwnViewModelFactory(requireContext())
    override fun getViewModelClass() = ViewModelDictionaryOwn::class.java

    override fun configureBinding() {
        super.configureBinding()
        adapter = AdapterWords(viewModel)
        binding.viewModel = viewModel
        binding.recyclerViewDictionary.adapter = adapter
        binding.clickListenerCloseFilter = clickListenerCloseFilter
        viewModel.wordsFiltered.observe(viewLifecycleOwner, Observer { words ->
            binding.hasWords = (words != null && words.isNotEmpty())
            if (words != null && words.isNotEmpty()) adapter.submitList(words)
            adapter.notifyDataSetChanged()
        })
        viewModel.user.observe(viewLifecycleOwner, Observer { user ->
            adapter.languageType = user?.learnLanguageType ?: 0
        })
    }

    override fun onFragmentSelected() {
        super.onFragmentSelected()
        viewModel.requestOwnWords()
    }
}