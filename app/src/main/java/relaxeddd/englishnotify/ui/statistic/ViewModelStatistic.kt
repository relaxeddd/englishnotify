package relaxeddd.englishnotify.ui.statistic

import relaxeddd.englishnotify.common.ALL_APP_WORDS
import relaxeddd.englishnotify.common.ALL_APP_WORDS_WITHOUT_SIMPLE
import relaxeddd.englishnotify.common.TagInfo
import relaxeddd.englishnotify.common.ViewModelBase
import relaxeddd.englishnotify.model.repository.RepositoryWord

class ViewModelStatistic(repositoryWord: RepositoryWord) : ViewModelBase() {

    val tagsInfo: List<TagInfo>

    init {
        var tagsInfo: List<TagInfo> = repositoryWord.calculateTagsInfo()
        tagsInfo = tagsInfo.sortedByDescending { it.received }
        var tagInfoAll = TagInfo(ALL_APP_WORDS)
        var tagInfoAll5 = TagInfo(ALL_APP_WORDS_WITHOUT_SIMPLE)

        tagsInfo.forEach {
            if (it.key == ALL_APP_WORDS) {
                tagInfoAll = it
            }
            if (it.key == ALL_APP_WORDS_WITHOUT_SIMPLE) {
                tagInfoAll5 = it
            }
        }
        val tagsInfoList = ArrayList(tagsInfo)

        tagsInfoList.remove(tagInfoAll5)
        tagsInfoList.add(0, tagInfoAll)
        tagsInfoList.remove(tagInfoAll)
        tagsInfoList.add(0, tagInfoAll)

        this.tagsInfo = tagsInfoList
    }
}