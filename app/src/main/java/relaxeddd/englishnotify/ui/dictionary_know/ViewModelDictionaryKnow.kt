package relaxeddd.englishnotify.ui.dictionary_know

import relaxeddd.englishnotify.common.Word
import relaxeddd.englishnotify.model.preferences.SharedHelper
import relaxeddd.englishnotify.model.repository.RepositoryWord
import relaxeddd.englishnotify.ui.dictionary.ViewModelDictionary

class ViewModelDictionaryKnow(repositoryWord: RepositoryWord) : ViewModelDictionary(repositoryWord) {

    override fun filterWords(items: HashSet<Word>) : HashSet<Word> {
        val learnStageMax = SharedHelper.getTrueAnswersToLearn()
        val isEnabledSecondaryProgress = SharedHelper.isEnabledSecondaryProgress()
        return super.filterWords(items).filter { it.isLearned(isEnabledSecondaryProgress, learnStageMax)
                && (!it.isCreatedByUser || isShowOwnWords.value == true) }.toHashSet()
    }
}
