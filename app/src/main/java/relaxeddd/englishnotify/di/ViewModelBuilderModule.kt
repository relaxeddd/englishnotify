package relaxeddd.englishnotify.di

import androidx.lifecycle.ViewModelProvider
import dagger.Binds
import dagger.Module
import relaxeddd.englishnotify.ui.AppViewModelFactory

@Module
abstract class ViewModelBuilderModule {

    @Binds
    internal abstract fun bindViewModelFactory(factory: AppViewModelFactory): ViewModelProvider.Factory
}
