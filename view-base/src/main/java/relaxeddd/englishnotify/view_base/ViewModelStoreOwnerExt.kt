package relaxeddd.englishnotify.view_base

import androidx.lifecycle.ViewModelStoreOwner
import relaxeddd.englishnotify.view_base.WrappingViewModel.Companion.wrappingViewModelFactory
import relaxeddd.englishnotify.view_base.WrappingViewModelReadOnlyProperty.Companion.wrappedByViewModelReadOnlyProperty

fun <T : Any> ViewModelStoreOwner.propertyViaViewModel(
    viewModelKey: String? = null,
    factory: () -> T
) = wrappedByViewModelReadOnlyProperty(
    viewModelKey,
    wrappingViewModelFactory(factory)
)
