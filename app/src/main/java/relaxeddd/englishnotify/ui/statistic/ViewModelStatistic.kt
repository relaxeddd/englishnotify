package relaxeddd.englishnotify.ui.statistic

import relaxeddd.englishnotify.common.ViewModelBase
import relaxeddd.englishnotify.model.repository.RepositoryWord

class ViewModelStatistic(repositoryWord: RepositoryWord) : ViewModelBase() {

    val ownTagInfo = repositoryWord.getOwnWordsTagInfo()
    val ownWords = repositoryWord.getOwnWords().filter { !it.isDeleted }.sortedByDescending { it.learnStage }
}