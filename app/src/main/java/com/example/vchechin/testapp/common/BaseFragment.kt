package com.example.vchechin.testapp.common

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.LayoutRes
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.databinding.ViewDataBinding
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelProviders

abstract class BaseFragment<VM : ViewModel, B : ViewDataBinding> : Fragment() {

    protected lateinit var viewModel: VM
    protected lateinit var binding: B

    @LayoutRes
    abstract fun getLayoutResId() : Int
    abstract fun getToolbarTitleResId(): Int
    abstract fun getViewModelFactory() : ViewModelProvider.NewInstanceFactory
    abstract fun getViewModelClass(): Class<VM>
    protected open fun configureBinding() {}

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        binding = DataBindingUtil.inflate(inflater, getLayoutResId(), null, false)
        viewModel = ViewModelProviders.of(this, getViewModelFactory()).get(getViewModelClass())

        configureBinding()
        return binding.root
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        (activity as AppCompatActivity).supportActionBar?.title = getString(getToolbarTitleResId())
    }
}