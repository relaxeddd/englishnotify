package relaxeddd.englishnotify.ui.dictionary_own

import android.view.MenuItem
import androidx.lifecycle.Observer
import androidx.navigation.Navigation
import relaxeddd.englishnotify.R
import relaxeddd.englishnotify.common.InjectorUtils
import relaxeddd.englishnotify.common.NAVIGATION_DIALOG_OWN_CATEGORY
import relaxeddd.englishnotify.databinding.FragmentDictionaryOwnBinding
import relaxeddd.englishnotify.dialogs.DialogOwnCategory
import relaxeddd.englishnotify.ui.dictionary.AdapterWords
import relaxeddd.englishnotify.ui.dictionary.FragmentDictionary

class FragmentDictionaryOwn : FragmentDictionary<ViewModelDictionaryOwn, FragmentDictionaryOwnBinding>() {

    override fun getLayoutResId() = R.layout.fragment_dictionary_own
    override fun getViewModelFactory() = InjectorUtils.provideDictionaryOwnViewModelFactory(requireContext())
    override fun getViewModelClass() = ViewModelDictionaryOwn::class.java
    override fun getMenuResId() = R.menu.menu_fragment_dictionary_own

    override fun configureBinding() {
        super.configureBinding()
        adapter = AdapterWords(viewModel)
        binding.viewModel = viewModel
        binding.recyclerViewDictionary.adapter = adapter
        binding.clickListenerCloseFilter = clickListenerCloseFilter
        binding.clickListenerAddOwnWord = Navigation.createNavigateOnClickListener(R.id.fragmentWord, null)
        viewModel.wordsFiltered.observe(viewLifecycleOwner, Observer { words ->
            binding.hasWords = (words != null && words.isNotEmpty())
            if (words != null && words.isNotEmpty()) adapter.submitList(words)
            adapter.notifyDataSetChanged()
        })
        viewModel.user.observe(viewLifecycleOwner, Observer { user ->
            adapter.languageType = user?.learnLanguageType ?: 0
        })
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.item_menu_dialog_own -> {
                onNavigationEvent(NAVIGATION_DIALOG_OWN_CATEGORY)
                return true
            }
            else -> return super.onOptionsItemSelected(item)
        }
    }

    override fun onNavigationEvent(eventId: Int) {
        when (eventId) {
            NAVIGATION_DIALOG_OWN_CATEGORY -> {
                DialogOwnCategory().show(this@FragmentDictionaryOwn.childFragmentManager, "Check tags Dialog")
            }
            else -> super.onNavigationEvent(eventId)
        }
    }

    override fun onFragmentSelected() {
        super.onFragmentSelected()
        viewModel.requestOwnWords()
    }
}