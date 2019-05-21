package relaxeddd.englishnotify.ui.word

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import relaxeddd.englishnotify.R
import relaxeddd.englishnotify.common.*
import relaxeddd.englishnotify.model.repository.RepositoryUser

class ViewModelWord : ViewModelBase() {

    private var isWordCreating: Boolean = false

    fun createOwnWord(eng: String, transcription: String, rus: String) {
        if (isWordCreating) {
            return
        }

        isWordCreating = true
        navigateEvent.value = Event(NAVIGATION_LOADING_SHOW)
        ioScope.launch {
            val word = Word(eng, rus, transcription, timestamp = System.currentTimeMillis())
            val result = RepositoryUser.getInstance().insertOwnWord(word)

            withContext(Dispatchers.Main) {
                navigateEvent.value = Event(NAVIGATION_LOADING_HIDE)
                if (result) {
                    showToast(R.string.word_added)
                    navigateEvent.value = Event(NAVIGATION_ACTIVITY_BACK)
                }
                isWordCreating = false
            }
        }
    }
}