package relaxeddd.englishnotify.ui.dictionary_own

import relaxeddd.englishnotify.common.InjectorUtils
import relaxeddd.englishnotify.ui.dictionary.AdapterDictionary
import relaxeddd.englishnotify.ui.dictionary.FragmentDictionary

class FragmentDictionaryOwn : FragmentDictionary<ViewModelDictionaryOwn, AdapterDictionary>() {

    override fun getViewModelFactory() = InjectorUtils.provideDictionaryOwnViewModelFactory(requireContext())
    override fun getViewModelClass() = ViewModelDictionaryOwn::class.java
    override fun createWordsAdapter() = AdapterDictionary(viewModel)
}
