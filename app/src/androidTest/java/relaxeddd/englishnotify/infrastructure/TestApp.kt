package relaxeddd.englishnotify.infrastructure

import relaxeddd.englishnotify.App
import relaxeddd.englishnotify.di.ApplicationComponent
import relaxeddd.englishnotify.infrastructure.di.DaggerTestApplicationComponent

class TestApp : App() {

    override fun createComponent(): ApplicationComponent {
        return DaggerTestApplicationComponent.factory().create(applicationContext)
    }
}
