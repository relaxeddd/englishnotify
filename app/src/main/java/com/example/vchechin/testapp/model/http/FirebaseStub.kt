package com.example.vchechin.testapp.model.http

import com.example.vchechin.testapp.R
import com.example.vchechin.testapp.common.*
import com.example.vchechin.testapp.model.IFirebase
import kotlinx.coroutines.delay
import kotlin.random.Random

object FirebaseStub : IFirebase {

    override suspend fun saveUser(user: User) : Resource<User> {
        if (!isNetworkAvailable()) {
            return Resource(STATUS_ERROR_NETWORK, getString(R.string.network_not_available), user)
        }
        delay(2000)
        return Resource(if (Random.nextBoolean()) STATUS_OK else STATUS_ERROR, getString(R.string.error_update), user)
    }
}