package relaxeddd.englishnotify.ui.dictionary_know

import relaxeddd.englishnotify.common.InjectorUtils
import relaxeddd.englishnotify.ui.dictionary.AdapterDictionary
import relaxeddd.englishnotify.ui.dictionary.FragmentDictionary

class FragmentDictionaryKnow : FragmentDictionary<ViewModelDictionaryKnow, AdapterDictionary>() {

    override fun getViewModelFactory() = InjectorUtils.provideDictionaryKnowViewModelFactory(requireContext())
    override fun getViewModelClass() = ViewModelDictionaryKnow::class.java
    override fun createWordsAdapter() = AdapterDictionary(viewModel)
}
