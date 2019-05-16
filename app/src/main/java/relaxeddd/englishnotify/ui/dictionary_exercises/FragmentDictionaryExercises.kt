package relaxeddd.englishnotify.ui.dictionary_exercises

import android.view.MenuItem
import androidx.lifecycle.Observer
import relaxeddd.englishnotify.R
import relaxeddd.englishnotify.common.InjectorUtils
import relaxeddd.englishnotify.common.showToastLong
import relaxeddd.englishnotify.databinding.FragmentDictionaryExercisesBinding
import relaxeddd.englishnotify.model.repository.RepositoryWord
import relaxeddd.englishnotify.ui.dictionary.AdapterExercises
import relaxeddd.englishnotify.ui.dictionary.FragmentDictionary

class FragmentDictionaryExercises : FragmentDictionary<ViewModelDictionaryExercises, FragmentDictionaryExercisesBinding, AdapterExercises>() {

    override fun getLayoutResId() = R.layout.fragment_dictionary_exercises
    override fun getToolbarTitleResId() = R.string.exercises
    override fun getViewModelFactory() = InjectorUtils.provideDictionaryExercisesViewModelFactory(requireContext())
    override fun getViewModelClass() = ViewModelDictionaryExercises::class.java
    override fun getSearchMenuItemId() = R.id.item_menu_search_all

    override fun configureBinding() {
        super.configureBinding()
        adapter = AdapterExercises(viewModel)
        binding.viewModel = viewModel
        binding.recyclerViewDictionary.adapter = adapter
        binding.clickListenerCloseFilter = clickListenerCloseFilter
        viewModel.wordsFiltered.observe(viewLifecycleOwner, Observer { words ->
            binding.hasWords = (words != null && words.isNotEmpty())
            if (words != null && words.isNotEmpty()) adapter.submitList(words)
        })
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.item_menu_training -> {
                if (RepositoryWord.getInstance().isTrainingWordsExists()) {
                    navigate(R.id.action_fragmentDictionaryExercises_to_fragmentTrainingSetting)
                } else {
                    showToastLong(R.string.no_training_words)
                }
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }
}