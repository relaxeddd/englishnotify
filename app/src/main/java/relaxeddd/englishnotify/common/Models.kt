package relaxeddd.englishnotify.common

import androidx.annotation.Keep

@Keep
data class AnimBlock(var isAnimating: Boolean = false)

@Keep
data class Result(val code: Int = RESULT_UNDEFINED, val message: String = "") {
    fun isSuccess() = code == RESULT_OK
}

data class CategoryItem(val key: String)

data class TagInfo(var total: Int = 0, var learned: Int = 0, var received: Int = 0)
