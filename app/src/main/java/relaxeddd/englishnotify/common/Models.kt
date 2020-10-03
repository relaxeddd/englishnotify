package relaxeddd.englishnotify.common

import androidx.annotation.Keep
import androidx.room.Entity
import androidx.room.PrimaryKey

@Keep
data class User(

    val userId: String = "",
    val email: String = "",
    var receiveNotifications: Boolean = true,
    var notificationsTimeType: Int = 0,
    val tagsAvailable: List<String> = ArrayList(),
    var tagsSelected: List<String> = ArrayList(),
    var learnLanguageType: Int = 0,
    var subscriptionTime: Long = 0,
    var selectedTag: String = IRREGULAR,
    var testCount: Int = 0
) {
    constructor(user: User) : this(user.userId, user.email, user.receiveNotifications, user.notificationsTimeType,
        user.tagsAvailable, user.tagsSelected, user.learnLanguageType, user.subscriptionTime, user.selectedTag,
        user.testCount)
}

@Entity(tableName = WORDS)
@Keep
data class Word(

    @PrimaryKey
    val id: String = "",
    var eng: String = "",
    var rus: String = "",
    var transcription: String = "",
    var tags: List<String> = ArrayList(),
    val sampleEng: String = "",
    val sampleRus: String = "",
    val v2: String = "",
    val v3: String = "",
    var timestamp: Long = 0L,
    var isDeleted: Boolean = false,
    var learnStage: Int = 0,
    var type: String = "",
    var isCreatedByUser: Boolean = true,
    var isOwnCategory: Boolean = false,
    var level: Int = 0
) {
    constructor(word: Word) : this(word.id,  word.eng, word.rus, word.transcription, word.tags, word.sampleEng, word.sampleRus,
        word.v2, word.v3, word.timestamp, word.isDeleted, word.learnStage, word.type, word.isCreatedByUser,
        word.isOwnCategory, word.level)
}

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

@Keep
data class UpdateUserResult(val result: Result?, val user: User?)

/*@Keep
data class CreateWordResult(val result: Result?, val word: Word? = null)*/

@Keep
data class PurchaseResult(val result: Result?, val userId: String = "", val tokenId: String = "", val itemType: String = "",
                          val refillInfo: RefillInfo = RefillInfo(), val isObtained: Boolean = false, val text: String = "")

@Keep
data class RefillInfo(val subscriptionTime: Long = 0)

@Keep
data class InitData(val result: Result?, val user: User?, val words: List<Word>? = null,
                    val isActualVersion: Boolean = true, val tagsInfo: List<TagInfo>? = ArrayList(),
                    val rating: List<RatingItem>? = ArrayList())

data class CategoryItem(val key: String)

@Keep
data class TagInfo(val key: String, var total: Int = 0, var learned: Int = 0, var received: Int = 0)

@Keep
data class RatingItem(val name: String, var value: Int = 0)
