package com.example.vchechin.testapp.ui.dictionary

import androidx.lifecycle.LiveData
import com.example.vchechin.testapp.common.ViewModelBase
import com.example.vchechin.testapp.common.Word
import com.example.vchechin.testapp.model.repository.RepositoryWord

class ViewModelDictionary(private val repositoryWord: RepositoryWord) : ViewModelBase() {
    val words: LiveData<List<Word>> = repositoryWord.words
}
