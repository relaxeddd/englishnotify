package relaxeddd.englishnotify.common

interface ISelectCategory {

    fun getSelectedCategory() : CategoryItem?
    fun setSelectedCategory(item : CategoryItem?)
}