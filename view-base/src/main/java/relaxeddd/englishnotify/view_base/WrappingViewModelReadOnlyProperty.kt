package relaxeddd.englishnotify.view_base

import androidx.lifecycle.ViewModelStoreOwner
import kotlin.properties.ReadOnlyProperty
import kotlin.reflect.KProperty

class WrappingViewModelReadOnlyProperty<out V>(
    private val viewModelStoreOwnerProvider: () -> ViewModelStoreOwner,
    private val sharedViewModelKey: String?,
    private val factory: WrappingViewModel.Factory<V>
) : LazyReadOnlyProperty<Any, V>() {

    companion object {

        fun <W : Any> ViewModelStoreOwner.wrappedByViewModelReadOnlyProperty(
            viewModelKey: String?,
            factory: WrappingViewModel.Factory<W>,
        ): ReadOnlyProperty<ViewModelStoreOwner, W> {
            return WrappingViewModelReadOnlyProperty({ this }, viewModelKey, factory)
        }
    }

    override fun createValue(thisRef: Any, property: KProperty<*>): V {
        val storeOwner = viewModelStoreOwnerProvider()
        val key = sharedViewModelKey ?: keyFromProperty(storeOwner, property)
        val vm = storeOwner.viewModelStore.get(key, factory::create)
        return vm.wrapped
    }

    private fun keyFromProperty(thisRef: ViewModelStoreOwner, property: KProperty<*>): String {
        return thisRef::class.java.canonicalName!! + "#" + property.name
    }
}
