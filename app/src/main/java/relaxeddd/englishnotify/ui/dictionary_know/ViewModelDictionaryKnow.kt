package relaxeddd.englishnotify.ui.dictionary_know

import relaxeddd.englishnotify.domain_words.entity.Word
import relaxeddd.englishnotify.domain_words.repository.RepositoryWords
import relaxeddd.englishnotify.preferences.Preferences
import relaxeddd.englishnotify.ui.dictionary.ViewModelDictionary
import javax.inject.Inject

class ViewModelDictionaryKnow @Inject constructor(
    prefs: Preferences,
    repositoryWords: RepositoryWords,
) : ViewModelDictionary(prefs, repositoryWords) {

    override fun filterWords(prefs: Preferences, items: HashSet<Word>) : HashSet<Word> {
        val learnStageMax = prefs.getTrueAnswersToLearn()
        val isEnabledSecondaryProgress = prefs.isEnabledSecondaryProgress()
        return super.filterWords(prefs, items).filter {
            it.isLearned(isEnabledSecondaryProgress, learnStageMax)
        }.toHashSet()
    }
}
