package relaxeddd.pushenglish.factories

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import relaxeddd.pushenglish.model.repository.RepositoryUser
import relaxeddd.pushenglish.ui.main.ViewModelMain

class MainViewModelFactory(private val repositoryUser: RepositoryUser) : ViewModelProvider.NewInstanceFactory() {

    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return ViewModelMain(repositoryUser) as T
    }
}