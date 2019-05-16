package relaxeddd.englishnotify.ui.dictionary_all

import android.view.MenuItem
import androidx.lifecycle.Observer
import relaxeddd.englishnotify.R
import relaxeddd.englishnotify.common.InjectorUtils
import relaxeddd.englishnotify.common.showToastLong
import relaxeddd.englishnotify.databinding.FragmentDictionaryAllBinding
import relaxeddd.englishnotify.model.repository.RepositoryWord
import relaxeddd.englishnotify.ui.dictionary.AdapterDictionary
import relaxeddd.englishnotify.ui.dictionary.FragmentDictionary

class FragmentDictionaryAll : FragmentDictionary<ViewModelDictionaryAll, FragmentDictionaryAllBinding, AdapterDictionary>() {

    override fun getLayoutResId() = R.layout.fragment_dictionary_all
    override fun getToolbarTitleResId() = R.string.all_words
    override fun getViewModelFactory() = InjectorUtils.provideDictionaryAllViewModelFactory(requireContext())
    override fun getViewModelClass() = ViewModelDictionaryAll::class.java
    override fun getSearchMenuItemId() = R.id.item_menu_search_all

    override fun configureBinding() {
        super.configureBinding()
        adapter = AdapterDictionary(viewModel)
        binding.viewModel = viewModel
        binding.recyclerViewDictionary.adapter = adapter
        binding.clickListenerCloseFilter = clickListenerCloseFilter
        viewModel.wordsFiltered.observe(viewLifecycleOwner, Observer { words ->
            binding.hasWords = (words != null && words.isNotEmpty())
            if (words != null && words.isNotEmpty()) adapter.submitList(words)
        })
        viewModel.user.observe(viewLifecycleOwner, Observer { user ->
            adapter.languageType = user?.learnLanguageType ?: 0
        })
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.item_menu_training -> {
                if (RepositoryWord.getInstance().isTrainingWordsExists()) {
                    navigate(R.id.action_fragmentDictionaryAll_to_fragmentTrainingSetting)
                } else {
                    showToastLong(R.string.no_training_words)
                }
                return true
            }
            else -> return super.onOptionsItemSelected(item)
        }
    }
}