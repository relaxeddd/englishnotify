package relaxeddd.englishnotify.ui.word

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import relaxeddd.englishnotify.common.*
import relaxeddd.englishnotify.model.repository.RepositoryWord

class ViewModelWord : ViewModelBase() {

    var wordId = ""

    fun createOwnWord(eng: String, transcription: String, rus: String) {
        ioScope.launch {
            if (wordId.isEmpty()) {
                RepositoryWord.getInstance().insertOwnCategoryWord(eng, eng, rus, transcription)
            } else {
                RepositoryWord.getInstance().insertOwnCategoryWord(eng, eng, rus, transcription)
                if (eng != this@ViewModelWord.wordId) {
                    RepositoryWord.getInstance().removeWordFromDb(this@ViewModelWord.wordId)
                }
            }

            withContext(Dispatchers.Main) {
                navigateEvent.value = Event(NAVIGATION_ACTIVITY_BACK)
            }
        }
    }
}