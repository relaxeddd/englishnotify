package relaxeddd.englishnotify.ui.dictionary_all

import android.view.MenuItem
import relaxeddd.englishnotify.R
import relaxeddd.englishnotify.common.InjectorUtils
import relaxeddd.englishnotify.common.showToastLong
import relaxeddd.englishnotify.model.repository.RepositoryWord
import relaxeddd.englishnotify.ui.dictionary.AdapterDictionary
import relaxeddd.englishnotify.ui.dictionary.FragmentDictionaryDefault

class FragmentDictionaryAll : FragmentDictionaryDefault<ViewModelDictionaryAll, AdapterDictionary>() {

    override fun getToolbarTitleResId() = R.string.all_words
    override fun getViewModelFactory() = InjectorUtils.provideDictionaryAllViewModelFactory(requireContext())
    override fun getViewModelClass() = ViewModelDictionaryAll::class.java
    override fun getMenuResId() = R.menu.menu_fragment_dictionary
    override fun getSearchMenuItemId() = R.id.item_menu_search_dictionary
    override fun createWordsAdapter() = AdapterDictionary(viewModel)

    override fun onOptionsItemSelected(item: MenuItem) = when (item.itemId) {
        R.id.item_menu_training -> {
            if (RepositoryWord.getInstance().isTrainingWordsExists()) {
                navigate(R.id.action_fragmentDictionaryAll_to_fragmentTrainingSetting)
            } else {
                showToastLong(R.string.no_training_words)
            }
            true
        }
        else -> super.onOptionsItemSelected(item)
    }
}