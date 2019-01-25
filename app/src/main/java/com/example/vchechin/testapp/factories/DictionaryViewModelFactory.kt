package com.example.vchechin.testapp.factories

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.vchechin.testapp.model.repository.RepositoryWord
import com.example.vchechin.testapp.ui.dictionary.ViewModelDictionary

class DictionaryViewModelFactory(private val repositoryWord: RepositoryWord) : ViewModelProvider.NewInstanceFactory() {

    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return ViewModelDictionary(repositoryWord) as T
    }
}