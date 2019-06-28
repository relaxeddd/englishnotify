package relaxeddd.englishnotify.ui.categories.section

import androidx.lifecycle.MutableLiveData
import com.google.android.material.radiobutton.MaterialRadioButton
import kotlinx.coroutines.launch
import relaxeddd.englishnotify.R
import relaxeddd.englishnotify.common.*
import relaxeddd.englishnotify.model.repository.RepositoryUser
import relaxeddd.englishnotify.model.repository.RepositoryWord
import relaxeddd.englishnotify.ui.categories.CategorySection

class ViewModelCategorySection(type: CategorySection, private val repositoryUser: RepositoryUser) : ViewModelBase(), ISelectCategory {

    companion object {

        var selectedCategory: String = ""
        val mapCategoryRadioButtons = HashMap<String, ArrayList<MaterialRadioButton>>()
    }

    val title = MutableLiveData("")
    val categories = MutableLiveData<List<CategoryItem>>(ArrayList())

    init {
        val allTags = repositoryUser.liveDataUser.value?.tagsAvailable ?: ArrayList()
        val list = ArrayList<CategoryItem>()

        if (selectedCategory.isEmpty()) {
            selectedCategory = repositoryUser.liveDataUser.value?.selectedTag ?: ""
        }
        for (tag in allTags) {
            var isFit = isCategoryFit(tag, type)

            if (type == CategorySection.NEW && !isFit) {
                isFit = (!isCategoryFit(tag, CategorySection.MAIN) && !isCategoryFit(tag, CategorySection.EXERCISES)
                        && !isCategoryFit(tag, CategorySection.OTHER)) || !isCategoryTranslationExists(tag)
            }
            if (isFit) {
                val categoryItem = CategoryItem(tag)
                list.add(categoryItem)
            }
        }

        categories.value = list
        title.value = getStringByResName(selectedCategory)
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

        title.value = getStringByResName(selectedCategory)
    }

    override fun onFragmentResume() {
        super.onFragmentResume()
        title.value = getStringByResName(selectedCategory)
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

        if (!category.equals(repositoryUser.liveDataUser.value?.selectedTag, true)) {
            uiScope.launch {
                navigateEvent.value = Event(NAVIGATION_LOADING_SHOW)
                val result = repositoryUser.setSelectedTag(category)
                navigateEvent.value = Event(NAVIGATION_LOADING_HIDE)

                if (result) {
                    navigateEvent.value = Event(NAVIGATION_ACTIVITY_BACK)
                }
            }
        } else {
            navigateEvent.value = Event(NAVIGATION_ACTIVITY_BACK)
        }
    }

    private fun isCategoryFit(category: String, type: CategorySection) = when(type) {
        CategorySection.MAIN -> {
            when (category) {
                ALL_APP_WORDS, ALL_APP_WORDS_WITHOUT_SIMPLE, OWN, IRREGULAR, PROVERB, HARD, HARD_5 -> true
                else -> false
            }
        }
        CategorySection.NEW -> {
            when (category) {
                EXERCISES_PLACE_PRETEXTS_1, EXERCISES_VERBS_2, EXERCISES_MODAL_VERBS_1, EXERCISES_PASSIVE_VOICE_1 -> true
                else -> false
            }
        }
        CategorySection.EXERCISES -> category.contains(EXERCISE)
        CategorySection.OTHER -> {
            when (category) {
                TOURISTS, TOURISTS_5, PRONOUN, HUMAN_BODY, HUMAN_BODY_5, COLORS, COLORS_5, TIME, TIME_5, PHRASES, PHRASES_5, ANIMALS, ANIMALS_5,
                FAMILY, FAMILY_5, HUMAN_QUALITIES, HUMAN_QUALITIES_5, FEELINGS, FEELINGS_5, EMOTIONS, EMOTIONS_5, WORK, WORK_5,
                MOVEMENT, MOVEMENT_5, PROFESSIONS, PROFESSIONS_5, FREQUENT, FREQUENT_5, EDUCATION, EDUCATION_5, FOOD, FOOD_5,
                WEATHER, WEATHER_5, HOUSE, HOUSE_5, GEOGRAPHY, GEOGRAPHY_5, ENTERTAINMENT, ENTERTAINMENT_5, SPORT, SPORT_5,
                AUTO, AUTO_5, FREQUENT_VERBS, FREQUENT_VERBS_5 -> true
                else -> false
            }
        }
    }

    private fun isCategoryTranslationExists(category: String) = when (category) {
        ALL_APP_WORDS, ALL_APP_WORDS_WITHOUT_SIMPLE, OWN, IRREGULAR, PROVERB, HARD, HARD_5,

        TOURISTS, TOURISTS_5, PRONOUN, HUMAN_BODY, HUMAN_BODY_5, COLORS, COLORS_5, TIME, TIME_5, PHRASES, PHRASES_5, ANIMALS, ANIMALS_5,
        FAMILY, FAMILY_5, HUMAN_QUALITIES, HUMAN_QUALITIES_5, FEELINGS, FEELINGS_5, EMOTIONS, EMOTIONS_5, WORK, WORK_5,
        MOVEMENT, MOVEMENT_5, PROFESSIONS, PROFESSIONS_5, FREQUENT, FREQUENT_5, EDUCATION, EDUCATION_5, FOOD, FOOD_5,
        WEATHER, WEATHER_5, HOUSE, HOUSE_5, GEOGRAPHY, GEOGRAPHY_5, ENTERTAINMENT, ENTERTAINMENT_5, SPORT, SPORT_5,
        AUTO, AUTO_5, FREQUENT_VERBS, FREQUENT_VERBS_5,

        EXERCISES_PLACE_PRETEXTS_1, EXERCISES_VERBS_2, EXERCISES_MODAL_VERBS_1, EXERCISES_PASSIVE_VOICE_1, EXERCISES_REFLEXIVE_1,
        EXERCISES_INDIRECT_SPEECH_1, EXERCISES_ARTICLES_1, EXERCISES_COMPARISON_ADJECTIVES_1, EXERCISES_CONDITIONAL_SENTENCES_1,
        EXERCISES_VERBS_FIRST -> true
        else -> false
    }
}