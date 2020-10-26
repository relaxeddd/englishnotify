package relaxeddd.englishnotify.ui.statistic

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Observer
import relaxeddd.englishnotify.common.*
import relaxeddd.englishnotify.model.repository.RepositoryWord
import java.util.Comparator

class ViewModelStatistic(private val repositoryWord: RepositoryWord) : ViewModelBase() {

    private val wordsObserver = Observer<List<Word>> {
        updateFilteredOwnWords()
    }

    val ownTagInfo = repositoryWord.getOwnWordsTagInfo()
    val ownWords = MutableLiveData<List<Word>>(ArrayList())

    init {
        repositoryWord.words.observeForever(wordsObserver)
        updateFilteredOwnWords()
    }

    override fun onCleared() {
        super.onCleared()
        repositoryWord.words.removeObserver(wordsObserver)
    }

    private fun updateFilteredOwnWords() {
        val isEnabledSecondaryProgress = SharedHelper.isEnabledSecondaryProgress()
        ownWords.value = repositoryWord.getOwnWords().filter { !it.isDeleted }.sortedWith(object: Comparator<Word> {
            override fun compare(o1: Word?, o2: Word?): Int {
                if (o1 == null) return 1
                if (o2 == null) return -1

                val learnStageCompare = if (o1.learnStage > o2.learnStage) 1 else if (o1.learnStage == o2.learnStage) 0 else -1
                val learnStageSecondaryCompare = if (!isEnabledSecondaryProgress) {
                    0
                } else {
                    if (o1.learnStageSecondary > o2.learnStageSecondary) 1 else if (o1.learnStageSecondary == o2.learnStageSecondary) 0 else -1
                }
                val alphabetCompare = if (o1.eng > o2.eng) 1 else if (o1.eng == o2.eng) 0 else -1

                return if (learnStageCompare != 0) learnStageCompare else if (learnStageSecondaryCompare != 0) learnStageSecondaryCompare else alphabetCompare
            }
        })
    }
}
