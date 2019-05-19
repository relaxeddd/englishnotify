package relaxeddd.englishnotify.common

import com.google.android.material.radiobutton.MaterialRadioButton

interface ISelectCategory {

    fun getSelectedCategory() : String?
    fun setSelectedCategory(item : CategoryItem?)
    fun onRadioButtonInit(category: String, radioButton: MaterialRadioButton)
}