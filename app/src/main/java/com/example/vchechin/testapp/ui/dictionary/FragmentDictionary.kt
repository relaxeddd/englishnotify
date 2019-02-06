package com.example.vchechin.testapp.ui.dictionary

import android.os.Bundle
import android.view.MenuItem
import android.view.View
import androidx.lifecycle.Observer
import com.example.vchechin.testapp.R
import com.example.vchechin.testapp.common.*
import com.example.vchechin.testapp.databinding.FragmentDictionaryBinding
import com.example.vchechin.testapp.dialogs.DialogCheckTags
import kotlinx.android.synthetic.main.fragment_dictionary.*

class FragmentDictionary : BaseFragment<ViewModelDictionary, FragmentDictionaryBinding>() {

    var adapter: AdapterWords = AdapterWords()

    private val listenerCheckTags: ListenerResult<List<String>> = object: ListenerResult<List<String>> {
        override fun onResult(result: List<String>) {
            viewModel.setFilterTags(result)
            animateDropdown(card_view_dictionary_filter, false)
        }
    }

    override fun getToolbarTitleResId() = R.string.dictionary
    override fun getLayoutResId() = R.layout.fragment_dictionary
    override fun getViewModelFactory() = InjectorUtils.provideDictionaryViewModelFactory(requireContext())
    override fun getViewModelClass() = ViewModelDictionary::class.java
    override fun getMenuResId() = R.menu.menu_filter

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.item_menu_filter -> {
                animateDropdown(card_view_dictionary_filter, card_view_dictionary_filter.visibility == View.GONE)
                return true
            }
            else -> return super.onOptionsItemSelected(item)
        }
    }

    override fun configureBinding() {
        super.configureBinding()
        binding.viewModel = viewModel
        binding.recyclerViewDictionary.adapter = adapter
        binding.clickListenerFilterTags = viewModel.clickListenerFilterTags
        viewModel.wordsFiltered.observe(viewLifecycleOwner, Observer { words ->
            binding.hasWords = (words != null && words.isNotEmpty())
            if (words != null && words.isNotEmpty())
                adapter.submitList(words)
        })
    }

    override fun onNavigationEvent(eventId: Int) {
        when (eventId) {
            NAVIGATION_DIALOG_CHECK_TAGS -> {
                val dialog = DialogCheckTags()
                val args = Bundle()
                args.putStringArray(ITEMS, viewModel.tags.toTypedArray())
                args.putStringArray(CHECKED_ITEMS, viewModel.filterTags.value?.toTypedArray())
                dialog.arguments = args
                dialog.listener = listenerCheckTags
                dialog.show(this@FragmentDictionary.childFragmentManager, "Check tags Dialog")
            }
        }
    }
}