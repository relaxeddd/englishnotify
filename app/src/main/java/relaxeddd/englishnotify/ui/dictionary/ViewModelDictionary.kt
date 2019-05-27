package relaxeddd.englishnotify.ui.dictionary

import android.view.View
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Observer
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import relaxeddd.englishnotify.common.*
import relaxeddd.englishnotify.model.repository.RepositoryUser
import relaxeddd.englishnotify.model.repository.RepositoryWord

open class ViewModelDictionary(protected val repositoryWord: RepositoryWord, protected val repositoryUser: RepositoryUser) : ViewModelBase() {

    val user: LiveData<User?> = repositoryUser.liveDataUser
    val sortByType = MutableLiveData<SortByType>(SortByType.getByName(SharedHelper.getSortByType()))
    val filterTags = MutableLiveData<HashSet<String>>(HashSet())
    val tags = HashSet<String>()
    val wordsFiltered = MutableLiveData<List<Word>>(ArrayList())
    private val words: LiveData<List<Word>> = repositoryWord.words
    private val wordsObserver = Observer<List<Word>> { words ->
        tags.clear()
        words?.forEach { it.tags.forEach { tag -> if (tag.isNotEmpty()) tags.add(tag) } }
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

    fun resetProgress(word: Word) {
        repositoryWord.setWordProgress(word, 0)
    }

    fun addToOwn(word: Word) {
        if (word.isOwnCategory) return
        navigateEvent.value = Event(NAVIGATION_LOADING_SHOW)
        ioScope.launch {
            repositoryUser.insertOwnWord(word)
            withContext(Dispatchers.Main) {
                navigateEvent.value = Event(NAVIGATION_LOADING_HIDE)
            }
        }
    }

    fun removeFromOwnDict(word: Word) {
        navigateEvent.value = Event(NAVIGATION_LOADING_SHOW)
        ioScope.launch {
            repositoryUser.deleteOwnWord(word.eng)
            withContext(Dispatchers.Main) {
                navigateEvent.value = Event(NAVIGATION_LOADING_HIDE)
            }
        }
    }

    fun deleteWord(word: Word) {
        if (word.isOwnCategory) {
            navigateEvent.value = Event(NAVIGATION_LOADING_SHOW)
        }
        ioScope.launch {
            val deleteResult = if (word.isOwnCategory) repositoryUser.deleteOwnWord(word.eng) else true

            if (deleteResult) {
                repositoryWord.deleteWord(word)
            }
            withContext(Dispatchers.Main) {
                navigateEvent.value = Event(NAVIGATION_LOADING_HIDE)
            }
        }
    }

    fun deleteWords(words: Collection<Word>) {
        for (word in words) {
            if (word.isOwnCategory) {
                navigateEvent.value = Event(NAVIGATION_LOADING_SHOW)
                break
            }
        }
        ioScope.launch {
            val listIds = HashSet<String>()

            words.forEach { if (it.isOwnCategory) listIds.add(it.eng) }
            val deleteResult = if (listIds.isNotEmpty()) repositoryUser.deleteOwnWords(listIds.toList()) else true

            if (deleteResult) {
                words.forEach { repositoryWord.deleteWord(it) }
            }
            withContext(Dispatchers.Main) {
                navigateEvent.value = Event(NAVIGATION_LOADING_HIDE)
            }
        }
    }

    //------------------------------------------------------------------------------------------------------------------
    protected open fun filterWords(items: HashSet<Word>) : HashSet<Word> {
        return items.filter { !it.isDeleted }.toHashSet()
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

        filteredItems = filterWords(filteredItems)

        val sortList = when (sortByType.value) {
            SortByType.ALPHABETICAL_NAME -> filteredItems.sortedBy{ it.eng.toLowerCase() }
            SortByType.ALPHABETICAL_TRANSLATE -> filteredItems.sortedBy{ it.rus.toLowerCase() }
            SortByType.TIME_OLD -> filteredItems.sortedBy{ it.timestamp }
            else -> filteredItems.sortedByDescending{ it.timestamp }
        }

        wordsFiltered.value = sortList
    }
}
