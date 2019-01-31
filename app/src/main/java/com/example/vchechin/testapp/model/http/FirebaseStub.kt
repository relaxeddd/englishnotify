package com.example.vchechin.testapp.model.http

import com.example.vchechin.testapp.common.Resource
import com.example.vchechin.testapp.common.STATUS_ERROR
import com.example.vchechin.testapp.common.STATUS_OK
import com.example.vchechin.testapp.common.User
import com.example.vchechin.testapp.model.IFirebase
import kotlinx.coroutines.delay
import kotlin.random.Random

object FirebaseStub : IFirebase {

    override suspend fun saveUser(user: User) : Resource<User> {
        delay(2000)
        return Resource(if (Random.nextBoolean()) STATUS_OK else STATUS_ERROR, "Error update", user)
    }
}