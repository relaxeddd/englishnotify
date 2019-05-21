package relaxeddd.englishnotify.ui.dictionary_own

import android.os.Bundle
import android.view.MenuItem
import android.view.View
import androidx.lifecycle.Observer
import androidx.navigation.Navigation
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.card.MaterialCardView
import kotlinx.android.synthetic.main.fragment_dictionary_own.*
import relaxeddd.englishnotify.R
import relaxeddd.englishnotify.common.InjectorUtils
import relaxeddd.englishnotify.common.NAVIGATION_DIALOG_OWN_CATEGORY
import relaxeddd.englishnotify.databinding.FragmentDictionaryOwnBinding
import relaxeddd.englishnotify.dialogs.DialogOwnCategory
import relaxeddd.englishnotify.ui.dictionary.AdapterDictionary
import relaxeddd.englishnotify.ui.dictionary.FragmentDictionary

class FragmentDictionaryOwn : FragmentDictionary<ViewModelDictionaryOwn, FragmentDictionaryOwnBinding, AdapterDictionary>() {

    override fun getLayoutResId() = R.layout.fragment_dictionary_own
    override fun getToolbarTitleResId() = R.string.own_category
    override fun getViewModelFactory() = InjectorUtils.provideDictionaryOwnViewModelFactory(requireContext())
    override fun getViewModelClass() = ViewModelDictionaryOwn::class.java
    override fun getMenuResId() = R.menu.menu_fragment_dictionary_own
    override fun getSearchMenuItemId() = R.id.item_menu_search_dictionary_own
    override fun createWordsAdapter() = AdapterDictionary(viewModel)
    override fun getRecyclerViewWords(): RecyclerView = recycler_view_dictionary
    override fun getCardViewFilter() : MaterialCardView = card_view_dictionary_filter

    override fun configureBinding() {
        super.configureBinding()

        binding.viewModel = viewModel
        binding.clickListenerCloseFilter = clickListenerCloseFilter
        binding.clickListenerAddOwnWord = Navigation.createNavigateOnClickListener(R.id.action_fragmentDictionaryOwn_to_fragmentWord)
        viewModel.user.observe(viewLifecycleOwner, Observer { user ->
            adapter.languageType = user?.learnLanguageType ?: 0
        })
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        viewModel.wordsFiltered.observe(viewLifecycleOwner, Observer { words ->
            binding.hasWords = (words != null && words.isNotEmpty())
            updateAdapter(words)
        })
    }

    override fun onOptionsItemSelected(item: MenuItem) = when (item.itemId) {
        R.id.item_menu_dialog_own -> {
            onNavigationEvent(NAVIGATION_DIALOG_OWN_CATEGORY)
            true
        }
        else -> super.onOptionsItemSelected(item)
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