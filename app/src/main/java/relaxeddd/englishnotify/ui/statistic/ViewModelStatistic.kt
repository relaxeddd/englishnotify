package relaxeddd.englishnotify.ui.statistic

import relaxeddd.englishnotify.common.TagInfo
import relaxeddd.englishnotify.common.ViewModelBase
import relaxeddd.englishnotify.model.repository.RepositoryWord

class ViewModelStatistic(repositoryWord: RepositoryWord) : ViewModelBase() {

    val tagsInfo: List<TagInfo> = repositoryWord.calculateTagsInfo()
}