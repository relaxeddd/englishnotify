package relaxeddd.pushenglish.model.repository

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Observer
import relaxeddd.pushenglish.common.USER_ID_TEST
import relaxeddd.pushenglish.common.User
import relaxeddd.pushenglish.common.showToast
import relaxeddd.pushenglish.model.db.UserDao
import relaxeddd.pushenglish.model.http.ApiHelper
import relaxeddd.pushenglish.model.http.FirebaseStub
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.iid.FirebaseInstanceId
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import relaxeddd.pushenglish.R

class RepositoryUser private constructor(val userDao: UserDao) {

    companion object {
        @Volatile private var instance: RepositoryUser? = null
        fun getInstance(userDao: UserDao) = instance ?: synchronized(this) {
            instance ?: RepositoryUser(userDao).also { instance = it }
        }
    }

    private val userObserver = Observer<User?> { user ->
        liveDataUser.postValue(user)
    }

    /*var liveDataUser = userDao.findById("oXkta2ZmBbPG84DONJaoyDyNzj23")
    var firebaseUser: FirebaseUser? = null
    var tokenId: String? = null
    var userId: String = "oXkta2ZmBbPG84DONJaoyDyNzj23"*/

    var userId: String = USER_ID_TEST
        set(value) {
            field = value
            liveDataUserRoom.removeObserver(userObserver)
            liveDataUserRoom = userDao.findById(value)
            subscribeLiveDataUser()
        }
    private var liveDataUserRoom = userDao.findById(userId)
    var liveDataUser = MutableLiveData<User>(liveDataUserRoom.value)

    init {
        subscribeLiveDataUser()
    }

    fun isAuthorized() = FirebaseAuth.getInstance().currentUser != null

    //------------------------------------------------------------------------------------------------------------------
    suspend fun initUser() {
        RepositoryCommon.getInstance().initFirebase { isSuccess ->
            if (!isSuccess) {
                return@initFirebase
            }

            CoroutineScope(Dispatchers.Main).launch {
                val firebaseUser = RepositoryCommon.getInstance().firebaseUser
                val tokenId = RepositoryCommon.getInstance().tokenId
                val pushToken = FirebaseInstanceId.getInstance().token ?: ""
                val answer = ApiHelper.requestInit(firebaseUser, tokenId, pushToken)

                if (answer.isSuccess() && answer.value != null && answer.value.user.id.isNotEmpty()) {
                    userDao.insert(answer.value.user)
                    userId = answer.value.user.id
                } else {
                    showToast(answer.errorStr)
                }
            }
        }
    }

    suspend fun deleteUserInfo() {
        val user = liveDataUserRoom.value
        if (user != null) {
            userDao.delete(user)
            withContext(Dispatchers.Main) {
                userId = ""
                showToast(R.string.logout_success)
            }
        }
    }

    //------------------------------------------------------------------------------------------------------------------
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

    //------------------------------------------------------------------------------------------------------------------
    private fun subscribeLiveDataUser() {
        liveDataUserRoom.observeForever(userObserver)
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