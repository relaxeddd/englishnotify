package com.example.vchechin.testapp.ui.dictionary

import androidx.lifecycle.ViewModelProviders
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.Observer
import com.example.vchechin.testapp.R
import com.example.vchechin.testapp.common.BaseFragment
import com.example.vchechin.testapp.common.InjectorUtils
import com.example.vchechin.testapp.databinding.FragmentDictionaryBinding

class FragmentDictionary : BaseFragment() {

    private lateinit var viewModelDictionary: ViewModelDictionary

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        val binding = FragmentDictionaryBinding.inflate(inflater, container, false)
        val adapter = AdapterWords()
        binding.recyclerViewDictionary.adapter = adapter

        subscribeUi(binding, adapter)
        return binding.root
    }

    private fun subscribeUi(binding: FragmentDictionaryBinding, adapter: AdapterWords) {
        val factory = InjectorUtils.provideDictionaryViewModelFactory(requireContext())
        viewModelDictionary = ViewModelProviders.of(this, factory).get(ViewModelDictionary::class.java)

        viewModelDictionary.words.observe(viewLifecycleOwner, Observer { words ->
            binding.hasWords = (words != null && words.isNotEmpty())
            if (words != null && words.isNotEmpty())
                adapter.submitList(words)
        })
    }

    override fun getToolbarTitleResId() = R.string.dictionary
}
