package relaxeddd.englishnotify.ui.dictionary_all

import androidx.fragment.app.viewModels
import relaxeddd.englishnotify.ui.dictionary.AdapterDictionary
import relaxeddd.englishnotify.ui.dictionary.FragmentDictionary

class FragmentDictionaryAll : FragmentDictionary<ViewModelDictionaryAll, AdapterDictionary>() {

    override fun createWordsAdapter() = AdapterDictionary(viewModel)

    override val viewModel: ViewModelDictionaryAll by viewModels()
}
