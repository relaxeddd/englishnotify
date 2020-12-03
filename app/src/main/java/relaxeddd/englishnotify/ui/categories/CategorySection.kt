package relaxeddd.englishnotify.ui.categories

import androidx.annotation.StringRes
import relaxeddd.englishnotify.R

enum class CategorySection(@StringRes val titleResId: Int) {

    MAIN(R.string.main), OWN_CATEGORIES(R.string.own_categories), EXERCISES(R.string.exercises), OTHER(R.string.other)
}
