package relaxeddd.englishnotify.ui.statistic

import relaxeddd.englishnotify.common.ViewModelBase
import relaxeddd.englishnotify.common.Word
import relaxeddd.englishnotify.model.repository.RepositoryWord
import java.util.Comparator

class ViewModelStatistic(repositoryWord: RepositoryWord) : ViewModelBase() {

    val ownTagInfo = repositoryWord.getOwnWordsTagInfo()

    val ownWords = repositoryWord.getOwnWords().filter { !it.isDeleted }.sortedWith(object: Comparator<Word> {

        override fun compare(o1: Word?, o2: Word?): Int {
            if (o1 == null) return 1
            if (o2 == null) return -1

            val learnStageCompare = if (o1.learnStage > o2.learnStage) 1 else if (o1.learnStage == o2.learnStage) 0 else -1
            val alphabetCompare = if (o1.eng > o2.eng) 1 else if (o1.eng == o2.eng) 0 else -1

            return if (learnStageCompare != 0) learnStageCompare else alphabetCompare
        }
    })
}