package com.example.vchechin.testapp.ui.dictionary

import android.view.View
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Observer
import com.example.vchechin.testapp.common.Event
import com.example.vchechin.testapp.common.NAVIGATION_DIALOG_CHECK_TAGS
import com.example.vchechin.testapp.common.ViewModelBase
import com.example.vchechin.testapp.common.Word
import com.example.vchechin.testapp.model.repository.RepositoryWord

class ViewModelDictionary(private val repositoryWord: RepositoryWord) : ViewModelBase() {

    private val words: LiveData<List<Word>> = repositoryWord.words
    val filterTags = MutableLiveData<HashSet<String>>()
    val tags = HashSet<String>()
    val wordsFiltered = MutableLiveData<List<Word>>(ArrayList())
    val wordsObserver = object: Observer<List<Word>> {
        override fun onChanged(words: List<Word>?) {
            tags.clear()
            words?.forEach { it.tags.forEach { tag -> tags.add(tag) } }
            if (filterTags.value == null) {
                filterTags.value = tags
            }
            updateFilteredWords()
        }
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

    fun setFilterTags(tags: List<String>) {
        if (!tags.containsAll(ViewModelDictionary@this.tags)) {
            filterTags.value = tags.toHashSet()
        } else {
            filterTags.value = HashSet()
        }
        updateFilteredWords()
    }

    private fun updateFilteredWords() {
        val filteredItems = HashSet<Word>()

        if (filterTags.value?.isNotEmpty() == true) {
            words.value?.forEach { it.tags.forEach { wordTag -> run {
                if (filterTags.value?.contains(wordTag) == true) filteredItems.add(it)
            }}}
        } else {
            filteredItems.addAll(words.value ?: ArrayList())
        }

        wordsFiltered.value = filteredItems.toList()
    }
}
