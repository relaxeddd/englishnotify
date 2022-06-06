package relaxeddd.englishnotify.ui.categories.section

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.google.android.material.radiobutton.MaterialRadioButton
import kotlinx.coroutines.launch
import relaxeddd.englishnotify.R
import relaxeddd.englishnotify.common.*
import relaxeddd.englishnotify.model.preferences.SharedHelper
import relaxeddd.englishnotify.model.repository.RepositoryWord

class ViewModelCategorySection : ViewModelBase(), ISelectCategory {

    companion object {

        var selectedCategory: String = ""
        val mapCategoryRadioButtons = HashMap<String, ArrayList<MaterialRadioButton>>()
    }

    val title = MutableLiveData("")
    val categories = MutableLiveData<List<CategoryItem>>(ArrayList())

    init {
        val allTags = arrayListOf("all_app_words", "own")
        allTags.addAll(RepositoryWord.getInstance().getOwnWordCategories())

        val list = ArrayList<CategoryItem>()

        if (selectedCategory.isEmpty()) {
            selectedCategory = SharedHelper.getSelectedCategory()
        }
        allTags.filter { isCategoryFit(it) }.forEach {
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
        if (category == OWN && RepositoryWord.getInstance().getOwnWords().isEmpty()) {
            showToast(R.string.category_own_not_selected)
            return
        }

        if (!category.equals(SharedHelper.getSelectedCategory(), true)) {
            viewModelScope.launch {
                if (category.isNotEmpty()) {
                    SharedHelper.setSelectedCategory(category)
                    navigateEvent.value = Event(NAVIGATION_ACTIVITY_BACK)
                } else {
                    showToast(R.string.tags_should_not_be_empty)
                }
            }
        } else {
            navigateEvent.value = Event(NAVIGATION_ACTIVITY_BACK)
        }
    }

    private fun isCategoryFit(category: String) : Boolean {
        return when (category) {
            ALL_APP_WORDS, ALL_APP_WORDS_WITHOUT_SIMPLE, IRREGULAR, PROVERB, HARD, HARD_5 -> true

            TOURISTS, TOURISTS_5, PRONOUN, HUMAN_BODY, HUMAN_BODY_5, COLORS, COLORS_5, TIME, TIME_5, PHRASES, PHRASES_5, ANIMALS, ANIMALS_5,
            FAMILY, FAMILY_5, HUMAN_QUALITIES, HUMAN_QUALITIES_5, FEELINGS, FEELINGS_5, EMOTIONS, EMOTIONS_5, WORK, WORK_5,
            MOVEMENT, MOVEMENT_5, PROFESSIONS, PROFESSIONS_5, FREQUENT, FREQUENT_5, EDUCATION, EDUCATION_5, FOOD, FOOD_5,
            WEATHER, WEATHER_5, HOUSE, HOUSE_5, GEOGRAPHY, GEOGRAPHY_5, ENTERTAINMENT, ENTERTAINMENT_5, SPORT, SPORT_5,
            AUTO, AUTO_5, FREQUENT_VERBS, FREQUENT_VERBS_5 -> true

            else -> isOwnCategory(category)
        }
    }
}
