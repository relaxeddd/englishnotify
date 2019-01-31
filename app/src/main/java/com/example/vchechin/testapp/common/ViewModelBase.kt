package com.example.vchechin.testapp.common

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers

open abstract class ViewModelBase : ViewModel() {

    protected val navigateEvent = MutableLiveData<Event<Int>>()
    protected val uiScope = CoroutineScope(Dispatchers.Main)

    val navigation : LiveData<Event<Int>>
        get() = navigateEvent
}