package com.example.vchechin.testapp.common

import android.animation.ValueAnimator
import android.content.Context
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import com.example.vchechin.testapp.App
import android.util.DisplayMetrics
import android.view.View.*
import android.net.NetworkInfo
import android.content.Context.CONNECTIVITY_SERVICE
import androidx.core.content.ContextCompat.getSystemService
import android.net.ConnectivityManager
import androidx.annotation.StringRes


fun showToast(string: String) {
    Toast.makeText(App.context, string, Toast.LENGTH_SHORT).show()
}

fun showToast(@StringRes resId: Int) {
    Toast.makeText(App.context, resId, Toast.LENGTH_SHORT).show()
}

fun convertDpToPixel(dp: Float): Float {
    return dp * (App.context.resources.displayMetrics.densityDpi.toFloat() / DisplayMetrics.DENSITY_DEFAULT)
}

fun animateDropdown(view: ViewGroup, isOpen: Boolean, animBlock: AnimBlock = AnimBlock(false), paddingDp: Float = 0f) {
    if (animBlock.isAnimating || view.visibility == GONE && !isOpen || view.visibility == VISIBLE && isOpen) return

    val widthMeasureSpec = View.MeasureSpec.makeMeasureSpec(App.context.resources.displayMetrics.widthPixels,
                                                            View.MeasureSpec.AT_MOST)
    val heightMeasureSpec = MeasureSpec.makeMeasureSpec(0, MeasureSpec.UNSPECIFIED)

    view.measure(widthMeasureSpec, heightMeasureSpec)
    val maxHeight = view.measuredHeight + convertDpToPixel(paddingDp).toInt()
    val animator: ValueAnimator

    animBlock.isAnimating = true
    if (isOpen) {
        view.visibility = View.VISIBLE
        animator = ValueAnimator.ofInt(1, maxHeight)
    } else {
        animator = ValueAnimator.ofInt(maxHeight, 0)
    }

    animator.addUpdateListener { valueAnimator ->
        val height = valueAnimator.animatedValue as Int
        val params = view.layoutParams
        params.height = height
        view.layoutParams = params

        if (!isOpen && height == 0) {
            view.visibility = View.GONE
        }
        if ((!isOpen && height == 0) || (isOpen && height == maxHeight)) {
            animBlock.isAnimating = false
        }
    }
    animator.duration = 200
    animator.start()
}

infix fun <T> Collection<T>.equalsIgnoreOrder(collection: Collection<T>?)
        = collection?.let { this.size == it.size && this.containsAll(it) } ?: false

internal fun <T> Collection<T>.print() : String {
    var string = ""
    this.forEach {
        if (string.isNotEmpty()) string += ", "
        string += it
    }
    return string
}

fun isNetworkAvailable(): Boolean {
    val connectivityManager = App.context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager?
    val activeNetworkInfo = connectivityManager!!.activeNetworkInfo
    return activeNetworkInfo != null && activeNetworkInfo.isConnected
}

fun getString(@StringRes resId: Int) = App.context.getString(resId)