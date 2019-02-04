package com.example.vchechin.testapp.common

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
    var tagsSelected: List<String> = ArrayList()
) {
    constructor(user: User) : this(user.id, user.email, user.receiveNotifications, user.notificationsTimeType,
        user.tagsAvailable, user.tagsSelected)
}

@Entity(tableName = WORDS)
data class Word(

    @PrimaryKey
    val eng: String = "",
    val rus: String = "",
    val transcription: String = "",
    val tags: List<String> = ArrayList()
)

data class Resource<T>(

    val status: Int = STATUS_UNDEFINED,
    val errorStr: String = "",
    val value: T
) {
    fun isSuccess() = status == STATUS_OK
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