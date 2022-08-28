package relaxeddd.englishnotify.common

import androidx.annotation.Keep

@Keep
data class AnimBlock(var isAnimating: Boolean = false)

data class CategoryItem(val key: String)

data class TagInfo(var total: Int = 0, var learned: Int = 0, var received: Int = 0)
