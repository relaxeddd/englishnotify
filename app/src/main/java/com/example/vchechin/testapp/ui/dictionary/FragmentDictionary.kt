package com.example.vchechin.testapp.ui.dictionary

import androidx.lifecycle.ViewModelProviders
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.example.vchechin.testapp.R
import com.example.vchechin.testapp.common.BaseFragment

class FragmentDictionary : BaseFragment() {

    private lateinit var viewModelDictionary: ViewModelDictionary

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        return inflater.inflate(R.layout.fragment_dictionary, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        viewModelDictionary = ViewModelProviders.of(this).get(ViewModelDictionary::class.java)
        // TODO: Use the ViewModel
    }

    override fun getToolbarTitleResId() = R.string.dictionary
}
