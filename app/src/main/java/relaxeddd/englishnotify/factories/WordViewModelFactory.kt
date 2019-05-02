package relaxeddd.englishnotify.factories

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import relaxeddd.englishnotify.ui.word.ViewModelWord

class WordViewModelFactory : ViewModelProvider.NewInstanceFactory() {

    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return ViewModelWord() as T
    }
}