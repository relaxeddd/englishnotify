package relaxeddd.englishnotify.ui.dictionary_exercises

import relaxeddd.englishnotify.common.EXERCISE
import relaxeddd.englishnotify.common.Word
import relaxeddd.englishnotify.model.preferences.SharedHelper
import relaxeddd.englishnotify.model.repository.RepositoryWord
import relaxeddd.englishnotify.ui.dictionary.ViewModelDictionary

class ViewModelDictionaryExercises(repositoryWord: RepositoryWord) : ViewModelDictionary(repositoryWord) {

    override fun filterWords(items: HashSet<Word>) : HashSet<Word> {
        val learnStageMax = SharedHelper.getTrueAnswersToLearn()
        return super.filterWords(items).filter { it.type == EXERCISE && !it.isLearned(SharedHelper.isEnabledSecondaryProgress(), learnStageMax) }.toHashSet()
    }
}
