package relaxeddd.englishnotify.common

import android.os.Bundle
import android.view.*
import androidx.annotation.CallSuper
import androidx.annotation.LayoutRes
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.SearchView
import androidx.databinding.DataBindingUtil
import androidx.databinding.ViewDataBinding
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelProviders
import relaxeddd.englishnotify.R

abstract class BaseFragment<VM : ViewModelBase, B : ViewDataBinding> : Fragment() {

    protected var textSearch = ""
        set(value) {
            field = value
            onSearchTextChanged(value)
        }
    protected lateinit var viewModel: VM
    protected lateinit var binding: B

    @LayoutRes
    abstract fun getLayoutResId() : Int
    abstract fun getToolbarTitleResId(): Int
    abstract fun getViewModelFactory() : ViewModelProvider.NewInstanceFactory
    abstract fun getViewModelClass(): Class<VM>
    protected open fun isHomeMenuButtonEnabled() = false
    protected open fun getHomeMenuButtonIconResId() = R.drawable.ic_menu
    protected open fun getHomeMenuButtonListener() = {}
    protected open fun getMenuResId() = EMPTY_RES
    protected open fun onNavigationEvent(eventId: Int) {}
    protected open fun onSearchTextChanged(searchText: String) {}
    protected open fun getSearchMenuItemId() = R.id.item_menu_search

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        binding = DataBindingUtil.inflate(inflater, getLayoutResId(), null, false)
        viewModel = ViewModelProviders.of(this, getViewModelFactory()).get(getViewModelClass())

        configureMenu()
        configureBinding()
        binding.lifecycleOwner = viewLifecycleOwner
        binding.executePendingBindings()

        return binding.root
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        (activity as AppCompatActivity).supportActionBar?.title = getString(getToolbarTitleResId())
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        super.onCreateOptionsMenu(menu, inflater)
        if (getMenuResId() != EMPTY_RES) {
            inflater.inflate(getMenuResId(), menu)
        }

        val searchItem = menu.findItem(getSearchMenuItemId())

        if (searchItem != null && searchItem.actionView != null) {
            val searchView = menu.findItem(R.id.item_menu_search).actionView as SearchView

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

    @CallSuper
    protected open fun configureBinding() {
        viewModel.navigation.observe(this, Observer {
            it.getContentIfNotHandled()?.let {eventId ->
                onNavigationEvent(eventId)
            }
        })
    }

    private fun configureMenu() {
        setHasOptionsMenu(getMenuResId() != EMPTY_RES)
        (activity as ActivityBase<*, *>).configureMenu(isHomeMenuButtonEnabled(), getHomeMenuButtonIconResId(),
            getHomeMenuButtonListener())
    }
}