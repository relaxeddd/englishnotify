package relaxeddd.englishnotify.common

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers

abstract class ViewModelBase : ViewModel() {

    protected val navigateEvent = MutableLiveData<Event<Int>>()
    protected val uiScope = CoroutineScope(Dispatchers.Main)
    protected val ioScope = CoroutineScope(Dispatchers.IO)

    val navigation : LiveData<Event<Int>>
        get() = navigateEvent

    open fun onFragmentResume() {}
}
