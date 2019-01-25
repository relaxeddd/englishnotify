package com.example.vchechin.testapp.common

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment

abstract class BaseFragment : Fragment() {

    abstract fun getToolbarTitleResId() : Int

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        (activity as AppCompatActivity).supportActionBar?.title = getString(getToolbarTitleResId())
    }
}