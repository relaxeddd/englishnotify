package relaxeddd.englishnotify.ui.training_setting

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Observer
import com.google.android.material.radiobutton.MaterialRadioButton
import kotlinx.coroutines.launch
import relaxeddd.englishnotify.R
import relaxeddd.englishnotify.common.*
import relaxeddd.englishnotify.model.repository.RepositoryWord

class ViewModelTrainingSetting(private val repositoryWord: RepositoryWord) : ViewModelBase(), ISelectCategory {

    val categories = MutableLiveData<List<CategoryItem>>(ArrayList())
    var checkedItem: CategoryItem? = null
    var trainingLanguage: Int = SharedHelper.getTrainingLanguage()

    private val wordsObserver = Observer<List<Word>> { _ ->
        updateCategories()
    }

    init {
        repositoryWord.words.observeForever(wordsObserver)
        updateCategories()
    }

    override fun onCleared() {
        super.onCleared()
        repositoryWord.words.removeObserver(wordsObserver)
    }

    override fun getSelectedCategory() = checkedItem?.key
    override fun setSelectedCategory(item: CategoryItem?) {
        checkedItem = item
    }
    override fun onRadioButtonInit(category: String, radioButton: MaterialRadioButton) {}

    fun onClickAccept() {
        val category = checkedItem?.key

        if (category.isNullOrEmpty()) {
            showToast(R.string.error_category_select)
            return
        }
        if (RepositoryWord.getInstance().getTrainingWordsByCategory(category).size < 5) {
            showToast(R.string.no_training_category_words)
            return
        }

        SharedHelper.setTrainingCategory(category)
        SharedHelper.setTrainingLanguage(trainingLanguage)
        navigateEvent.value = Event(NAVIGATION_FRAGMENT_TRAINING)
    }

    private fun updateCategories() {
        val list = ArrayList<CategoryItem>()
        val selectedTag = SharedHelper.getTrainingCategory()
        val allTags = repositoryWord.getWordCategoriesForTraining()

        for (tag in allTags) {
            val categoryItem = CategoryItem(tag)

            if (tag == selectedTag) {
                checkedItem = categoryItem
            }
            if (tag == ALL_APP_WORDS && checkedItem == null) {
                checkedItem = categoryItem
            }
            when (tag) {
                ALL_APP_WORDS -> list.add(0, categoryItem)
                OWN -> list.add(0, categoryItem)
                else -> list.add(categoryItem)
            }
        }
        categories.postValue(list)
    }
}
