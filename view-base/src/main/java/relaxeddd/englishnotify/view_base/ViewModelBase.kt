package relaxeddd.englishnotify.view_base

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import relaxeddd.englishnotify.view_base.models.Event

abstract class ViewModelBase : ViewModel() {

    protected val navigateEvent = MutableLiveData<Event<Int>>()

    val navigation : LiveData<Event<Int>>
        get() = navigateEvent

    open fun onFragmentResume() {}
}
