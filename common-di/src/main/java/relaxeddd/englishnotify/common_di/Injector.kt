package relaxeddd.englishnotify.common_di

import android.app.Activity
import android.app.Application
import android.view.View
import androidx.annotation.MainThread
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelStoreOwner
import java.util.*

@MainThread
object Injector {

    private val strongReferenceHolder: MutableMap<Any, Any> = WeakHashMap()
    private val lazyReferenceStore: MutableMap<Class<*>, ComponentLazyReference<*>> = HashMap()

    fun init(application: Application, componentLazyReferenceList: List<ComponentLazyReference<*>>) {
        for (componentLazyReference in componentLazyReferenceList) {
            lazyReferenceStore[componentLazyReference.componentClass] = componentLazyReference
        }
        application.registerActivityLifecycleCallbacks(OnDestroyCleaner(this))
    }

    // For tests and sample
    fun addComponent(componentLazyReference: ComponentLazyReference<*>) {
        lazyReferenceStore[componentLazyReference.componentClass] = componentLazyReference
    }

    fun <T : Any> getComponent(activity: Activity, componentClass: Class<T>): T = this[activity, componentClass]

    fun <T : Any> getComponent(fragment: Fragment, componentClass: Class<T>): T = this[fragment, componentClass]

    fun <T : Any> getComponent(view: View, componentClass: Class<T>): T = this[view, componentClass]

    fun <T : Any> getComponentForViewModelStoreOwner(
        viewModelStoreOwner: ViewModelStoreOwner,
        componentClass: Class<T>,
    ): T {
        return this[viewModelStoreOwner, componentClass]
    }

    fun release(componentClass: Class<*>) {
        componentClass.removeDependantComponents()
    }

    fun releaseAll() {
        strongReferenceHolder.clear()
    }

    @MainThread
    internal fun remove(activity: Activity) {
        strongReferenceHolder.remove(activity)
    }

    @MainThread
    internal fun remove(fragment: Fragment) {
        strongReferenceHolder.remove(fragment)
    }

    private operator fun <T : Any> get(activityOrFragmentOrView: Any, componentClass: Class<T>): T {
        val component = getLazyComponent(componentClass)
        strongReferenceHolder[activityOrFragmentOrView] = component
        return component
    }

    @MainThread
    private fun <T : Any> getLazyComponent(componentClass: Class<T>): T {
        val componentLazyReference = checkNotNull(lazyReferenceStore[componentClass]) {
            "You should provide ComponentLazyReference for ${componentClass.name}"
        }
        @Suppress("UNCHECKED_CAST")
        return componentLazyReference.get() as T
    }

    @MainThread
    private fun Class<*>.removeDependantComponents() {
        methods.forEach { it.returnType.removeComponent() }
        removeComponent()
    }

    private fun Class<*>.removeComponent() {
        if (strongReferenceHolder.isNotEmpty()) {
            val components = strongReferenceHolder.values
            val componentsToRemove = ArrayList<Any>()
            components
                .filter { isAssignableFrom(it.javaClass) }
                .forEach { componentsToRemove.add(it) }
            components.removeAll(componentsToRemove)
        }

        removeFromLazyReferenceStoreIfExists()

        interfaces.forEach { it.removeFromLazyReferenceStoreIfExists() }
    }

    private fun Class<*>.removeFromLazyReferenceStoreIfExists() {
        lazyReferenceStore[this]?.clearReference()
    }
}
