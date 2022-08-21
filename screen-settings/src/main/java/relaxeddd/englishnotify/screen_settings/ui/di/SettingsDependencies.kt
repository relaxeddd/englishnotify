package relaxeddd.englishnotify.screen_settings.ui.di

import android.content.Context
import relaxeddd.englishnotify.domain_words.repository.RepositoryWords
import relaxeddd.englishnotify.preferences.Preferences

interface SettingsDependencies {

    val context: Context
    val prefs: Preferences
    val repositoryWords: RepositoryWords
}
