package relaxeddd.englishnotify.ui.categories

import androidx.lifecycle.MutableLiveData
import kotlinx.coroutines.launch
import relaxeddd.englishnotify.R
import relaxeddd.englishnotify.common.*
import relaxeddd.englishnotify.model.repository.RepositoryUser
import relaxeddd.englishnotify.model.repository.RepositoryWord

class ViewModelCategories(private val repositoryUser: RepositoryUser) : ViewModelBase(), ISelectCategory {

    val categories = MutableLiveData<List<CategoryItem>>(ArrayList())
    var checkedItem: CategoryItem? = null

    init {
        val list = ArrayList<CategoryItem>()
        val selectedTag = repositoryUser.liveDataUser.value?.selectedTag
        val allTags = repositoryUser.liveDataUser.value?.tagsAvailable

        if (allTags != null) {
            for (tag in allTags) {
                val categoryItem = CategoryItem(tag)

                if (tag == selectedTag) {
                    checkedItem = categoryItem
                }
                list.add(categoryItem)
            }
        }
        categories.postValue(list)
    }

    override fun getSelectedCategory() = checkedItem
    override fun setSelectedCategory(item: CategoryItem?) {
        checkedItem = item
    }

    fun onClickAccept() {
        val category = checkedItem?.key

        if (category.isNullOrEmpty()) {
            showToast(R.string.error_update)
            return
        }
        if (category == OWN && RepositoryWord.getInstance().getOwnWords().isEmpty()) {
            showToastLong(R.string.category_own_not_selected)
            return
        }

        if (!category.equals(repositoryUser.liveDataUser.value?.selectedTag, true)) {
            uiScope.launch {
                navigateEvent.value = Event(NAVIGATION_LOADING_SHOW)
                val result = repositoryUser.setSelectedTag(category)
                navigateEvent.value = Event(NAVIGATION_LOADING_HIDE)

                if (result) {
                    navigateEvent.value = Event(NAVIGATION_ACTIVITY_BACK)
                }
            }
        }
    }
}