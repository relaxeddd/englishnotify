package relaxeddd.englishnotify.factories

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import relaxeddd.englishnotify.model.repository.RepositoryUser
import relaxeddd.englishnotify.model.repository.RepositoryWord
import relaxeddd.englishnotify.ui.dictionary_all.ViewModelDictionaryAll

class DictionaryAllViewModelFactory(private val repositoryWord: RepositoryWord, private val repositoryUser: RepositoryUser)
    : ViewModelProvider.NewInstanceFactory() {

    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return ViewModelDictionaryAll(repositoryWord, repositoryUser) as T
    }
}