package relaxeddd.englishnotify.common

import androidx.annotation.Keep
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = USERS)
@Keep
data class User(

    @PrimaryKey
    val userId: String = "",
    val email: String = "",
    var receiveNotifications: Boolean = true,
    var notificationsTimeType: Int = 1,
    val tagsAvailable: List<String> = ArrayList(),
    var tagsSelected: List<String> = ArrayList(),
    var learnLanguageType: Int = 0,
    var subscriptionTime: Long = 0,
    var selectedTag: String = "irregular",
    var testCount: Int = 0
) {
    constructor(user: User) : this(user.userId, user.email, user.receiveNotifications, user.notificationsTimeType,
        user.tagsAvailable, user.tagsSelected, user.learnLanguageType, user.subscriptionTime, user.selectedTag, user.testCount)
}

@Entity(tableName = WORDS)
@Keep
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
    val timestamp: Long = 0,
    var isDeleted: Boolean = false,
    var isLearned: Boolean = false
)

@Keep
data class Resource<T>(

    val status: Int = RESULT_UNDEFINED,
    val errorStr: String = "",
    val value: T? = null
) {
    fun isSuccess() = status == RESULT_OK
}

@Keep
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

@Keep
data class AnimBlock(var isAnimating: Boolean = false)

@Keep
data class Result(val code: Int = RESULT_UNDEFINED, val msg: String = "") {
    fun isSuccess() = code == RESULT_OK
}

@Keep
data class UpdateUserResult(val result: Result?, val user: User?)

@Keep
data class PurchaseResult(val result: Result?, val userId: String = "", val tokenId: String = "", val itemType: String = "",
                          val refillInfo: RefillInfo = RefillInfo(), val isObtained: Boolean = false, val text: String = "")

@Keep
data class PurchaseObject(val userId: String, val tokenId: String, val itemType: String, val refillInfo: RefillInfo,
                          val isObtained: Boolean = false)

@Keep
data class RefillInfo(val subscriptionTime: Long = 0)

@Keep
data class InitData(val result: Result?, val user: User?, val isActualVersion: Boolean = true)