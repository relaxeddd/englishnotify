package relaxeddd.englishnotify.model

import relaxeddd.englishnotify.common.Resource
import relaxeddd.englishnotify.common.User

interface IFirebase {

    suspend fun saveUser(user: User) : Resource<User>
}