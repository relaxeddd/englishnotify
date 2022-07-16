package relaxeddd.englishnotify.ui.dictionary_know

import androidx.fragment.app.viewModels
import relaxeddd.englishnotify.ui.dictionary.AdapterDictionary
import relaxeddd.englishnotify.ui.dictionary.FragmentDictionary

class FragmentDictionaryKnow : FragmentDictionary<ViewModelDictionaryKnow, AdapterDictionary>() {

    override fun createWordsAdapter() = AdapterDictionary(viewModel)

    override val viewModel: ViewModelDictionaryKnow by viewModels()
}
