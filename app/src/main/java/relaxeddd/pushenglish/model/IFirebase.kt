package relaxeddd.pushenglish.model

import relaxeddd.pushenglish.common.Resource
import relaxeddd.pushenglish.common.User

interface IFirebase {

    suspend fun saveUser(user: User) : Resource<User>
}