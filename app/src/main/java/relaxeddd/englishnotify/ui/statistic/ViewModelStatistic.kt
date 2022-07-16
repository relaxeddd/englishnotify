package relaxeddd.englishnotify.ui.statistic

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Observer
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch
import relaxeddd.englishnotify.App
import relaxeddd.englishnotify.common.Event
import relaxeddd.englishnotify.common.NAVIGATION_LOADING_HIDE
import relaxeddd.englishnotify.common.NAVIGATION_LOADING_SHOW
import relaxeddd.englishnotify.common.TagInfo
import relaxeddd.englishnotify.common.ViewModelBase
import relaxeddd.englishnotify.common.Word
import relaxeddd.englishnotify.model.db.AppDatabase
import relaxeddd.englishnotify.model.preferences.SharedHelper
import relaxeddd.englishnotify.model.repository.RepositoryWord
import kotlin.math.min

class ViewModelStatistic : ViewModelBase() {

    private val repositoryWord = RepositoryWord.getInstance(AppDatabase.getInstance(App.context.applicationContext).wordDao())

    private val learnStageMax = SharedHelper.getTrueAnswersToLearn()

    private val wordsObserver = Observer<List<Word>> {
        updateOwnWords()
    }

    var ownTagInfo = TagInfo()
    val ownWords = MutableLiveData<List<Word>>(ArrayList())

    init {
        repositoryWord.words.observeForever(wordsObserver)
        updateOwnWords()
    }

    override fun onCleared() {
        super.onCleared()
        repositoryWord.words.removeObserver(wordsObserver)
    }

    fun resetProgress(word: Word) {
        viewModelScope.launch {
            repositoryWord.setWordLearnStage(word, 0, false)
            repositoryWord.setWordLearnStage(word, 0, true)
        }
    }

    fun deleteWord(word: Word) {
        navigateEvent.value = Event(NAVIGATION_LOADING_SHOW)
        viewModelScope.launch {
            repositoryWord.deleteWord(word.id)
            navigateEvent.value = Event(NAVIGATION_LOADING_HIDE)
        }
    }

    private fun updateWordsTagInfo(words: List<Word>) {
        val learnStageMax = SharedHelper.getTrueAnswersToLearn()
        val isEnabledSecondaryProgress = SharedHelper.isEnabledSecondaryProgress()
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
        val isEnabledSecondaryProgress = SharedHelper.isEnabledSecondaryProgress()
        val words = ArrayList(repositoryWord.words.value ?: emptyList()).filter { !it.isDeleted }.sortedWith(object: Comparator<Word> {
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
