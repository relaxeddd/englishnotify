package com.example.vchechin.testapp.model.repository

import androidx.databinding.ObservableField
import com.example.vchechin.testapp.model.db.WordDao

class RepositoryWord private constructor(private val wordDao: WordDao) {

    companion object {

        @Volatile private var instance: RepositoryWord? = null

        fun getInstance(wordDao: WordDao) =
            instance ?: synchronized(this) {
                instance ?: RepositoryWord(wordDao).also { instance = it }
            }
    }

    var words = wordDao.getAll()
}