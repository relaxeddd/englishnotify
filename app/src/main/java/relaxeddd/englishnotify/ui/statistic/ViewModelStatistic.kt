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
        var tagInfoAll: TagInfo? = null
        var tagInfoAll5: TagInfo? = null

        tagsInfo.forEach {
            if (it.key == ALL_APP_WORDS) tagInfoAll = it
            if (it.key == ALL_APP_WORDS_WITHOUT_SIMPLE) tagInfoAll5 = it
        }
        val tagsInfoList = ArrayList(tagsInfo)

        if (tagInfoAll5 != null) {
            tagsInfoList.remove(tagInfoAll5)
            tagsInfoList.add(0, tagInfoAll5)
        }
        if (tagInfoAll != null) {
            tagsInfoList.remove(tagInfoAll)
            tagsInfoList.add(0, tagInfoAll)
        }

        this.tagsInfo = tagsInfoList
    }
}