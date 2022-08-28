package relaxeddd.englishnotify.screen_settings.ui.di

import relaxeddd.englishnotify.preferences.Preferences
import relaxeddd.englishnotify.screen_settings.ui.ViewModelSettings

abstract class SettingsComponent {

    internal abstract val prefs: Preferences
    internal abstract val viewModel: ViewModelSettings
}

fun SettingsComponent(deps: SettingsDependencies): SettingsComponent {
    return object : SettingsComponent(),
        SettingsModule by SettingsModule(deps),
        SettingsDependencies by deps {}
}
