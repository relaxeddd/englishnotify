package relaxeddd.englishnotify.ui.word

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import relaxeddd.englishnotify.common.*
import relaxeddd.englishnotify.model.repository.RepositoryWord

class ViewModelWord : ViewModelBase() {

    var existsWordId = ""

    fun createOwnWord(eng: String, transcription: String, rus: String) {
        ioScope.launch {
            val existsWord = RepositoryWord.getInstance().getWord(eng)

            if (existsWordId.isNotEmpty()) {
                if (existsWord == null || existsWord.eng != eng || existsWord.rus != rus || existsWord.transcription != transcription) {
                    RepositoryWord.getInstance().insertOwnCategoryWord(eng, eng, rus, transcription)
                    if (eng != this@ViewModelWord.existsWordId) {
                        RepositoryWord.getInstance().removeWordFromDb(this@ViewModelWord.existsWordId)
                    }
                }
            } else {
                if (existsWord != null) {
                    withContext(Dispatchers.Main) {
                        navigateEvent.value = Event(NAVIGATION_WORD_EXISTS_ERROR)
                    }
                    return@launch
                } else {
                    RepositoryWord.getInstance().insertOwnCategoryWord(eng, eng, rus, transcription)
                }
            }

            withContext(Dispatchers.Main) {
                navigateEvent.value = Event(NAVIGATION_ACTIVITY_BACK)
            }
        }
    }
}