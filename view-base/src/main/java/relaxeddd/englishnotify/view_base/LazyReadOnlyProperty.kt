package relaxeddd.englishnotify.view_base

import kotlin.properties.ReadOnlyProperty
import kotlin.reflect.KProperty

abstract class LazyReadOnlyProperty<in T, out V> : ReadOnlyProperty<T, V> {

    private var value: V? = null

    protected abstract fun createValue(thisRef: T, property: KProperty<*>) : V

    override fun getValue(thisRef: T, property: KProperty<*>): V {
        return value ?: createValue(thisRef, property).also { value = it }
    }
}
