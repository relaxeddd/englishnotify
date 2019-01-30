package com.example.vchechin.testapp.common

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = USERS)
data class User(

    @PrimaryKey
    val id: String = "",
    val email: String = "",
    val receiveNotifications: Boolean = true
)

@Entity(tableName = WORDS)
data class Word(

    @PrimaryKey
    val eng: String = "",
    val rus: String = "",
    val transcription: String = "",
    val tags: List<String> = ArrayList()
)