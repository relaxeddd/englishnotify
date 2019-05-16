package relaxeddd.englishnotify.common

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import relaxeddd.englishnotify.model.repository.RepositoryUser
import relaxeddd.englishnotify.model.repository.RepositoryWord
import relaxeddd.englishnotify.ui.categories.ViewModelCategories
import relaxeddd.englishnotify.ui.dictionary_all.ViewModelDictionaryAll
import relaxeddd.englishnotify.ui.dictionary_exercises.ViewModelDictionaryExercises
import relaxeddd.englishnotify.ui.dictionary_know.ViewModelDictionaryKnow
import relaxeddd.englishnotify.ui.dictionary_own.ViewModelDictionaryOwn
import relaxeddd.englishnotify.ui.main.ViewModelMain
import relaxeddd.englishnotify.ui.notifications.ViewModelNotifications
import relaxeddd.englishnotify.ui.settings.ViewModelSettings
import relaxeddd.englishnotify.ui.training.ViewModelTraining
import relaxeddd.englishnotify.ui.training_setting.ViewModelTrainingSetting
import relaxeddd.englishnotify.ui.word.ViewModelWord

class DictionaryAllViewModelFactory(private val repositoryWord: RepositoryWord, private val repositoryUser: RepositoryUser)
    : ViewModelProvider.NewInstanceFactory() {

    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return ViewModelDictionaryAll(repositoryWord, repositoryUser) as T
    }
}

class DictionaryOwnViewModelFactory(private val repositoryWord: RepositoryWord, private val repositoryUser: RepositoryUser)
    : ViewModelProvider.NewInstanceFactory() {

    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return ViewModelDictionaryOwn(repositoryWord, repositoryUser) as T
    }
}

class DictionaryExercisesViewModelFactory(private val repositoryWord: RepositoryWord, private val repositoryUser: RepositoryUser)
    : ViewModelProvider.NewInstanceFactory() {

    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return ViewModelDictionaryExercises(repositoryWord, repositoryUser) as T
    }
}

class DictionaryKnowViewModelFactory(private val repositoryWord: RepositoryWord, private val repositoryUser: RepositoryUser)
    : ViewModelProvider.NewInstanceFactory() {

    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return ViewModelDictionaryKnow(repositoryWord, repositoryUser) as T
    }
}

class MainViewModelFactory(private val repositoryUser: RepositoryUser) : ViewModelProvider.NewInstanceFactory() {

    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return ViewModelMain(repositoryUser) as T
    }
}

class NotificationsViewModelFactory(private val repositoryUser: RepositoryUser) : ViewModelProvider.NewInstanceFactory() {

    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return ViewModelNotifications(repositoryUser) as T
    }
}

class SettingsViewModelFactory(private val repositoryUser: RepositoryUser) : ViewModelProvider.NewInstanceFactory() {

    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return ViewModelSettings(repositoryUser) as T
    }
}

class WordViewModelFactory : ViewModelProvider.NewInstanceFactory() {

    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return ViewModelWord() as T
    }
}

class CategoriesViewModelFactory(private val repositoryUser: RepositoryUser) : ViewModelProvider.NewInstanceFactory() {

    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return ViewModelCategories(repositoryUser) as T
    }
}

class TrainingSettingViewModelFactory : ViewModelProvider.NewInstanceFactory() {

    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return ViewModelTrainingSetting() as T
    }
}

class TrainingViewModelFactory(private val repositoryWord: RepositoryWord) : ViewModelProvider.NewInstanceFactory() {

    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return ViewModelTraining(repositoryWord) as T
    }
}