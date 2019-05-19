package relaxeddd.englishnotify.ui.categories

import androidx.annotation.StringRes
import relaxeddd.englishnotify.R

enum class CategorySection(@StringRes val titleResId: Int) {

    MAIN(R.string.main), NEW(R.string.new_categories), EXERCISES(R.string.exercises), OTHER(R.string.other)
}