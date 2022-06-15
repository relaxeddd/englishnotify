package relaxeddd.englishnotify.common

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import relaxeddd.englishnotify.model.repository.RepositoryWord
import relaxeddd.englishnotify.ui.categories.section.ViewModelCategorySection
import relaxeddd.englishnotify.ui.dictionary_all.ViewModelDictionaryAll
import relaxeddd.englishnotify.ui.dictionary_container.ViewModelDictionaryContainer
import relaxeddd.englishnotify.ui.dictionary_know.ViewModelDictionaryKnow
import relaxeddd.englishnotify.ui.main.ViewModelMain
import relaxeddd.englishnotify.ui.notifications.ViewModelNotifications
import relaxeddd.englishnotify.ui.parse.ViewModelParse
import relaxeddd.englishnotify.ui.parsed_words.ViewModelParsedWords
import relaxeddd.englishnotify.ui.settings.ViewModelSettings
import relaxeddd.englishnotify.ui.statistic.ViewModelStatistic
import relaxeddd.englishnotify.ui.time.ViewModelTime
import relaxeddd.englishnotify.ui.training.ViewModelTraining
import relaxeddd.englishnotify.ui.training_setting.ViewModelTrainingSetting
import relaxeddd.englishnotify.ui.word.ViewModelWord

class DictionaryAllViewModelFactory(private val repositoryWord: RepositoryWord)
    : ViewModelProvider.NewInstanceFactory() {

    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return ViewModelDictionaryAll(repositoryWord) as T
    }
}

class DictionaryKnowViewModelFactory(private val repositoryWord: RepositoryWord)
    : ViewModelProvider.NewInstanceFactory() {

    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return ViewModelDictionaryKnow(repositoryWord) as T
    }
}

class MainViewModelFactory : ViewModelProvider.NewInstanceFactory() {

    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return ViewModelMain() as T
    }
}

class NotificationsViewModelFactory : ViewModelProvider.NewInstanceFactory() {

    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return ViewModelNotifications() as T
    }
}

class SettingsViewModelFactory : ViewModelProvider.NewInstanceFactory() {

    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return ViewModelSettings() as T
    }
}

class WordViewModelFactory : ViewModelProvider.NewInstanceFactory() {

    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return ViewModelWord() as T
    }
}

class ParseViewModelFactory : ViewModelProvider.NewInstanceFactory() {

    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return ViewModelParse() as T
    }
}

class ParsedWordsViewModelFactory : ViewModelProvider.NewInstanceFactory() {

    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return ViewModelParsedWords() as T
    }
}

class CategorySectionViewModelFactory : ViewModelProvider.NewInstanceFactory() {

    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return ViewModelCategorySection() as T
    }
}

class TrainingSettingViewModelFactory(private val repositoryWord: RepositoryWord) : ViewModelProvider.NewInstanceFactory() {

    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return ViewModelTrainingSetting(repositoryWord) as T
    }
}

class TrainingViewModelFactory(private val repositoryWord: RepositoryWord) : ViewModelProvider.NewInstanceFactory() {

    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return ViewModelTraining(repositoryWord) as T
    }
}

class StatisticViewModelFactory(private val repositoryWord: RepositoryWord) : ViewModelProvider.NewInstanceFactory() {

    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return ViewModelStatistic(repositoryWord) as T
    }
}

class TimeViewModelFactory : ViewModelProvider.NewInstanceFactory() {

    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return ViewModelTime() as T
    }
}

class DictionaryContainerViewModelFactory : ViewModelProvider.NewInstanceFactory() {

    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return ViewModelDictionaryContainer() as T
    }
}

/*class CategoriesViewModelFactory : ViewModelProvider.NewInstanceFactory() {

    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return ViewModelCategories() as T
    }
}*/
