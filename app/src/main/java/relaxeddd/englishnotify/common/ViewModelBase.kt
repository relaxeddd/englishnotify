package relaxeddd.englishnotify.common

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

abstract class ViewModelBase : ViewModel() {

    protected val navigateEvent = MutableLiveData<Event<Int>>()

    val navigation : LiveData<Event<Int>>
        get() = navigateEvent

    open fun onFragmentResume() {}
}
