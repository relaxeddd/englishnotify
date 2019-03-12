package relaxeddd.englishnotify.factories

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import relaxeddd.englishnotify.model.repository.RepositoryUser
import relaxeddd.englishnotify.ui.main.ViewModelMain

class MainViewModelFactory(private val repositoryUser: RepositoryUser) : ViewModelProvider.NewInstanceFactory() {

    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return ViewModelMain(repositoryUser) as T
    }
}