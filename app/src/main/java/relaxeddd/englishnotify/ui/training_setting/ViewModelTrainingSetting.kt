package relaxeddd.englishnotify.ui.training_setting

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Observer
import com.google.android.material.radiobutton.MaterialRadioButton
import relaxeddd.englishnotify.App
import relaxeddd.englishnotify.R
import relaxeddd.englishnotify.common.CategoryItem
import relaxeddd.englishnotify.common.ISelectCategory
import relaxeddd.englishnotify.common.NAVIGATION_FRAGMENT_TRAINING
import relaxeddd.englishnotify.common.showToast
import relaxeddd.englishnotify.domain_words.entity.Word
import relaxeddd.englishnotify.domain_words.repository.RepositoryWords
import relaxeddd.englishnotify.preferences.Preferences
import relaxeddd.englishnotify.preferences.utils.ALL_APP_WORDS
import relaxeddd.englishnotify.view_base.ViewModelBase
import relaxeddd.englishnotify.view_base.models.Event

class ViewModelTrainingSetting : ViewModelBase(), ISelectCategory {

    private val prefs = Preferences.getInstance()

    private val repositoryWord = RepositoryWords.getInstance(App.context)

    val categories = MutableLiveData<List<CategoryItem>>(ArrayList())
    var checkedItem: CategoryItem? = null
    var trainingLanguage: Int = prefs.getTrainingLanguage()

    private val wordsObserver = Observer<List<Word>> {
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
        val trainWordsCount = RepositoryWords.getInstance(App.context).getTrainingWordsByCategory(category, prefs.isCheckLearnedWords(), trainingLanguage)
        if (trainWordsCount.size < 5) {
            showToast(R.string.no_training_category_words)
            return
        }

        prefs.setTrainingCategory(category)
        prefs.setTrainingLanguage(trainingLanguage)
        navigateEvent.value = Event(NAVIGATION_FRAGMENT_TRAINING)
    }

    private fun updateCategories() {
        val list = ArrayList<CategoryItem>()
        val selectedTag = prefs.getTrainingCategory()
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
                else -> list.add(categoryItem)
            }
        }
        categories.postValue(list)
    }
}
