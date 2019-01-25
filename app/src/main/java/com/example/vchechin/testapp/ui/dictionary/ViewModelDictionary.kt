package com.example.vchechin.testapp.ui.dictionary

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import com.example.vchechin.testapp.common.Word
import com.example.vchechin.testapp.model.repository.RepositoryWord

class ViewModelDictionary(private val repositoryWord: RepositoryWord) : ViewModel() {
    val words: LiveData<List<Word>> = repositoryWord.words
}
