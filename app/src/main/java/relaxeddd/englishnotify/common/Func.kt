@file:Suppress("unused")
package relaxeddd.englishnotify.common

import android.animation.ValueAnimator
import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.graphics.drawable.Drawable
import android.net.Uri
import android.os.Build
import android.text.SpannableString
import android.text.Spanned
import android.text.style.ClickableSpan
import android.text.style.UnderlineSpan
import android.util.DisplayMetrics
import android.view.View
import android.view.ViewGroup
import android.view.WindowInsets
import android.view.inputmethod.InputMethodManager
import android.widget.Toast
import androidx.annotation.IdRes
import androidx.annotation.RequiresApi
import androidx.annotation.StringRes
import androidx.appcompat.content.res.AppCompatResources
import androidx.core.graphics.drawable.DrawableCompat
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.fragment.app.FragmentActivity
import androidx.navigation.NavController
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import relaxeddd.englishnotify.App
import relaxeddd.englishnotify.R
import relaxeddd.englishnotify.preferences.utils.THEME_BLACK
import relaxeddd.englishnotify.preferences.utils.THEME_BLUE
import relaxeddd.englishnotify.preferences.utils.THEME_BLUE_LIGHT
import java.util.regex.Pattern

fun getPrimaryColorResId(appThemeType: Int) = when (appThemeType) {
    THEME_BLUE -> R.color.colorPrimary2
    THEME_BLACK -> R.color.colorPrimary3
    THEME_BLUE_LIGHT -> R.color.colorPrimary4
    else -> R.color.colorPrimary
}

fun getPrimaryDarkColorResId(appThemeType: Int) = when (appThemeType) {
    THEME_BLUE -> R.color.colorPrimaryDark2
    THEME_BLACK -> R.color.colorPrimaryDark3
    THEME_BLUE_LIGHT -> R.color.colorPrimaryDark4
    else -> R.color.colorPrimaryDark
}

fun getAccentColorResId(appThemeType: Int) = when (appThemeType) {
    THEME_BLUE -> R.color.colorAccent2
    THEME_BLACK -> R.color.colorAccent3
    THEME_BLUE_LIGHT -> R.color.colorAccent4
    else -> R.color.colorAccent
}

fun showToast(string: String) {
    CoroutineScope(Dispatchers.Main).launch { Toast.makeText(App.context, string, Toast.LENGTH_SHORT).show() }
}

fun showToast(@StringRes resId: Int) {
    CoroutineScope(Dispatchers.Main).launch { Toast.makeText(App.context, resId, Toast.LENGTH_SHORT).show() }
}

fun showToastLong(@StringRes resId: Int) {
    CoroutineScope(Dispatchers.Main).launch { Toast.makeText(App.context, resId, Toast.LENGTH_LONG).show() }
}

fun showKeyboard(view: View?) {
    view ?: return
    view.postDelayed({
        if (!view.isShown) {
            return@postDelayed
        }
        view.requestFocus()
    }, 100)
    view.postDelayed({
        if (!view.isShown) {
            return@postDelayed
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            view.windowInsetsController?.show(WindowInsets.Type.ime())
        } else {
            val imm: InputMethodManager? = view.context.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager?
            imm?.showSoftInput(view, InputMethodManager.SHOW_IMPLICIT)
        }
    }, 300)
}

fun hideKeyboard(view: View?) {
    view ?: return
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
        view.windowInsetsController?.hide(WindowInsets.Type.ime())
    } else {
        val imm = view.context.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager?
        imm?.hideSoftInputFromWindow(view.windowToken, 0)
    }
    view.clearFocus()
}

fun convertDpToPixel(dp: Float): Float {
    return dp * (App.context.resources.displayMetrics.densityDpi.toFloat() / DisplayMetrics.DENSITY_DEFAULT)
}

fun animateDropdown(view: ViewGroup, isOpen: Boolean, animBlock: AnimBlock = AnimBlock(false),
                    paddingDp: Float = 0f) {
    if (animBlock.isAnimating || view.visibility == View.GONE && !isOpen || view.visibility == View.VISIBLE && isOpen) return

    val widthMeasureSpec = View.MeasureSpec.makeMeasureSpec(App.context.resources.displayMetrics.widthPixels, View.MeasureSpec.AT_MOST)
    val heightMeasureSpec = View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED)

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

fun getAppString(@StringRes resId: Int) : String = App.context.getString(resId)

