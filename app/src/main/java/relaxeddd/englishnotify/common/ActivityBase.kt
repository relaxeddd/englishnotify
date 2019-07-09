package relaxeddd.englishnotify.common

import android.os.Bundle
import android.view.MenuItem
import androidx.annotation.CallSuper
import androidx.annotation.LayoutRes
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.databinding.ViewDataBinding
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelProviders
import relaxeddd.englishnotify.R

abstract class ActivityBase<VM : ViewModelBase, B : ViewDataBinding> : AppCompatActivity(), LifecycleOwner {

    private var listenerHomeMenuButton: () -> Unit = {}
    protected lateinit var binding: B
    protected lateinit var viewModel: VM
    var isMyResumed = false

    @LayoutRes
    abstract fun getLayoutResId() : Int
    abstract fun getViewModelFactory() : ViewModelProvider.NewInstanceFactory
    abstract fun getViewModelClass(): Class<VM>
    open fun onNavigationEvent(eventId: Int) {}
    protected open fun setupThemeColors() {}

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setupTheme()

        val factory = getViewModelFactory()

        binding = DataBindingUtil.setContentView(this, getLayoutResId())
        viewModel = ViewModelProviders.of(this, factory).get(getViewModelClass())
        configureBinding()
        binding.lifecycleOwner = this
        binding.executePendingBindings()

        setupThemeColors()
    }

    override fun onResume() {
        super.onResume()
        isMyResumed = true
    }

    override fun onPause() {
        super.onPause()
        isMyResumed = false
    }

    override fun onOptionsItemSelected(item: MenuItem) = when (item.itemId) {
        android.R.id.home -> {
            listenerHomeMenuButton.invoke()
            true
        }
        else -> super.onOptionsItemSelected(item)
    }

    @CallSuper
    protected open fun configureBinding() {
        viewModel.navigation.observe(this, Observer {
            it.getContentIfNotHandled()?.let {eventId ->
                onNavigationEvent(eventId)
            }
        })
    }

    fun configureMenu(isShowHomeMenuButton: Boolean = false, homeMenuButtonIconResId: Int, clickListener: () -> Unit, elevation: Float) {
        supportActionBar?.setDisplayHomeAsUpEnabled(isShowHomeMenuButton)
        supportActionBar?.setHomeAsUpIndicator(homeMenuButtonIconResId)
        supportActionBar?.elevation = elevation
        listenerHomeMenuButton = clickListener
    }

    private fun setupTheme() {
        when (SharedHelper.getAppThemeType(this)) {
            THEME_STANDARD -> setTheme(R.style.AppTheme)
            THEME_BLUE -> setTheme(R.style.AppTheme2)
            THEME_BLACK -> setTheme(R.style.AppTheme3)
        }
    }
}