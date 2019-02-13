package relaxeddd.pushenglish.model.repository

import relaxeddd.pushenglish.common.ERROR_NOT_AUTHORIZED
import relaxeddd.pushenglish.common.USER_ID_TEST
import relaxeddd.pushenglish.common.User
import relaxeddd.pushenglish.common.showToast
import relaxeddd.pushenglish.model.db.UserDao
import relaxeddd.pushenglish.model.http.ApiHelper
import relaxeddd.pushenglish.model.http.FirebaseStub
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.iid.FirebaseInstanceId
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class RepositoryUser private constructor(val userDao: UserDao) {

    companion object {

        @Volatile private var instance: RepositoryUser? = null

        fun getInstance(userDao: UserDao) =
            instance ?: synchronized(this) {
                instance
                    ?: RepositoryUser(userDao).also { instance = it }
            }
    }

    var liveDataUser = userDao.findById(USER_ID_TEST)
    var firebaseUser: FirebaseUser? = null
    var tokenId: String? = null

    fun isAuthorized() = FirebaseAuth.getInstance().currentUser != null

    suspend fun initUser() {
        if (FirebaseAuth.getInstance().currentUser == null) {
            showToast(ERROR_NOT_AUTHORIZED)
            return
        }

        firebaseUser = FirebaseAuth.getInstance().currentUser

        ApiHelper.initUserTokenId(firebaseUser) {
            if (it.isSuccess() && it.value != null) {
                tokenId = it.value
                CoroutineScope(Dispatchers.IO).launch {
                    val pushToken = FirebaseInstanceId.getInstance().token ?: ""
                    val answer = ApiHelper.requestInit(firebaseUser, tokenId, pushToken)

                    if (answer.isSuccess() && answer.value != null) {
                        userDao.insert(answer.value.user)
                    } else {
                        showToast(answer.errorStr)
                    }
                }
            } else {
                showToast(ERROR_NOT_AUTHORIZED)
            }
        }
    }

    suspend fun setReceiveNotifications(isReceive: Boolean) {
        val user = User(liveDataUser.value ?: return)
        user.receiveNotifications = isReceive
        updateUser(user, liveDataUser.value)
    }

    suspend fun setLearnLanguageType(timeType: Int) {
        val user = User(liveDataUser.value ?: return)
        user.learnLanguageType = timeType
        updateUser(user, liveDataUser.value)
    }

    suspend fun setNotificationsTimeType(timeType: Int) {
        val user = User(liveDataUser.value ?: return)
        user.notificationsTimeType = timeType
        updateUser(user, liveDataUser.value)
    }

    suspend fun setCheckedTags(checkedTags: List<String>) {
        val user = User(liveDataUser.value ?: return)
        user.tagsSelected = checkedTags
        updateUser(user, liveDataUser.value)
    }

    private suspend fun updateUser(user: User, oldUser: User?) {
        userDao.insert(user)
        val result = FirebaseStub.saveUser(user)

        if (!result.isSuccess()) {
            showToast(result.errorStr)
            userDao.insert(oldUser ?: return)
        }
    }
}