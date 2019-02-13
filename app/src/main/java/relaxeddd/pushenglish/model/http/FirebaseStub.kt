package relaxeddd.pushenglish.model.http

import relaxeddd.pushenglish.R
import relaxeddd.pushenglish.model.IFirebase
import kotlinx.coroutines.delay
import relaxeddd.pushenglish.common.*
import kotlin.random.Random

object FirebaseStub : IFirebase {

    override suspend fun saveUser(user: User) : Resource<User> {
        if (!isNetworkAvailable()) {
            return Resource(
                STATUS_ERROR_NETWORK,
                getString(R.string.network_not_available),
                user
            )
        }
        delay(2000)
        return Resource(
            if (Random.nextBoolean()) STATUS_OK else STATUS_ERROR,
            getString(R.string.error_update),
            user
        )
    }
}