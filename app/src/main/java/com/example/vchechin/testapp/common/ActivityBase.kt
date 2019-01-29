package com.example.vchechin.testapp.common

import android.os.Bundle
import androidx.annotation.LayoutRes
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.databinding.ViewDataBinding
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelProviders
import com.example.vchechin.testapp.R

abstract class ActivityBase<VM : ViewModel, B : ViewDataBinding> : AppCompatActivity(), LifecycleOwner {

    protected lateinit var binding: B
    protected lateinit var viewModel: VM

    @LayoutRes
    abstract fun getLayoutResId() : Int
    abstract fun getViewModelFactory() : ViewModelProvider.NewInstanceFactory
    abstract fun getViewModelClass(): Class<VM>
    protected open fun configureBinding() {}

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val factory = getViewModelFactory()

        binding = DataBindingUtil.setContentView(this, getLayoutResId())
        viewModel = ViewModelProviders.of(this, factory).get(getViewModelClass())

        configureBinding()

        binding.setLifecycleOwner(this)
        binding.executePendingBindings()
    }
}