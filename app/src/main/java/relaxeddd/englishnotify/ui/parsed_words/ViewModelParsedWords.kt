package relaxeddd.englishnotify.ui.parsed_words

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch
import relaxeddd.englishnotify.App
import relaxeddd.englishnotify.common.Event
import relaxeddd.englishnotify.common.NAVIGATION_ACTIVITY_BACK_TWICE
import relaxeddd.englishnotify.common.ViewModelBase
import relaxeddd.englishnotify.domain_words.entity.Word
import relaxeddd.englishnotify.domain_words.repository.RepositoryWords

class ViewModelParsedWords : ViewModelBase() {

    val parsedWords = MutableLiveData<ArrayList<Word>>(ArrayList(RepositoryWords.getInstance(App.context).tempParsedWords))

    fun onClickedAccept() {
        viewModelScope.launch {
            RepositoryWords.getInstance(App.context).insertWords(parsedWords.value ?: ArrayList())
            RepositoryWords.getInstance(App.context).tempParsedWords.clear()
            navigateEvent.value = Event(NAVIGATION_ACTIVITY_BACK_TWICE)
        }
    }

    fun deleteWord(word: Word) {
        val words = ArrayList(parsedWords.value ?: ArrayList())
        words.remove(word)
        parsedWords.value = words
    }
}
