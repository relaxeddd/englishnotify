package com.example.vchechin.testapp.common

import android.os.Bundle
import android.view.*
import androidx.annotation.LayoutRes
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.databinding.ViewDataBinding
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelProviders
import com.example.vchechin.testapp.R

abstract class BaseFragment<VM : ViewModel, B : ViewDataBinding> : Fragment() {

    protected lateinit var viewModel: VM
    protected lateinit var binding: B

    @LayoutRes
    abstract fun getLayoutResId() : Int
    abstract fun getToolbarTitleResId(): Int
    abstract fun getViewModelFactory() : ViewModelProvider.NewInstanceFactory
    abstract fun getViewModelClass(): Class<VM>
    protected open fun configureBinding() {}
    protected open fun isHomeMenuButtonEnabled() = false
    protected open fun getHomeMenuButtonIconResId() = R.drawable.ic_menu
    protected open fun getHomeMenuButtonListener() = {}
    protected open fun getMenuResId() = EMPTY_RES

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        binding = DataBindingUtil.inflate(inflater, getLayoutResId(), null, false)
        viewModel = ViewModelProviders.of(this, getViewModelFactory()).get(getViewModelClass())

        configureMenu()
        configureBinding()
        binding.setLifecycleOwner(this)
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
    }

    private fun configureMenu() {
        setHasOptionsMenu(getMenuResId() != EMPTY_RES)
        (activity as ActivityBase<*, *>).configureMenu(isHomeMenuButtonEnabled(), getHomeMenuButtonIconResId(),
            getHomeMenuButtonListener())
    }
}