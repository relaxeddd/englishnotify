package relaxeddd.englishnotify.ui.parsed_words

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch
import relaxeddd.englishnotify.common.NAVIGATION_ACTIVITY_BACK_TWICE
import relaxeddd.englishnotify.domain_words.entity.Word
import relaxeddd.englishnotify.domain_words.repository.RepositoryWords
import relaxeddd.englishnotify.view_base.ViewModelBase
import relaxeddd.englishnotify.view_base.models.Event
import javax.inject.Inject

class ViewModelParsedWords @Inject constructor(private val repositoryWords: RepositoryWords) : ViewModelBase() {

    val parsedWords = MutableLiveData<ArrayList<Word>>(ArrayList(repositoryWords.tempParsedWords))

    fun onClickedAccept() {
        viewModelScope.launch {
            repositoryWords.insertWords(parsedWords.value ?: ArrayList())
            repositoryWords.tempParsedWords.clear()
            navigateEvent.value = Event(NAVIGATION_ACTIVITY_BACK_TWICE)
        }
    }

    fun deleteWord(word: Word) {
        val words = ArrayList(parsedWords.value ?: ArrayList())
        words.remove(word)
        parsedWords.value = words
    }
}
