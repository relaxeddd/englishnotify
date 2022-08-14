package relaxeddd.englishnotify.ui.dictionary_all

import androidx.fragment.app.viewModels
import androidx.lifecycle.ViewModelProvider
import relaxeddd.englishnotify.preferences.Preferences
import relaxeddd.englishnotify.ui.dictionary.AdapterDictionary
import relaxeddd.englishnotify.ui.dictionary.FragmentDictionary
import javax.inject.Inject

class FragmentDictionaryAll : FragmentDictionary<ViewModelDictionaryAll, AdapterDictionary>() {

    @Inject
    override lateinit var prefs: Preferences

    @Inject
    lateinit var viewModelFactory: ViewModelProvider.Factory

    override val viewModel by viewModels<ViewModelDictionaryAll> { viewModelFactory }

    override fun createWordsAdapter() = AdapterDictionary(prefs, viewModel)
}
