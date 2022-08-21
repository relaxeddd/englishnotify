package relaxeddd.englishnotify.view_base

import android.os.Bundle
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import androidx.annotation.CallSuper
import androidx.annotation.DrawableRes
import androidx.annotation.IdRes
import androidx.annotation.MenuRes
import androidx.appcompat.widget.SearchView
import androidx.appcompat.widget.Toolbar
import androidx.core.view.forEach
import androidx.fragment.app.Fragment
import androidx.viewbinding.ViewBinding
import relaxeddd.englishnotify.common.EMPTY_RES
import relaxeddd.englishnotify.common.NAVIGATION_ACTIVITY_BACK
import relaxeddd.englishnotify.common.NAVIGATION_ACTIVITY_BACK_TWICE
import relaxeddd.englishnotify.preferences.Preferences
import relaxeddd.englishnotify.view_base.interfaces.IFabOwner
import relaxeddd.englishnotify.view_base.interfaces.INavControllerOwner
import relaxeddd.englishnotify.view_base.interfaces.INavigationOwner
import relaxeddd.englishnotify.view_base.interfaces.IToolbarOwner

abstract class BaseDaggerlessFragment<VM : ViewModelBase, B : ViewBinding> : Fragment() {

    protected abstract val prefs: Preferences

    protected abstract val viewModel: VM

    protected var textSearch = ""
        set(value) {
            if (field != value) {
                field = value
                onSearchTextChanged(value)
            }
        }
    protected var binding: B? = null
    protected var menu: Menu? = null
    protected var searchView: SearchView? = null

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

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        subscribeToViewModel()

        view.findViewById<Toolbar>(R.id.toolbar)?.apply {
            val isOldNavigationDesign = prefs.isBottomNavigation()

            this.elevation = getToolbarElevation()
            onCreateOptionsMenu(menu, activity?.menuInflater ?: return)

            (activity as? IToolbarOwner)?.registerToolbar(this)
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
        (activity as? IFabOwner)?.configureFab(getFabIconResId(), getFabListener())
        setupThemeColors()
    }

    override fun onResume() {
        super.onResume()
        viewModel.onFragmentResume()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        binding = null
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
                    val changedText = newText?.lowercase() ?: ""
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
            NAVIGATION_ACTIVITY_BACK_TWICE -> {
                try {
                    val navController = (activity as? INavControllerOwner)?.getNavController()
                    navController?.popBackStack()
                    onNavigationEvent(NAVIGATION_ACTIVITY_BACK)
                } catch (e: IllegalStateException) {}
            }
            else -> (activity as? INavigationOwner)?.onNavigationEvent(eventId)
        }
    }

    @CallSuper
    protected open fun subscribeToViewModel() {
        viewModel.navigation.observe(viewLifecycleOwner) {
            it.getContentIfNotHandled()?.let { eventId ->
                onNavigationEvent(eventId)
            }
        }
    }

    protected fun navigate(@IdRes actionId: Int, args: Bundle? = null) {
        try {
            (activity as? INavControllerOwner)?.getNavController()?.navigate(actionId, args)
        } catch (e: IllegalStateException) {}
    }

    protected fun updateToolbarTitle(title: String) {
        activity?.findViewById<Toolbar>(R.id.toolbar)?.apply {
            setTitle(title)
        }
    }
}
