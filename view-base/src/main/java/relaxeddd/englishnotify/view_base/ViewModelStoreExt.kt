package relaxeddd.englishnotify.view_base

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelStore

inline fun <reified VM : ViewModel> ViewModelStore.get(crossinline factory: () -> VM): VM {
    return get(key = null, factory)
}

inline fun <reified VM : ViewModel> ViewModelStore.get(key: String?, crossinline factory: () -> VM): VM {
    val viewModelProviderFactory = object : ViewModelProvider.Factory {
        @Suppress("UNCHECKED_CAST")
        override fun <VM : ViewModel> create(modelClass: Class<VM>) = factory() as VM
    }
    val viewModelProvider = ViewModelProvider(this, viewModelProviderFactory)
    return if (key == null) viewModelProvider.get(VM::class.java) else viewModelProvider.get(key, VM::class.java)
}
