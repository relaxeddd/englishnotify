package relaxeddd.englishnotify.ui.dictionary_all

import relaxeddd.englishnotify.common.InjectorUtils
import relaxeddd.englishnotify.ui.dictionary.AdapterDictionary
import relaxeddd.englishnotify.ui.dictionary.FragmentDictionary

class FragmentDictionaryAll : FragmentDictionary<ViewModelDictionaryAll, AdapterDictionary>() {

    override fun getViewModelFactory() = InjectorUtils.provideDictionaryAllViewModelFactory(requireContext())
    override fun getViewModelClass() = ViewModelDictionaryAll::class.java
    override fun createWordsAdapter() = AdapterDictionary(viewModel)
}
