package relaxeddd.englishnotify.ui.dictionary

import android.view.View
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Observer
import kotlinx.coroutines.launch
import relaxeddd.englishnotify.common.*
import relaxeddd.englishnotify.model.repository.RepositoryUser
import relaxeddd.englishnotify.model.repository.RepositoryWord

class ViewModelDictionary(private val repositoryWord: RepositoryWord, private val repositoryUser: RepositoryUser) : ViewModelBase() {

    val user: LiveData<User?> = repositoryUser.liveDataUser
    val sortByType = MutableLiveData<SortByType>(SortByType.getByName(SharedHelper.getSortByType()))
    val filterTags = MutableLiveData<HashSet<String>>()
    val tags = HashSet<String>()
    val wordsFiltered = MutableLiveData<List<Word>>(ArrayList())
    private val words: LiveData<List<Word>> = repositoryWord.words
    private val wordsObserver = Observer<List<Word>> { words ->
        tags.clear()
        words?.forEach { it.tags.forEach { tag -> tags.add(tag) } }
        if (filterTags.value == null) {
            filterTags.value = tags
        }
        updateFilteredWords()
    }
    private val sortObserver = Observer<SortByType> { sort ->
        SharedHelper.setSortByType(sort.name)
    }

    init {
        repositoryWord.words.observeForever(wordsObserver)
        sortByType.observeForever(sortObserver)
    }

    override fun onCleared() {
        super.onCleared()
        repositoryWord.words.removeObserver(wordsObserver)
    }

    val clickListenerFilterTags = View.OnClickListener {
        navigateEvent.value = Event(NAVIGATION_DIALOG_CHECK_TAGS)
    }
    val clickListenerSortBy = View.OnClickListener {
        navigateEvent.value = Event(NAVIGATION_DIALOG_SORT_BY)
    }

    fun setFilterTags(tags: List<String>) {
        if (!tags.containsAll(this.tags)) {
            filterTags.value = tags.toHashSet()
        } else {
            filterTags.value = HashSet()
        }
        updateFilteredWords()
    }

    fun onDialogSortByType(type: Int) {
        val newSortByType = if (type >= 0 && type < SortByType.values().size) SortByType.values()[type] else SortByType.TIME_NEW
        sortByType.value = newSortByType
        updateFilteredWords()
    }

    fun applySearch(searchText: String) {
        updateFilteredWords(searchText)
    }

    fun deleteWord(word: Word) {
        ioScope.launch {
            repositoryWord.deleteWord(word)
        }
    }

    fun deleteWords(words: Collection<Word>) {
        ioScope.launch {
            words.forEach { repositoryWord.deleteWord(it) }
        }
    }

    private fun updateFilteredWords(searchText: String = "") {
        var filteredItems = HashSet<Word>()
        filteredItems.addAll(words.value ?: ArrayList())

        if (filterTags.value?.isNotEmpty() == true) {
            filteredItems = filteredItems.filter {
                it.tags.intersect(filterTags.value ?: HashSet()).isNotEmpty()
            }.toHashSet()
        }

        if (searchText.isNotEmpty()) {
            filteredItems = filteredItems.filter { it.eng.toLowerCase().contains(searchText)
                    || it.rus.toLowerCase().contains(searchText)
                    || it.transcription.toLowerCase().contains(searchText) }.toHashSet()
        }

        filteredItems = filteredItems.filter { !it.isDeleted }.toHashSet()

        val sortList = when (sortByType.value) {
            SortByType.ALPHABETICAL_NAME -> filteredItems.sortedBy{ it.eng.toLowerCase() }
            SortByType.ALPHABETICAL_TRANSLATE -> filteredItems.sortedBy{ it.rus.toLowerCase() }
            SortByType.TIME_OLD -> filteredItems.sortedBy{ it.timestamp }
            else -> filteredItems.sortedByDescending{ it.timestamp }
        }

        wordsFiltered.value = sortList
    }
}
