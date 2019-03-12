package relaxeddd.englishnotify.factories

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import relaxeddd.englishnotify.model.repository.RepositoryUser
import relaxeddd.englishnotify.model.repository.RepositoryWord
import relaxeddd.englishnotify.ui.dictionary.ViewModelDictionary

class DictionaryViewModelFactory(private val repositoryWord: RepositoryWord,
                                 private val repositoryUser: RepositoryUser
) : ViewModelProvider.NewInstanceFactory() {

    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return ViewModelDictionary(repositoryWord, repositoryUser) as T
    }
}