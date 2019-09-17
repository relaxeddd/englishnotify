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
            val wordId = if (wordId.isEmpty()) eng else wordId

            RepositoryWord.getInstance().insertOwnCategoryWord(wordId, eng, rus, transcription)
            if (eng != wordId) {
                RepositoryWord.getInstance().removeWordFromDb(wordId)
            }

            withContext(Dispatchers.Main) {
                navigateEvent.value = Event(NAVIGATION_ACTIVITY_BACK)
            }
        }
    }
}