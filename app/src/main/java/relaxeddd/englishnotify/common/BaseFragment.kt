package relaxeddd.englishnotify.common

import android.os.Bundle
import android.view.*
import androidx.annotation.CallSuper
import androidx.annotation.IdRes
import androidx.annotation.LayoutRes
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.SearchView
import androidx.databinding.DataBindingUtil
import androidx.databinding.ViewDataBinding
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.Navigation
import relaxeddd.englishnotify.R
import relaxeddd.englishnotify.ui.main.MainActivity
import java.lang.IllegalStateException

abstract class BaseFragment<VM : ViewModelBase, B : ViewDataBinding> : Fragment() {

    protected var textSearch = ""
        set(value) {
            field = value
            onSearchTextChanged(value)
        }
    protected lateinit var viewModel: VM
    protected lateinit var binding: B
    protected var menu: Menu? = null

    @LayoutRes
    abstract fun getLayoutResId() : Int
    abstract fun getViewModelFactory() : ViewModelProvider.NewInstanceFactory
    abstract fun getViewModelClass(): Class<VM>
    protected open fun getToolbarTitleResId() = EMPTY_RES
    protected open fun getToolbarTitle() = ""
    protected open fun isHomeMenuButtonEnabled() = false
    protected open fun getHomeMenuButtonIconResId() = R.drawable.ic_menu
    protected open fun getHomeMenuButtonListener(): () -> Unit = {}
    protected open fun getMenuResId() = EMPTY_RES
    protected open fun onSearchTextChanged(searchText: String) {}
    protected open fun getSearchMenuItemId() = EMPTY_RES
    protected open fun getToolbarElevation() = 4f
    protected open fun setupThemeColors() {}

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        binding = DataBindingUtil.inflate(inflater, getLayoutResId(), null, false)
        viewModel = ViewModelProvider(this, getViewModelFactory()).get(getViewModelClass())

        configureMenu()
        configureBinding()
        binding.lifecycleOwner = viewLifecycleOwner
        binding.executePendingBindings()

        return binding.root
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        val title = if (getToolbarTitleResId() != EMPTY_RES) getString(getToolbarTitleResId()) else getToolbarTitle()
        (activity as AppCompatActivity).supportActionBar?.title = title
    }

    override fun onResume() {
        super.onResume()
        viewModel.onFragmentResume()
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        super.onCreateOptionsMenu(menu, inflater)
        this.menu = menu
        if (getMenuResId() != EMPTY_RES) {
            inflater.inflate(getMenuResId(), menu)
        }

        val searchItem = if (getSearchMenuItemId() != EMPTY_RES) menu.findItem(getSearchMenuItemId()) else null

        if (searchItem != null && searchItem.actionView != null) {
            val searchView = searchItem.actionView as SearchView

            searchView.setOnCloseListener {
                textSearch = ""
                true
            }
            searchView.setOnQueryTextListener(object: SearchView.OnQueryTextListener {
                override fun onQueryTextChange(newText: String?): Boolean {
                    textSearch = newText?.toLowerCase() ?: ""
                    return true
                }

                override fun onQueryTextSubmit(query: String?): Boolean {
                    return true
                }
            })
        }
    }

    protected open fun onNavigationEvent(eventId: Int) {
        when (eventId) {
            NAVIGATION_LOADING_SHOW -> {
                if (activity is MainActivity) {
                    (activity as MainActivity).setLoadingVisible(true)
                }
            }
            NAVIGATION_LOADING_HIDE -> {
                if (activity is MainActivity) {
                    (activity as MainActivity).setLoadingVisible(false)
                }
            }
            NAVIGATION_ACTIVITY_BACK -> {
                if (activity is MainActivity && (activity as MainActivity).isMyResumed) {
                    activity?.onBackPressed()
                }
            }
            NAVIGATION_ACTIVITY_BACK_TWICE -> {
                try {
                    val navController = Navigation.findNavController(activity ?: return, R.id.fragment_navigation_host)
                    navController.popBackStack()
                    onNavigationEvent(NAVIGATION_ACTIVITY_BACK)
                } catch (e: IllegalStateException) {}
            }
            else -> {
                if (activity is MainActivity) {
                    (activity as MainActivity).onNavigationEvent(eventId)
                }
            }
        }
    }

    @CallSuper
    protected open fun configureBinding() {
        viewModel.navigation.observe(this, Observer {
            it.getContentIfNotHandled()?.let {eventId ->
                onNavigationEvent(eventId)
            }
        })
    }

    protected fun navigate(@IdRes actionId: Int, args: Bundle? = null) {
        try {
            Navigation.findNavController(activity ?: return, R.id.fragment_navigation_host).navigate(actionId, args)
        } catch (e: IllegalStateException) {}
    }

    protected fun updateToolbarTitle(title: String) {
        if (activity != null) {
            (activity as AppCompatActivity).supportActionBar?.title = title
        }
    }

    private fun configureMenu() {
        setHasOptionsMenu(true)
        (activity as ActivityBase<*, *>).configureMenu(isHomeMenuButtonEnabled(), getHomeMenuButtonIconResId(),
            getHomeMenuButtonListener(), getToolbarElevation())
    }
}