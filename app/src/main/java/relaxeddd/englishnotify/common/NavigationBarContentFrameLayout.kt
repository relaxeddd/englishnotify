package relaxeddd.englishnotify.common

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.util.AttributeSet
import android.view.WindowInsets
import android.widget.FrameLayout
import androidx.core.content.ContextCompat
import relaxeddd.englishnotify.R
import kotlin.math.roundToInt

class NavigationBarContentFrameLayout @JvmOverloads constructor(context: Context, attrs: AttributeSet? = null,
                                                                defStyleAttr: Int = 0)
        : FrameLayout(context, attrs, defStyleAttr) {

    private var navigationBarDividerColor: Int = 0
        set(value) {
            field = value
            dividerDrawable.color = value
        }

    private var navigationBarDividerSize: Int = 0
        set(value) {
            field = value
            updateDividerBounds()
        }

    private var lastInsets: WindowInsets? = null

    private val dividerDrawable by lazy(LazyThreadSafetyMode.NONE) {
        ColorDrawable(navigationBarDividerColor).apply {
            callback = this@NavigationBarContentFrameLayout
            alpha = 255
        }
    }

    init {
        val dividerSizeResId = if (SharedHelper.isOldNavigationDesign()) R.dimen.size_0 else R.dimen.size_1
        navigationBarDividerColor = ContextCompat.getColor(context, R.color.transparent)
        navigationBarDividerSize = resources.getDimension(dividerSizeResId).roundToInt()
    }

    override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {
        super.onSizeChanged(w, h, oldw, oldh)
        updateDividerBounds()
    }

    override fun draw(canvas: Canvas) {
        super.draw(canvas)

        val d = dividerDrawable
        if (!d.bounds.isEmpty) {
            d.draw(canvas)
        }
    }

    override fun onApplyWindowInsets(insets: WindowInsets): WindowInsets {
        lastInsets = insets
        updateDividerBounds()
        return insets
    }

    private fun updateDividerBounds() {
        if (android.os.Build.VERSION.SDK_INT < android.os.Build.VERSION_CODES.KITKAT_WATCH) {
            return
        }

        val d = dividerDrawable
        val insets = lastInsets

        val bottomInset = insets?.systemWindowInsetBottom ?: 0
        val leftInset = insets?.systemWindowInsetLeft ?: 0
        val rightInset = insets?.systemWindowInsetRight ?: 0

        when {
            bottomInset > 0 -> {
                d.setBounds(left, bottom - bottomInset, right, bottom + navigationBarDividerSize - bottomInset)
            }
            leftInset > 0 -> {
                d.setBounds(leftInset - navigationBarDividerSize, top, leftInset, bottom)
            }
            rightInset > 0 -> {
                d.setBounds(right - rightInset, top, right + navigationBarDividerSize - rightInset, bottom)
            }
            else -> {
                d.setBounds(0, 0, 0, 0)
            }
        }

        setWillNotDraw(d.bounds.isEmpty)
    }
}
