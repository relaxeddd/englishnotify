package relaxeddd.englishnotify.ui.dictionary_know

import relaxeddd.englishnotify.model.preferences.SharedHelper
import relaxeddd.englishnotify.common.Word
import relaxeddd.englishnotify.model.repository.RepositoryUser
import relaxeddd.englishnotify.model.repository.RepositoryWord
import relaxeddd.englishnotify.ui.dictionary.ViewModelDictionary

class ViewModelDictionaryKnow(repositoryWord: RepositoryWord, repositoryUser: RepositoryUser) : ViewModelDictionary(repositoryWord, repositoryUser) {

    override fun filterWords(items: HashSet<Word>) : HashSet<Word> {
        val isEnabledSecondaryProgress = SharedHelper.isEnabledSecondaryProgress()
        return super.filterWords(items).filter { it.isLearned(isEnabledSecondaryProgress)
                && (!it.isCreatedByUser || isShowOwnWords.value == true) }.toHashSet()
    }
}
