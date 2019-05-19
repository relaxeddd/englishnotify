package relaxeddd.englishnotify.common

interface ISelectCategory {

    fun getSelectedCategory() : String?
    fun setSelectedCategory(item : CategoryItem?)
}