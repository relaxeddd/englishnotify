package relaxeddd.englishnotify.preferences.models

import android.content.Context
import androidx.annotation.StringRes
import relaxeddd.englishnotify.preferences.R

enum class SortByType(@StringRes val nameResId: Int) {

    ALPHABETICAL_NAME(R.string.alphabetical_name), ALPHABETICAL_TRANSLATE(R.string.alphabetical_translate),
    TIME_NEW(R.string.time_new), TIME_OLD(R.string.time_old);

    fun getTitle(context: Context) : String = context.getString(nameResId)

    companion object {
        fun getNamesArray(context: Context) = Array(values().size) { context.getString(values()[it].nameResId) }

        fun getByName(name: String) = when(name) {
            ALPHABETICAL_NAME.name -> ALPHABETICAL_NAME
            ALPHABETICAL_TRANSLATE.name -> ALPHABETICAL_TRANSLATE
            TIME_OLD.name -> TIME_OLD
            else -> TIME_NEW
        }
    }
}
