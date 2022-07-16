package relaxeddd.englishnotify.common

import androidx.annotation.Keep

@Keep
open class Event<out T>(private val content: T) {
    private var hasBeenHandled = false

    fun getContentIfNotHandled(): T? {
        return if (hasBeenHandled) {
            null
        } else {
            hasBeenHandled = true
            content
        }
    }
}

@Keep
data class AnimBlock(var isAnimating: Boolean = false)

@Keep
data class Result(val code: Int = RESULT_UNDEFINED, val message: String = "") {
    fun isSuccess() = code == RESULT_OK
}

data class CategoryItem(val key: String)
