package com.example.vchechin.testapp

import android.app.Application
import android.content.Context
import androidx.multidex.MultiDex

class App : Application() {

    companion object {
        lateinit var context: Context
    }

    override fun onCreate() {
        super.onCreate()
        context = this
        MultiDex.install(this)
    }
}