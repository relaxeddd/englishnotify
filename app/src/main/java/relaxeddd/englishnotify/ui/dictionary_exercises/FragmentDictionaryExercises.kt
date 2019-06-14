package relaxeddd.englishnotify.ui.dictionary_exercises

import relaxeddd.englishnotify.R
import relaxeddd.englishnotify.common.InjectorUtils
import relaxeddd.englishnotify.ui.dictionary.AdapterExercises
import relaxeddd.englishnotify.ui.dictionary.FragmentDictionaryDefault

class FragmentDictionaryExercises : FragmentDictionaryDefault<ViewModelDictionaryExercises, AdapterExercises>() {

    override fun getToolbarTitleResId() = R.string.exercises
    override fun getViewModelFactory() = InjectorUtils.provideDictionaryExercisesViewModelFactory(requireContext())
    override fun getViewModelClass() = ViewModelDictionaryExercises::class.java
    override fun getMenuResId() = R.menu.menu_fragment_dictionary_exercises
    override fun getSearchMenuItemId() = R.id.item_menu_search_dictionary_exercises
    override fun createWordsAdapter() = AdapterExercises(viewModel)
}