package relaxeddd.englishnotify.common

interface ListenerResult<T> {

    fun onResult(result: T)
}