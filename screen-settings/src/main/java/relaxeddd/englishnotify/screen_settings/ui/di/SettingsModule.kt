package relaxeddd.englishnotify.screen_settings.ui.di

import relaxeddd.englishnotify.screen_settings.ui.ViewModelSettings

internal interface SettingsModule {

    val viewModel: ViewModelSettings
}

internal fun SettingsModule(deps: SettingsDependencies) = object : SettingsModule {

    override val viewModel = ViewModelSettings(deps.context, deps.prefs, deps.repositoryWords)
}
