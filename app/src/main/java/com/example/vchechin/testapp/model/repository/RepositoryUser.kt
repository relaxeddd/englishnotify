package com.example.vchechin.testapp.model.repository

import com.example.vchechin.testapp.common.USER_ID_TEST
import com.example.vchechin.testapp.common.User
import com.example.vchechin.testapp.common.showToast
import com.example.vchechin.testapp.model.db.UserDao
import com.example.vchechin.testapp.model.http.FirebaseStub

class RepositoryUser private constructor(val userDao: UserDao) {

    companion object {

        @Volatile private var instance: RepositoryUser? = null

        fun getInstance(userDao: UserDao) =
            instance ?: synchronized(this) {
                instance ?: RepositoryUser(userDao).also { instance = it }
            }
    }

    var liveDataUser = userDao.findById(USER_ID_TEST)

    suspend fun setReceiveNotifications(isReceive: Boolean) {
        val user = User(liveDataUser.value?: return)
        user.receiveNotifications = isReceive
        updateUser(user)
    }

    private suspend fun updateUser(user: User) {
        val result = FirebaseStub.saveUser(user)

        if (result.isSuccess()) {
            userDao.insert(user)
        } else {
            showToast(result.errorStr)
            userDao.insert(liveDataUser.value?: return)
        }
    }
}