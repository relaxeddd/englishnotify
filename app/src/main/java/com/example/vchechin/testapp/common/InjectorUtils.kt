package com.example.vchechin.testapp.common

import android.content.Context
import androidx.lifecycle.ViewModelProvider
import com.example.vchechin.testapp.factories.DictionaryViewModelFactory
import com.example.vchechin.testapp.factories.MainViewModelFactory
import com.example.vchechin.testapp.factories.NotificationsViewModelFactory
import com.example.vchechin.testapp.factories.SettingsViewModelFactory
import com.example.vchechin.testapp.model.repository.RepositoryWord
import com.example.vchechin.testapp.model.db.AppDatabase
import com.example.vchechin.testapp.model.repository.RepositoryUser
import com.example.vchechin.testapp.ui.main.MainActivity

object InjectorUtils {

    fun provideMainViewModelFactory(context: Context): MainViewModelFactory {
        val repository = RepositoryFactory.getUserRepository(context)
        return MainViewModelFactory(repository)
    }

    fun provideDictionaryViewModelFactory(context: Context): DictionaryViewModelFactory {
        val repository = RepositoryFactory.getWordRepository(context)
        return DictionaryViewModelFactory(repository)
    }

    fun provideNotificationsViewModelFactory(context: Context): NotificationsViewModelFactory {
        return NotificationsViewModelFactory()
    }

    fun provideSettingsViewModelFactory(context: Context): SettingsViewModelFactory {
        return SettingsViewModelFactory()
    }
}

object RepositoryFactory {

    internal fun getUserRepository(context: Context): RepositoryUser {
        return RepositoryUser.getInstance(
            AppDatabase.getInstance(context.applicationContext).userDao())
    }

    internal fun getWordRepository(context: Context): RepositoryWord {
        return RepositoryWord.getInstance(
            AppDatabase.getInstance(context.applicationContext).wordDao())
    }
}