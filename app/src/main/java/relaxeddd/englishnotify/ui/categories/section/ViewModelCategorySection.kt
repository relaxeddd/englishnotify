package relaxeddd.englishnotify.ui.categories.section

import androidx.lifecycle.MutableLiveData
import kotlinx.coroutines.launch
import relaxeddd.englishnotify.R
import relaxeddd.englishnotify.common.*
import relaxeddd.englishnotify.model.repository.RepositoryUser
import relaxeddd.englishnotify.model.repository.RepositoryWord
import relaxeddd.englishnotify.ui.categories.CategorySection

class ViewModelCategorySection(private val type: CategorySection, private val repositoryUser: RepositoryUser) : ViewModelBase(), ISelectCategory {

    val title = MutableLiveData<String>("")
    val categories = MutableLiveData<List<CategoryItem>>(ArrayList())

    init {
        updateCategories()
    }

    override fun getSelectedCategory() = SharedHelper.getLocalSelectedCategory()
    override fun setSelectedCategory(item: CategoryItem?) {
        item?.isSelected = true
        SharedHelper.setLocalSelectedCategory(item?.key ?: return)
        title.value = getStringByResName(SharedHelper.getLocalSelectedCategory())
    }

    fun onClickAccept() {
        val category = SharedHelper.getLocalSelectedCategory()

        if (category.isEmpty()) {
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
        } else {
            navigateEvent.value = Event(NAVIGATION_ACTIVITY_BACK)
        }
    }

    fun updateCategories() {
        val allTags = repositoryUser.liveDataUser.value?.tagsAvailable ?: ArrayList()
        val allCategoryItems = RepositoryWord.getInstance().getCategoryItems(allTags)
        val list = ArrayList<CategoryItem>()
        val selectedTag = SharedHelper.getLocalSelectedCategory()

        for (item in allCategoryItems) {
            var isFit = isCategoryFit(item.key, type)

            if (type == CategorySection.NEW && !isFit) {
                isFit = !isCategoryFit(item.key, CategorySection.MAIN) && !isCategoryFit(item.key, CategorySection.EXERCISES)
                        && !isCategoryFit(item.key, CategorySection.OTHER)
            }
            if (isFit) {
                if (item.key == selectedTag) {
                    item.isSelected = true
                }
                list.add(item)
            }
        }

        categories.value = list
        title.value = getStringByResName(SharedHelper.getLocalSelectedCategory())
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
                TOURISTS, TOURISTS_5, FREQUENT_VERBS, FREQUENT_VERBS_5, EXERCISES_VERBS_FIRST -> true
                else -> false
            }
        }
        CategorySection.EXERCISES -> {
            when (category) {
                EXERCISES_VERBS_FIRST -> true
                else -> false
            }
        }
        CategorySection.OTHER -> {
            when (category) {
                PRONOUN, HUMAN_BODY, HUMAN_BODY_5, COLORS, COLORS_5, TIME, TIME_5, PHRASES, PHRASES_5, ANIMALS, ANIMALS_5,
                FAMILY, FAMILY_5, HUMAN_QUALITIES, HUMAN_QUALITIES_5, FEELINGS, FEELINGS_5, EMOTIONS, EMOTIONS_5, WORK, WORK_5,
                MOVEMENT, MOVEMENT_5, PROFESSIONS, PROFESSIONS_5, FREQUENT, FREQUENT_5, EDUCATION, EDUCATION_5, FOOD, FOOD_5,
                WEATHER, WEATHER_5, HOUSE, HOUSE_5, GEOGRAPHY, GEOGRAPHY_5, ENTERTAINMENT, ENTERTAINMENT_5, SPORT, SPORT_5,
                AUTO, AUTO_5, FREQUENT_VERBS, FREQUENT_VERBS_5 -> true
                else -> false
            }
        }
    }
}