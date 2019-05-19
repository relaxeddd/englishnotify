package relaxeddd.englishnotify.ui.dictionary_know

import relaxeddd.englishnotify.R
import relaxeddd.englishnotify.common.InjectorUtils
import relaxeddd.englishnotify.ui.dictionary.AdapterDictionary
import relaxeddd.englishnotify.ui.dictionary.FragmentDictionaryDefault

class FragmentDictionaryKnow : FragmentDictionaryDefault<ViewModelDictionaryKnow, AdapterDictionary>() {

    override fun getToolbarTitleResId() = R.string.already_know
    override fun getViewModelFactory() = InjectorUtils.provideDictionaryKnowViewModelFactory(requireContext())
    override fun getViewModelClass() = ViewModelDictionaryKnow::class.java
    override fun getMenuResId() = R.menu.menu_fragment_dictionary_know
    override fun getSearchMenuItemId() = R.id.item_menu_search_dictionary_know
    override fun createWordsAdapter() = AdapterDictionary(viewModel)
}