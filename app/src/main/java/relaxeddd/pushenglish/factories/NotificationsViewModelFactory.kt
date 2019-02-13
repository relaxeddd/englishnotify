package relaxeddd.pushenglish.factories

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import relaxeddd.pushenglish.model.repository.RepositoryUser
import relaxeddd.pushenglish.ui.notifications.ViewModelNotifications

class NotificationsViewModelFactory(private val repositoryUser: RepositoryUser) : ViewModelProvider.NewInstanceFactory() {

    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return ViewModelNotifications(repositoryUser) as T
    }
}