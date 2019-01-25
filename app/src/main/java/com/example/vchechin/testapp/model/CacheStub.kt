package com.example.vchechin.testapp.model

import androidx.databinding.Observable
import androidx.databinding.ObservableField
import com.example.vchechin.testapp.common.Word
import com.example.vchechin.testapp.data.LiveDataEmail

object CacheStub {

    var email = ObservableField("abilion.wot@gmail.com")

    var words = arrayListOf(Word("dog", "собака", "dog", arrayListOf("noun", "top1")),
        Word("different", "разные", "ˈdif(ə)rənt", arrayListOf("adjective", "top1")),
        Word("suspend", "приостановить", "səˈspend", arrayListOf("verb")))
}