package relaxeddd.englishnotify.ui.word

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import relaxeddd.englishnotify.R
import relaxeddd.englishnotify.common.*
import relaxeddd.englishnotify.model.repository.RepositoryWord

class ViewModelWord : ViewModelBase() {

    private var isWordCreating: Boolean = false
    var wordId = ""

    fun createOwnWord(eng: String, transcription: String, rus: String) {
        if (isWordCreating) {
            return
        }

        isWordCreating = true
        navigateEvent.value = Event(NAVIGATION_LOADING_SHOW)
        ioScope.launch {
            val wordId = if (wordId.isEmpty()) eng else wordId
            val result = RepositoryWord.getInstance().insertOwnCategoryWord(wordId, eng, rus, transcription)

            withContext(Dispatchers.Main) {
                navigateEvent.value = Event(NAVIGATION_LOADING_HIDE)
                if (result) {
                    if (eng != wordId) {
                        withContext(Dispatchers.IO) {
                            RepositoryWord.getInstance().removeWordFromDb(wordId)
                        }
                    }
                    showToast(android.R.string.ok)
                    navigateEvent.value = Event(NAVIGATION_ACTIVITY_BACK)
                }
                isWordCreating = false
            }
        }
    }
}