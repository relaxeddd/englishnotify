package relaxeddd.englishnotify.ui.training_setting

import android.content.Context
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Observer
import com.google.android.material.radiobutton.MaterialRadioButton
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
import javax.inject.Inject

class ViewModelTrainingSetting @Inject constructor(
    private val context: Context,
    private val prefs: Preferences,
    private val repositoryWords: RepositoryWords,
) : ViewModelBase(), ISelectCategory {

    val categories = MutableLiveData<List<CategoryItem>>(ArrayList())
    var checkedItem: CategoryItem? = null
    var trainingLanguage: Int = prefs.getTrainingLanguage()

    private val wordsObserver = Observer<List<Word>> {
        updateCategories()
    }

    init {
        repositoryWords.words.observeForever(wordsObserver)
        updateCategories()
    }

    override fun onCleared() {
        super.onCleared()
        repositoryWords.words.removeObserver(wordsObserver)
    }

    override fun getSelectedCategory() = checkedItem?.key
    override fun setSelectedCategory(item: CategoryItem?) {
        checkedItem = item
    }
    override fun onRadioButtonInit(category: String, radioButton: MaterialRadioButton) {}

    fun onClickAccept() {
        val category = checkedItem?.key

        if (category.isNullOrEmpty()) {
            showToast(context, R.string.error_category_select)
            return
        }
        val trainWordsCount = repositoryWords.getTrainingWordsByCategory(category, prefs.isCheckLearnedWords(), trainingLanguage)
        if (trainWordsCount.size < 5) {
            showToast(context, R.string.no_training_category_words)
            return
        }

        prefs.setTrainingCategory(category)
        prefs.setTrainingLanguage(trainingLanguage)
        navigateEvent.value = Event(NAVIGATION_FRAGMENT_TRAINING)
    }

    private fun updateCategories() {
        val list = ArrayList<CategoryItem>()
        val selectedTag = prefs.getTrainingCategory()
        val allTags = repositoryWords.getWordCategoriesForTraining()

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
