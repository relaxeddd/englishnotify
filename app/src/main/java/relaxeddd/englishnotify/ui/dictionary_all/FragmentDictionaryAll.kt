package relaxeddd.englishnotify.ui.dictionary_all

import relaxeddd.englishnotify.R
import relaxeddd.englishnotify.common.InjectorUtils
import relaxeddd.englishnotify.ui.dictionary.AdapterDictionary
import relaxeddd.englishnotify.ui.dictionary.FragmentDictionaryDefault

class FragmentDictionaryAll : FragmentDictionaryDefault<ViewModelDictionaryAll, AdapterDictionary>() {

    override fun getToolbarTitleResId() = R.string.all_words
    override fun getViewModelFactory() = InjectorUtils.provideDictionaryAllViewModelFactory(requireContext())
    override fun getViewModelClass() = ViewModelDictionaryAll::class.java
    override fun getMenuResId() = R.menu.menu_fragment_dictionary
    override fun getSearchMenuItemId() = R.id.item_menu_search_dictionary
    override fun createWordsAdapter() = AdapterDictionary(viewModel)
}