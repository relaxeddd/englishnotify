package relaxeddd.pushenglish.common

import androidx.annotation.StringRes
import relaxeddd.pushenglish.App
import relaxeddd.pushenglish.R

enum class SortByType(@StringRes val nameResId: Int) {

    ALPHABETICAL_NAME(R.string.alphabetical_name), ALPHABETICAL_TRANSLATE(R.string.alphabetical_translate),
    TIME_NEW(R.string.time_new), TIME_OLD(R.string.time_old);

    fun getTitle() = App.context.getString(nameResId)

    companion object {
        fun getNamesArray() = Array<String>(values().size) { App.context.getString(values()[it].nameResId) }
    }
}