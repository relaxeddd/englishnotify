package relaxeddd.englishnotify.ui.dictionary_own

import relaxeddd.englishnotify.common.Word
import relaxeddd.englishnotify.model.preferences.SharedHelper
import relaxeddd.englishnotify.model.repository.RepositoryWord
import relaxeddd.englishnotify.ui.dictionary.ViewModelDictionary

class ViewModelDictionaryOwn(repositoryWord: RepositoryWord) : ViewModelDictionary(repositoryWord) {

    override val isShowOwnWordsContainer = false

    override fun filterWords(items: HashSet<Word>) : HashSet<Word> {
        val learnStageMax = SharedHelper.getTrueAnswersToLearn()
        val isEnabledSecondaryProgress = SharedHelper.isEnabledSecondaryProgress()
        return super.filterWords(items).filter { it.isOwnCategory && !it.isLearned(isEnabledSecondaryProgress, learnStageMax) }.toHashSet()
    }
}
