package relaxeddd.englishnotify.view_base

import androidx.lifecycle.ViewModel
import relaxeddd.englishnotify.view_base.WrappingViewModel.Factory

class WrappingViewModel<out W>(val wrapped: W) : ViewModel() {

    companion object {

        fun <W> wrappingViewModelFactory(factory: () -> W): Factory<W> = Factory {
            WrappingViewModel(factory())
        }
    }

    fun interface Factory<W> {
        fun create(): WrappingViewModel<W>
    }
}
