package relaxeddd.englishnotify.common

import android.os.Build
import android.os.Bundle
import android.view.*
import androidx.annotation.*
import androidx.appcompat.widget.SearchView
import androidx.appcompat.widget.Toolbar
import androidx.core.view.forEach
import androidx.databinding.DataBindingUtil
import androidx.databinding.ViewDataBinding
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.Navigation
import relaxeddd.englishnotify.R
import relaxeddd.englishnotify.ui.main.MainActivity
import java.lang.IllegalStateException

abstract class BaseFragment<VM : ViewModelBase, B : ViewDataBinding> : Fragment() {

    protected var textSearch = ""
        set(value) {
            if (field != value) {
                field = value
                onSearchTextChanged(value)
            }
        }
    protected lateinit var viewModel: VM
    protected lateinit var binding: B
    protected var menu: Menu? = null
    protected var searchView: SearchView? = null

    @LayoutRes
    abstract fun getLayoutResId() : Int
    abstract fun getViewModelFactory() : ViewModelProvider.NewInstanceFactory
    abstract fun getViewModelClass(): Class<VM>
    protected open fun getToolbarTitleResId() = EMPTY_RES
    protected open fun getToolbarTitle() = ""
    protected open fun isHomeMenuButtonEnabled() = false
    protected open fun getHomeMenuButtonIconResId() = R.drawable.ic_menu
    protected open fun getHomeMenuButtonListener(): () -> Unit = {}
    @MenuRes
    protected open fun getMenuResId() = EMPTY_RES
    protected open fun onSearchTextChanged(searchText: String) {}
    protected open fun getSearchMenuItemId() = EMPTY_RES
    protected open fun getToolbarElevation() = 4f
    protected open fun setupThemeColors() {}
    protected open fun onSearchViewStateChanged(isCollapsed: Boolean) {}
    protected open fun hasToolbar() = true
    protected open fun isTopLevelFragment() = false
    @DrawableRes
    protected open fun getFabIconResId() = EMPTY_RES
    protected open fun getFabListener(): View.OnClickListener? = null

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        binding = DataBindingUtil.inflate(inflater, getLayoutResId(), null, false)
        viewModel = ViewModelProvider(this, getViewModelFactory()).get(getViewModelClass())

        configureBinding()
        binding.lifecycleOwner = viewLifecycleOwner
        binding.executePendingBindings()

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        view.findViewById<Toolbar>(R.id.toolbar)?.apply {
            val isOldNavigationDesign = SharedHelper.isOldNavigationDesign()

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                this.elevation = getToolbarElevation()
            }
            onCreateOptionsMenu(menu, activity?.menuInflater ?: return)

            (activity as? MainActivity)?.registerToolbar(this)
            val title = if (getToolbarTitleResId() != EMPTY_RES) getString(getToolbarTitleResId()) else getToolbarTitle()
            setTitle(title)
            updateToolbarTitle(title)

            if (!isTopLevelFragment()) {
                setNavigationIcon(getHomeMenuButtonIconResId())
                setNavigationOnClickListener {
                    getHomeMenuButtonListener().invoke()
                }
            } else if (!isOldNavigationDesign) {
                setNavigationIcon(R.drawable.ic_menu)
            } else {
                navigationIcon = null
            }
        }
        (activity as? MainActivity)?.configureFab(getFabIconResId(), getFabListener())
        setupThemeColors()
    }

    override fun onResume() {
        super.onResume()
        viewModel.onFragmentResume()
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        if (getMenuResId() == EMPTY_RES) {
            return
        }

        inflater.inflate(getMenuResId(), menu)
        this.menu = menu

        menu.forEach { menuItem ->
            menuItem.setOnMenuItemClickListener { onOptionsItemSelected(it) }
        }

        val searchItem = if (getSearchMenuItemId() != EMPTY_RES) menu.findItem(getSearchMenuItemId()) else null

        if (searchItem != null && searchItem.actionView != null) {
            searchView = searchItem.actionView as SearchView

            searchItem.setOnActionExpandListener( object: MenuItem.OnActionExpandListener {
                override fun onMenuItemActionExpand(item: MenuItem?): Boolean {
                    onSearchViewStateChanged(false)
                    return true
                }

                override fun onMenuItemActionCollapse(item: MenuItem?): Boolean {
                    textSearch = ""
                    onSearchViewStateChanged(true)
                    return true
                }
            })
            searchView?.setOnCloseListener {
                textSearch = ""
                onSearchViewStateChanged(true)
                true
            }
            searchView?.setOnQueryTextListener(object: SearchView.OnQueryTextListener {
                override fun onQueryTextChange(newText: String?): Boolean {
                    val changedText = newText?.toLowerCase() ?: ""
                    if (textSearch != changedText) {
                        textSearch = changedText
                    }
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
        viewModel.navigation.observe(viewLifecycleOwner, {
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
        activity?.findViewById<Toolbar>(R.id.toolbar)?.apply {
            setTitle(title)
        }
    }

    protected fun isViewModelInitialized() = this::viewModel.isInitialized
    protected fun isBindingInitialized() = this::binding.isInitialized
}
