package relaxeddd.englishnotify.ui.dictionary_know

import androidx.fragment.app.viewModels
import androidx.lifecycle.ViewModelProvider
import relaxeddd.englishnotify.preferences.Preferences
import relaxeddd.englishnotify.ui.dictionary.AdapterDictionary
import relaxeddd.englishnotify.ui.dictionary.FragmentDictionary
import javax.inject.Inject

class FragmentDictionaryKnow : FragmentDictionary<ViewModelDictionaryKnow, AdapterDictionary>() {

    @Inject
    override lateinit var prefs: Preferences

    @Inject
    lateinit var viewModelFactory: ViewModelProvider.Factory

    override val viewModel by viewModels<ViewModelDictionaryKnow> { viewModelFactory }

    override fun createWordsAdapter() = AdapterDictionary(prefs, viewModel)
}
