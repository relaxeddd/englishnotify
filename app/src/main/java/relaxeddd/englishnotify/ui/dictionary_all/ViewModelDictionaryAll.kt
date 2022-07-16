package relaxeddd.englishnotify.ui.dictionary_all

import relaxeddd.englishnotify.domain_words.entity.Word
import relaxeddd.englishnotify.preferences.Preferences
import relaxeddd.englishnotify.ui.dictionary.ViewModelDictionary

class ViewModelDictionaryAll : ViewModelDictionary() {

    private val prefs = Preferences.getInstance()

    override fun filterWords(items: HashSet<Word>) : HashSet<Word> {
        val learnStageMax = prefs.getTrueAnswersToLearn()
        val isEnabledSecondaryProgress = prefs.isEnabledSecondaryProgress()
        return super.filterWords(items).filter { !it.isLearned(isEnabledSecondaryProgress, learnStageMax) }.toHashSet()
    }
}
