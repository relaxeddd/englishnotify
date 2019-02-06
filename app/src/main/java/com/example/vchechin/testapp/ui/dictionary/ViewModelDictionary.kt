package com.example.vchechin.testapp.ui.dictionary

import android.view.View
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Observer
import com.example.vchechin.testapp.common.*
import com.example.vchechin.testapp.model.repository.RepositoryWord

class ViewModelDictionary(private val repositoryWord: RepositoryWord) : ViewModelBase() {

    var sortByType = MutableLiveData<SortByType>(SortByType.ALPHABETICAL_NAME)
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

    init {
        repositoryWord.words.observeForever(wordsObserver)
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
        if (!tags.containsAll(ViewModelDictionary@this.tags)) {
            filterTags.value = tags.toHashSet()
        } else {
            filterTags.value = HashSet()
        }
        updateFilteredWords()
    }

    fun onDialogSortByType(type: Int) {
        val newSortByType = if (type > 0 && type < SortByType.values().size) SortByType.values()[type] else SortByType.TIME_NEW
        sortByType.value = newSortByType
        updateFilteredWords()
    }

    private fun updateFilteredWords() {
        var filteredItems = HashSet<Word>()
        filteredItems.addAll(words.value ?: ArrayList())

        if (filterTags.value?.isNotEmpty() == true) {
            filteredItems.filter { it.tags.intersect(filterTags.value ?: HashSet()).isNotEmpty() }
            /*words.value?.forEach { it.tags.forEach { wordTag -> run {
                if (filterTags.value?.contains(wordTag) == true) filteredItems.add(it)
            }}}*/
        }

        val sortList = when (sortByType) {
            SortByType.ALPHABETICAL_NAME -> filteredItems.sortedWith(compareBy { it.eng })
            SortByType.ALPHABETICAL_TRANSLATE -> filteredItems.sortedWith(compareBy { it.rus })
            SortByType.TIME_OLD -> filteredItems.sortedWith(compareBy { it.timestamp })
            else ->filteredItems.sortedWith(compareByDescending { it.timestamp })
        }

        wordsFiltered.value = sortList
    }
}
