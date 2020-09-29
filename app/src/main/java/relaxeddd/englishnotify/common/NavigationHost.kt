package relaxeddd.englishnotify.common

import android.view.View
import androidx.annotation.DrawableRes
import androidx.appcompat.widget.Toolbar

interface NavigationHost {

    fun registerToolbar(toolbar: Toolbar)
}

interface FloatingActionButtonHost {

    fun configureFab(@DrawableRes iconResId: Int?, listener: (View.OnClickListener)?)
}
