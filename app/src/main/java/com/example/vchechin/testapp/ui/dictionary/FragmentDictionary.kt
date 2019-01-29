package com.example.vchechin.testapp.ui.dictionary

import androidx.lifecycle.Observer
import com.example.vchechin.testapp.R
import com.example.vchechin.testapp.common.BaseFragment
import com.example.vchechin.testapp.common.InjectorUtils
import com.example.vchechin.testapp.databinding.FragmentDictionaryBinding

class FragmentDictionary : BaseFragment<ViewModelDictionary, FragmentDictionaryBinding>() {

    override fun getToolbarTitleResId() = R.string.dictionary
    override fun getLayoutResId() = R.layout.fragment_dictionary
    override fun getViewModelFactory() = InjectorUtils.provideDictionaryViewModelFactory(requireContext())
    override fun getViewModelClass() = ViewModelDictionary::class.java

    override fun configureBinding() {
        super.configureBinding()
        val adapter = AdapterWords()
        binding.recyclerViewDictionary.adapter = adapter
        viewModel.words.observe(viewLifecycleOwner, Observer { words ->
            binding.hasWords = (words != null && words.isNotEmpty())
            if (words != null && words.isNotEmpty())
                adapter.submitList(words)
        })
    }
}
