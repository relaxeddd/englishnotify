package relaxeddd.englishnotify.ui.dictionary

import androidx.annotation.CallSuper
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Observer
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch
import relaxeddd.englishnotify.common.NAVIGATION_DIALOG_CHECK_TAGS
import relaxeddd.englishnotify.common.NAVIGATION_DIALOG_SORTED_BY
import relaxeddd.englishnotify.common.NAVIGATION_FRAGMENT_WORD
import relaxeddd.englishnotify.common.NAVIGATION_LOADING_HIDE
import relaxeddd.englishnotify.common.NAVIGATION_LOADING_SHOW
import relaxeddd.englishnotify.common.NAVIGATION_PLAY_WORD
import relaxeddd.englishnotify.domain_words.entity.Word
import relaxeddd.englishnotify.domain_words.repository.RepositoryWords
import relaxeddd.englishnotify.preferences.Preferences
import relaxeddd.englishnotify.preferences.models.SortByType
import relaxeddd.englishnotify.view_base.ViewModelBase
import relaxeddd.englishnotify.view_base.models.Event

abstract class ViewModelDictionary(
    private val prefs: Preferences,
    private val repositoryWords: RepositoryWords,
) : ViewModelBase() {

    open val isShowOwnWordsContainer = true

    val sortByType = MutableLiveData(SortByType.getByName(prefs.getSortByType()))
    val filterTags = MutableLiveData<HashSet<String>>(HashSet())
    val tags = HashSet<String>()
    val wordsFiltered = MutableLiveData<List<Word>>(ArrayList())
    var playWord: Word? = null
    var editWord: Word? = null

    private val words: LiveData<List<Word>> = repositoryWords.words
    private val wordsObserver = Observer<List<Word>> { words ->
        tags.clear()
        words?.forEach { it.tags.forEach { tag -> if (tag.isNotEmpty()) tags.add(tag) } }
        updateFilteredWords()
    }
    private val sortObserver = Observer<SortByType> { sort ->
        prefs.setSortByType(sort.name)
    }

    init {
        repositoryWords.words.observeForever(wordsObserver)
        sortByType.observeForever(sortObserver)
    }

    override fun onCleared() {
        super.onCleared()
        repositoryWords.words.removeObserver(wordsObserver)
    }

    fun onClickedFilterTags() {
        navigateEvent.value = Event(NAVIGATION_DIALOG_CHECK_TAGS)
    }

    fun onClickedSortedBy() {
        navigateEvent.value = Event(NAVIGATION_DIALOG_SORTED_BY)
    }

    fun playWord(word: Word?) {
        playWord = word
        navigateEvent.value = Event(NAVIGATION_PLAY_WORD)
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
        viewModelScope.launch {
            repositoryWords.setWordLearnStage(word, 0, false)
            repositoryWords.setWordLearnStage(word, 0, true)
        }
    }

    fun edit(word: Word) {
        editWord = word
        navigateEvent.value = Event(NAVIGATION_FRAGMENT_WORD)
    }

    fun deleteWord(word: Word) {
        navigateEvent.value = Event(NAVIGATION_LOADING_SHOW)
        viewModelScope.launch {
            repositoryWords.deleteWord(word.id)
            navigateEvent.value = Event(NAVIGATION_LOADING_HIDE)
        }
    }

    fun deleteWords(words: Collection<Word>) {
        navigateEvent.value = Event(NAVIGATION_LOADING_SHOW)
        viewModelScope.launch {
            val listIds = HashSet<String>()

            words.forEach { listIds.add(it.id) }
            if (listIds.isNotEmpty()) {
                repositoryWords.deleteWords(listIds.toList())
            }
            navigateEvent.value = Event(NAVIGATION_LOADING_HIDE)
        }
    }

    //------------------------------------------------------------------------------------------------------------------
    @CallSuper
    protected open fun filterWords(prefs: Preferences, items: HashSet<Word>) : HashSet<Word> {
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
            filteredItems = filteredItems.filter { it.eng.lowercase().contains(searchText)
                    || it.rus.lowercase().contains(searchText)
                    || it.transcription.lowercase().contains(searchText) }.toHashSet()
        }

        filteredItems = filterWords(prefs, filteredItems)

        val sortList = when (sortByType.value) {
            SortByType.ALPHABETICAL_NAME -> filteredItems.sortedBy{ it.eng.lowercase() }
            SortByType.ALPHABETICAL_TRANSLATE -> filteredItems.sortedBy{ it.rus.lowercase() }
            SortByType.TIME_OLD -> filteredItems.sortedBy{ it.timestamp }
            else -> filteredItems.sortedByDescending{ it.timestamp }
        }

        wordsFiltered.value = sortList
    }
}
