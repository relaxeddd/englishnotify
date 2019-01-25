package com.example.vchechin.testapp.data

import androidx.databinding.Observable
import androidx.databinding.ObservableField
import androidx.lifecycle.LiveData
import com.example.vchechin.testapp.model.CacheStub

object LiveDataEmail : LiveData<String>() {

    val callback = object : Observable.OnPropertyChangedCallback() {
        override fun onPropertyChanged(sender: Observable?, propertyId: Int) {
            value = CacheStub.email.get()
        }
    }

    override fun onActive() {
        value = CacheStub.email.get()
        CacheStub.email.addOnPropertyChangedCallback(callback)
    }

    override fun onInactive() {
        CacheStub.email.removeOnPropertyChangedCallback(callback)
    }
}