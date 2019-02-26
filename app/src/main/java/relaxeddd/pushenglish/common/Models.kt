package relaxeddd.pushenglish.common

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = USERS)
data class User(

    @PrimaryKey
    val id: String = "",
    val email: String = "",
    var receiveNotifications: Boolean = true,
    var notificationsTimeType: Int = 1,
    val tagsAvailable: List<String> = ArrayList(),
    var tagsSelected: List<String> = ArrayList(),
    var learnLanguageType: Int = 0
) {
    constructor(user: User) : this(user.id, user.email, user.receiveNotifications, user.notificationsTimeType,
        user.tagsAvailable, user.tagsSelected, user.learnLanguageType)
}

@Entity(tableName = WORDS)
data class Word(

    @PrimaryKey
    val eng: String = "",
    val rus: String = "",
    val transcription: String = "",
    val tags: List<String> = ArrayList(),
    val sampleEng: String = "",
    val sampleRus: String = "",
    val v2: String = "",
    val v3: String = "",
    val timestamp: Long = 0
)

data class Resource<T>(

    val status: Int = RESULT_UNDEFINED,
    val errorStr: String = "",
    val value: T? = null
) {
    fun isSuccess() = status == RESULT_OK
}

open class Event<out T>(private val content: T) {
    var hasBeenHandled = false
        private set

    fun getContentIfNotHandled(): T? {
        return if (hasBeenHandled) {
            null
        } else {
            hasBeenHandled = true
            content
        }
    }

    fun peekContent(): T = content
}

data class AnimBlock(var isAnimating: Boolean = false)

data class Result(val code: Int = RESULT_UNDEFINED, val msg: String = "") {
    fun isSuccess() = code == RESULT_OK
}

data class UpdateUserResult(val result: Result, val user: User)

data class PurchaseResult(val code: Int = RESULT_UNDEFINED)

data class InitData(val result: Result, val user: User, val isActualVersion: Boolean = true)