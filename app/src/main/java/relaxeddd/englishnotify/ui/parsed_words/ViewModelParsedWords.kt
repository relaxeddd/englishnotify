package relaxeddd.englishnotify.ui.parsed_words

import androidx.lifecycle.MutableLiveData
import kotlinx.coroutines.launch
import relaxeddd.englishnotify.common.*
import relaxeddd.englishnotify.model.repository.RepositoryWord

class ViewModelParsedWords : ViewModelBase() {

    val parsedWords = MutableLiveData<ArrayList<Word>>(ArrayList(RepositoryWord.getInstance().tempParsedWords))

    fun onClickedAccept() {
        ioScope.launch {
            RepositoryWord.getInstance().insertWords(parsedWords.value ?: ArrayList())
            uiScope.launch {
                RepositoryWord.getInstance().tempParsedWords.clear()
                navigateEvent.value = Event(NAVIGATION_ACTIVITY_BACK_TWICE)
            }
        }
    }

    fun deleteWord(word: Word) {
        val words = ArrayList(parsedWords.value ?: ArrayList())
        words.remove(word)
        parsedWords.value = words
    }
}
