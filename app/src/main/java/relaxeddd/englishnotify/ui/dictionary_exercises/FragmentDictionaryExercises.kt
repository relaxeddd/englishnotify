package relaxeddd.englishnotify.ui.dictionary_exercises

import relaxeddd.englishnotify.common.InjectorUtils
import relaxeddd.englishnotify.ui.dictionary.AdapterExercises
import relaxeddd.englishnotify.ui.dictionary.FragmentDictionary

class FragmentDictionaryExercises : FragmentDictionary<ViewModelDictionaryExercises, AdapterExercises>() {

    override fun getViewModelFactory() = InjectorUtils.provideDictionaryExercisesViewModelFactory(requireContext())
    override fun getViewModelClass() = ViewModelDictionaryExercises::class.java
    override fun createWordsAdapter() = AdapterExercises(viewModel)
}
