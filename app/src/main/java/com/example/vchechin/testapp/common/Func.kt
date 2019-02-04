package com.example.vchechin.testapp.common

import android.widget.Toast
import com.example.vchechin.testapp.App

fun showToast(string: String) {
    Toast.makeText(App.context, string, Toast.LENGTH_SHORT).show()
}

infix fun <T> Collection<T>.equalsIgnoeOrder(collection: Collection<T>?)
        = collection?.let { this.size == it.size && this.containsAll(it) } ?: false