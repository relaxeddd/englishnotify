package relaxeddd.englishnotify.ui.dictionary_all

import relaxeddd.englishnotify.common.EXERCISE
import relaxeddd.englishnotify.common.Word
import relaxeddd.englishnotify.model.preferences.SharedHelper
import relaxeddd.englishnotify.model.repository.RepositoryWord
import relaxeddd.englishnotify.ui.dictionary.ViewModelDictionary

class ViewModelDictionaryAll(repositoryWord: RepositoryWord) : ViewModelDictionary(repositoryWord) {

    override fun filterWords(items: HashSet<Word>) : HashSet<Word> {
        val learnStageMax = SharedHelper.getTrueAnswersToLearn()
        val isEnabledSecondaryProgress = SharedHelper.isEnabledSecondaryProgress()
        return super.filterWords(items).filter { it.type != EXERCISE && !it.isLearned(isEnabledSecondaryProgress, learnStageMax)
                && (!it.isCreatedByUser || isShowOwnWords.value == true) }.toHashSet()
    }
}
