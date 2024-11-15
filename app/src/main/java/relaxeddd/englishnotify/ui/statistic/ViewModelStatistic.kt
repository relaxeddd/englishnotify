package relaxeddd.englishnotify.ui.statistic

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Observer
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch
import relaxeddd.englishnotify.common.NAVIGATION_LOADING_HIDE
import relaxeddd.englishnotify.common.NAVIGATION_LOADING_SHOW
import relaxeddd.englishnotify.common.TagInfo
import relaxeddd.englishnotify.domain_words.entity.Word
import relaxeddd.englishnotify.domain_words.repository.RepositoryWords
import relaxeddd.englishnotify.preferences.Preferences
import relaxeddd.englishnotify.view_base.ViewModelBase
import relaxeddd.englishnotify.view_base.models.Event
import javax.inject.Inject
import kotlin.math.min

class ViewModelStatistic @Inject constructor(
    private val prefs: Preferences,
    private val repositoryWords: RepositoryWords,
) : ViewModelBase() {

    private val learnStageMax = prefs.getTrueAnswersToLearn()

    private val wordsObserver = Observer<List<Word>> {
        updateOwnWords()
    }

    var ownTagInfo = TagInfo()
    val ownWords = MutableLiveData<List<Word>>(ArrayList())

    init {
        repositoryWords.words.observeForever(wordsObserver)
        updateOwnWords()
    }

    override fun onCleared() {
        super.onCleared()
        repositoryWords.words.removeObserver(wordsObserver)
    }

    fun resetProgress(word: Word) {
        viewModelScope.launch {
            repositoryWords.setWordLearnStage(word, 0, false)
            repositoryWords.setWordLearnStage(word, 0, true)
        }
    }

    fun deleteWord(word: Word) {
        navigateEvent.value = Event(NAVIGATION_LOADING_SHOW)
        viewModelScope.launch {
            repositoryWords.deleteWord(word.id)
            navigateEvent.value = Event(NAVIGATION_LOADING_HIDE)
        }
    }

    private fun updateWordsTagInfo(words: List<Word>) {
        val learnStageMax = prefs.getTrueAnswersToLearn()
        val isEnabledSecondaryProgress = prefs.isEnabledSecondaryProgress()
        val tagInfoOwn = TagInfo()

        tagInfoOwn.received = 0
        tagInfoOwn.learned = 0
        for (word in words) {
            if (!word.isDeleted) {
                tagInfoOwn.received++
                tagInfoOwn.total++
                if (word.isLearned(isEnabledSecondaryProgress, learnStageMax)) tagInfoOwn.learned++
            }
        }

        ownTagInfo = tagInfoOwn
    }

    private fun updateOwnWords() {
        val isEnabledSecondaryProgress = prefs.isEnabledSecondaryProgress()
        val words = ArrayList(repositoryWords.words.value ?: emptyList()).filter { !it.isDeleted }.sortedWith(object: Comparator<Word> {
            override fun compare(o1: Word?, o2: Word?): Int {
                if (o1 == null) return 1
                if (o2 == null) return -1

                val learnStage1 = min(o1.learnStage, learnStageMax) + (if (isEnabledSecondaryProgress) min(o1.learnStageSecondary, learnStageMax) else 0)
                val learnStage2 = min(o2.learnStage, learnStageMax) + (if (isEnabledSecondaryProgress) min(o2.learnStageSecondary, learnStageMax) else 0)

                val learnStageCompare = if (learnStage1 > learnStage2) 1 else if (learnStage1 == learnStage2) 0 else -1
                val alphabetCompare = if (o1.eng > o2.eng) 1 else if (o1.eng == o2.eng) 0 else -1

                return if (learnStageCompare != 0) learnStageCompare else alphabetCompare
            }
        })

        updateWordsTagInfo(words)
        ownWords.value = words
    }
}
