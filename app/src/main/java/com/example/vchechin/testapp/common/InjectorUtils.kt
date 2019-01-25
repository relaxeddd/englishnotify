package com.example.vchechin.testapp.common

import android.content.Context
import com.example.vchechin.testapp.factories.DictionaryViewModelFactory
import com.example.vchechin.testapp.factories.MainViewModelFactory
import com.example.vchechin.testapp.model.repository.RepositoryWord
import com.example.vchechin.testapp.model.db.AppDatabase
import com.example.vchechin.testapp.model.repository.RepositoryUser

object InjectorUtils {

    private fun getUserRepository(context: Context): RepositoryUser {
        return RepositoryUser.getInstance(
            AppDatabase.getInstance(context.applicationContext).userDao())
    }

    private fun getWordRepository(context: Context): RepositoryWord {
        return RepositoryWord.getInstance(
            AppDatabase.getInstance(context.applicationContext).wordDao())
    }

    //------------------------------------------------------------------------------------------------------------------
    fun provideMainViewModelFactory(context: Context): MainViewModelFactory {
        val repository = getUserRepository(context)
        return MainViewModelFactory(repository)
    }

    fun provideDictionaryViewModelFactory(context: Context): DictionaryViewModelFactory {
        val repository = getWordRepository(context)
        return DictionaryViewModelFactory(repository)
    }
}