package com.example.vchechin.testapp.model

import com.example.vchechin.testapp.common.Resource
import com.example.vchechin.testapp.common.User

interface IFirebase {

    suspend fun saveUser(user: User) : Resource<User>
}