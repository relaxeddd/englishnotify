package relaxeddd.englishnotify.ui.categories.section

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.google.android.material.radiobutton.MaterialRadioButton
import kotlinx.coroutines.launch
import relaxeddd.englishnotify.App
import relaxeddd.englishnotify.R
import relaxeddd.englishnotify.common.CategoryItem
import relaxeddd.englishnotify.common.ISelectCategory
import relaxeddd.englishnotify.common.NAVIGATION_ACTIVITY_BACK
import relaxeddd.englishnotify.common.OWN_KEY_SYMBOL
import relaxeddd.englishnotify.common.getStringByResName
import relaxeddd.englishnotify.common.showToast
import relaxeddd.englishnotify.domain_words.repository.RepositoryWords
import relaxeddd.englishnotify.preferences.Preferences
import relaxeddd.englishnotify.preferences.utils.ALL_APP_WORDS
import relaxeddd.englishnotify.view_base.ViewModelBase
import relaxeddd.englishnotify.view_base.models.Event

class ViewModelCategorySection : ViewModelBase(), ISelectCategory {

    companion object {

        var selectedCategory: String = ""
        val mapCategoryRadioButtons = HashMap<String, ArrayList<MaterialRadioButton>>()
    }

    private val prefs = Preferences.getInstance()

    val title = MutableLiveData("")
    val categories = MutableLiveData<List<CategoryItem>>(ArrayList())

    init {
        val allTags = arrayListOf(ALL_APP_WORDS)
        allTags.addAll(RepositoryWords.getInstance(App.context).getOwnWordCategories())

        val list = ArrayList<CategoryItem>()

        if (selectedCategory.isEmpty()) {
            selectedCategory = prefs.getSelectedCategory()
        }
        allTags.forEach {
            val categoryItem = CategoryItem(it)
            list.add(categoryItem)
        }

        categories.value = list
        title.value = getStringByResName(selectedCategory).replaceFirst(OWN_KEY_SYMBOL, "")
    }

    override fun getSelectedCategory() = selectedCategory
    override fun setSelectedCategory(item: CategoryItem?) {
        val unselectedCategory = selectedCategory
        selectedCategory = item?.key ?: ""

        if (mapCategoryRadioButtons.containsKey(unselectedCategory)) {
            val unselectedRb = mapCategoryRadioButtons[unselectedCategory] ?: ArrayList()
            for (radioButton in unselectedRb) {
                radioButton.isChecked = false
            }
        }
        if (mapCategoryRadioButtons.containsKey(selectedCategory)) {
            val selectRb = mapCategoryRadioButtons[selectedCategory] ?: ArrayList()
            for (radioButton in selectRb) {
                radioButton.isChecked = true
            }
        }

        title.value = getStringByResName(selectedCategory).replaceFirst(OWN_KEY_SYMBOL, "")
    }

    override fun onFragmentResume() {
        super.onFragmentResume()
        title.value = getStringByResName(selectedCategory).replaceFirst(OWN_KEY_SYMBOL, "")
    }

    override fun onRadioButtonInit(category: String, radioButton: MaterialRadioButton) {
        if (mapCategoryRadioButtons.containsKey(category)) {
            mapCategoryRadioButtons[category]?.add(radioButton)
        } else {
            val list = ArrayList<MaterialRadioButton>()
            list.add(radioButton)
            mapCategoryRadioButtons[category] = list
        }
    }

    override fun onCleared() {
        super.onCleared()
        for (listRd in mapCategoryRadioButtons.values) {
            listRd.clear()
        }
        mapCategoryRadioButtons.clear()
        selectedCategory = ""
    }

    fun onClickAccept() {
        val category = selectedCategory

        if (category.isEmpty()) {
            showToast(R.string.error_update)
            return
        }

        if (!category.equals(prefs.getSelectedCategory(), true)) {
            viewModelScope.launch {
                if (category.isNotEmpty()) {
                    prefs.setSelectedCategory(category)
                    navigateEvent.value = Event(NAVIGATION_ACTIVITY_BACK)
                } else {
                    showToast(R.string.tags_should_not_be_empty)
                }
            }
        } else {
            navigateEvent.value = Event(NAVIGATION_ACTIVITY_BACK)
        }
    }
}
