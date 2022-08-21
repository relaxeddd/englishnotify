package relaxeddd.englishnotify.common_di

import dagger.Lazy
import java.lang.ref.WeakReference

class ComponentLazyReference<T: Any>(
    internal val componentClass: Class<T>,
    private val provideComponent: () -> T,
) : Lazy<T> {

    private var componentWeakReference = WeakReference<T?>(null)

    override fun get(): T {
        var component = componentWeakReference.get()
        if (component == null) {
            component = provideComponent()
            componentWeakReference = WeakReference(component)
        }
        return component
    }

    fun clearReference() = componentWeakReference.clear()
}
