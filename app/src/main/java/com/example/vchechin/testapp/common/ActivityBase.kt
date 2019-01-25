package com.example.vchechin.testapp.common

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.LifecycleOwner

abstract class ActivityBase : AppCompatActivity(), LifecycleOwner {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        configureBinding()
    }

    protected open fun configureBinding() {}
}