package relaxeddd.englishnotify.common

import android.animation.ValueAnimator
import android.content.Context
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import relaxeddd.englishnotify.App
import android.util.DisplayMetrics
import android.view.View.*
import android.content.Intent
import android.net.ConnectivityManager
import android.net.Uri
import androidx.annotation.StringRes
import androidx.fragment.app.FragmentActivity
import relaxeddd.englishnotify.R

fun showToast(string: String) {
    Toast.makeText(App.context, string, Toast.LENGTH_SHORT).show()
}

fun showToast(@StringRes resId: Int) {
    Toast.makeText(App.context, resId, Toast.LENGTH_SHORT).show()
}

fun convertDpToPixel(dp: Float): Float {
    return dp * (App.context.resources.displayMetrics.densityDpi.toFloat() / DisplayMetrics.DENSITY_DEFAULT)
}

fun animateDropdown(view: ViewGroup, isOpen: Boolean, animBlock: AnimBlock = AnimBlock(false),
                    paddingDp: Float = 0f) {
    if (animBlock.isAnimating || view.visibility == GONE && !isOpen || view.visibility == VISIBLE && isOpen) return

    val widthMeasureSpec = View.MeasureSpec.makeMeasureSpec(
        App.context.resources.displayMetrics.widthPixels,
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

fun getStringByResName(resName: String): String {
    val packageName = App.context.packageName
    val resId = App.context.resources.getIdentifier(resName, "string", packageName)
    return if (resId != 0) getString(resId) else resName
}

fun openWebPrivacyPolicy(activity: FragmentActivity?) {
    val browserIntent = Intent(Intent.ACTION_VIEW, Uri.parse("https://sites.google.com/view/pushenglish"))
    browserIntent.flags = Intent.FLAG_ACTIVITY_REORDER_TO_FRONT
    activity?.startActivity(browserIntent)
}

fun openWebApplication(activity: FragmentActivity?) {
    val browserIntent = Intent(Intent.ACTION_VIEW, Uri.parse("http://google.com"))
    browserIntent.flags = Intent.FLAG_ACTIVITY_REORDER_TO_FRONT
    activity?.startActivity(browserIntent)
}

fun getErrorString(code: Int) = getErrorString(Result(code, getString(R.string.undefined_error)))

fun getErrorString(result: Result) = when (result.code) {
    RESULT_UNDEFINED -> getString(R.string.undefined_error)
    RESULT_ERROR_SEND_FEEDBACK -> getString(R.string.feedback_send_error)
    RESULT_ERROR_FEEDBACK_TOO_SHORT -> getString(R.string.feedback_too_short)
    RESULT_ERROR_NETWORK -> getString(R.string.network_not_available)
    RESULT_LOCAL_ERROR -> getString(R.string.undefined_error)
    RESULT_ERROR_UNAUTHORIZED -> getString(R.string.unauthorized_error)
    RESULT_ERROR_USER_NOT_FOUND -> getString(R.string.user_not_found)
    RESULT_ERROR_APP_INIT -> getString(R.string.error_initialization)
    RESULT_ERROR_ADD_PUSH_TOKEN -> getString(R.string.error_push_token)
    RESULT_ERROR_UPDATE_USER -> getString(R.string.error_update)
    else -> result.msg
}