fun getAppString(@StringRes resId: Int, arg: String) : String = App.context.getString(resId, arg)

fun getStringByResName(resName: String): String {
    val packageName = App.context.packageName
    val resId = App.context.resources.getIdentifier(resName, "string", packageName)
    return if (resId != 0) getAppString(resId) else resName
}

fun openWebPrivacyPolicy(activity: FragmentActivity?) {
    val browserIntent = Intent(Intent.ACTION_VIEW, Uri.parse("https://sites.google.com/view/pushenglish"))
    browserIntent.flags = Intent.FLAG_ACTIVITY_REORDER_TO_FRONT
    activity?.startActivity(browserIntent)
}

fun openWebApplication(activity: FragmentActivity?) {
    val browserIntent = Intent(Intent.ACTION_VIEW, Uri.parse("https://play.google.com/store/apps/details?id=relaxeddd.englishnotify"))
    browserIntent.flags = Intent.FLAG_ACTIVITY_REORDER_TO_FRONT
    activity?.startActivity(browserIntent)
}

fun isValidNickname(nickname: String) : Boolean {
    val pattern = Pattern.compile("^[a-zA-Zа-яА-Я][a-zA-Zа-яА-Я0-9_]{3,16}$")
    val matcher = pattern.matcher(nickname)
    return matcher.matches() && nickname != "null" && nickname != "NoName"
}

internal fun NavController.myNavigate(@IdRes resId: Int) {
    try {
        navigate(resId)
    } catch (e: IllegalStateException) {}
}

@RequiresApi(Build.VERSION_CODES.KITKAT_WATCH)
object NoopWindowInsetsListener : View.OnApplyWindowInsetsListener {
    override fun onApplyWindowInsets(v: View, insets: WindowInsets): WindowInsets {
        return insets
    }
}

@RequiresApi(Build.VERSION_CODES.KITKAT_WATCH)
object HeightTopWindowInsetsListener : View.OnApplyWindowInsetsListener {
    override fun onApplyWindowInsets(v: View, insets: WindowInsets): WindowInsets {
        val topInset = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            insets.getInsets(WindowInsets.Type.statusBars()).top
        } else {
            @Suppress("DEPRECATION")
            insets.systemWindowInsetTop
        }

        if (v.layoutParams.height != topInset) {
            v.layoutParams.height = topInset
            v.requestLayout()
        }
        return insets
    }
}

data class ViewPaddingState(
    val left: Int,
    val top: Int,
    val right: Int,
    val bottom: Int,
    val start: Int,
    val end: Int
)

fun View.doOnApplyWindowInsets(f: (View, WindowInsetsCompat, ViewPaddingState) -> Unit) {
    val paddingState = createStateForView(this)
    ViewCompat.setOnApplyWindowInsetsListener(this) { v, insets ->
        f(v, insets, paddingState)
        insets
    }
    requestApplyInsetsWhenAttached()
}

@RequiresApi(Build.VERSION_CODES.KITKAT_WATCH)
fun View.requestApplyInsetsWhenAttached() {
    if (isAttachedToWindow) {
        requestApplyInsets()
    } else {
        addOnAttachStateChangeListener(object : View.OnAttachStateChangeListener {
            override fun onViewAttachedToWindow(v: View) {
                v.requestApplyInsets()
            }

            override fun onViewDetachedFromWindow(v: View) = Unit
        })
    }
}

private fun createStateForView(view: View) = ViewPaddingState(view.paddingLeft,
    view.paddingTop, view.paddingRight, view.paddingBottom, view.paddingStart, view.paddingEnd)

@SuppressLint("ResourceType")
fun navigationItemBackground(context: Context): Drawable? {
    var background = AppCompatResources.getDrawable(context, R.drawable.navigation_item_background)

    if (background != null) {
        try {
            val tint = AppCompatResources.getColorStateList(context, R.drawable.navigation_item_background_tint)

            background = DrawableCompat.wrap(background.mutate())
            background.setTintList(tint)
        } catch (e: Exception) {}
    }

    return background
}

fun setClickableSubstring(string: String, spannableString: SpannableString, substring: String, clickableSpan: ClickableSpan) {
    val firstIndex = string.indexOf(substring)
    val lastIndex = firstIndex + substring.length

    spannableString.setSpan(clickableSpan, firstIndex, lastIndex, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE)
    spannableString.setSpan(UnderlineSpan(), firstIndex, lastIndex, 0)
}
