package relaxeddd.englishnotify.common

import android.view.View
import androidx.annotation.DrawableRes
import androidx.appcompat.widget.Toolbar
import com.google.android.material.radiobutton.MaterialRadioButton

interface ListenerResult<T> {

    fun onResult(result: T)
}

interface ISelectCategory {

    fun getSelectedCategory() : String?
    fun setSelectedCategory(item : CategoryItem?)
    fun onRadioButtonInit(category: String, radioButton: MaterialRadioButton)
}

interface NavigationHost {

    fun registerToolbar(toolbar: Toolbar)
}

interface FloatingActionButtonHost {

    fun configureFab(@DrawableRes iconResId: Int?, listener: (View.OnClickListener)?)
}
