package relaxeddd.englishnotify.view_base.interfaces

import android.view.View

interface IFabOwner {

    fun configureFab(iconResId: Int?, listener: (View.OnClickListener)?)
}
