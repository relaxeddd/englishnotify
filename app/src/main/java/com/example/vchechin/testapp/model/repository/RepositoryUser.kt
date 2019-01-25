package com.example.vchechin.testapp.model.repository

import com.example.vchechin.testapp.common.USER_ID_TEST
import com.example.vchechin.testapp.model.db.UserDao

class RepositoryUser private constructor(private val userDao: UserDao) {

    companion object {

        @Volatile private var instance: RepositoryUser? = null

        fun getInstance(userDao: UserDao) =
            instance ?: synchronized(this) {
                instance ?: RepositoryUser(userDao).also { instance = it }
            }
    }

    var user = userDao.findById(USER_ID_TEST)
}