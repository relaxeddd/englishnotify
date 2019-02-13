package relaxeddd.pushenglish.factories

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import relaxeddd.pushenglish.model.repository.RepositoryWord
import relaxeddd.pushenglish.ui.dictionary.ViewModelDictionary

class DictionaryViewModelFactory(private val repositoryWord: RepositoryWord) : ViewModelProvider.NewInstanceFactory() {

    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return ViewModelDictionary(repositoryWord) as T
    }
}