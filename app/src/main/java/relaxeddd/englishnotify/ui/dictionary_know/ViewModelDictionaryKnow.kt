package relaxeddd.englishnotify.ui.dictionary_know

import relaxeddd.englishnotify.domain_words.entity.Word
import relaxeddd.englishnotify.preferences.Preferences
import relaxeddd.englishnotify.ui.dictionary.ViewModelDictionary

class ViewModelDictionaryKnow : ViewModelDictionary() {

    private val prefs get() = Preferences.getInstance()

    override fun filterWords(items: HashSet<Word>) : HashSet<Word> {
        val learnStageMax = prefs.getTrueAnswersToLearn()
        val isEnabledSecondaryProgress = prefs.isEnabledSecondaryProgress()
        return super.filterWords(items).filter { it.isLearned(isEnabledSecondaryProgress, learnStageMax) }.toHashSet()
    }
}
