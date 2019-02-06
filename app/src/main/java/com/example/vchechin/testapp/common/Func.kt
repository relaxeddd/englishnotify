package com.example.vchechin.testapp.common

import android.animation.ValueAnimator
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import com.example.vchechin.testapp.App
import android.util.DisplayMetrics
import android.view.View.MeasureSpec

fun showToast(string: String) {
    Toast.makeText(App.context, string, Toast.LENGTH_SHORT).show()
}

fun convertDpToPixel(dp: Float): Float {
    return dp * (App.context.resources.displayMetrics.densityDpi.toFloat() / DisplayMetrics.DENSITY_DEFAULT)
}

fun animateDropdown(view: ViewGroup, isOpen: Boolean, paddingDp: Float = 0f) {
    val widthMeasureSpec = View.MeasureSpec.makeMeasureSpec(App.context.resources.displayMetrics.widthPixels,
                                                            View.MeasureSpec.AT_MOST)
    val heightMeasureSpec = MeasureSpec.makeMeasureSpec(0, MeasureSpec.UNSPECIFIED)

    view.measure(widthMeasureSpec, heightMeasureSpec)
    val maxHeight = view.measuredHeight + convertDpToPixel(paddingDp).toInt()
    val animator: ValueAnimator

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
    }
    animator.duration = 200
    animator.start()
}

infix fun <T> Collection<T>.equalsIgnoreOrder(collection: Collection<T>?)
        = collection?.let { this.size == it.size && this.containsAll(it) } ?: false