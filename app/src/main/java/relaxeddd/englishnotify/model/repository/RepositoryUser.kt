package relaxeddd.englishnotify.model.repository

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Observer
import relaxeddd.englishnotify.model.db.UserDao
import relaxeddd.englishnotify.model.http.ApiHelper
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.iid.FirebaseInstanceId
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import relaxeddd.englishnotify.common.*
import relaxeddd.englishnotify.R
import relaxeddd.englishnotify.push.MyFirebaseMessagingService

class RepositoryUser private constructor(val userDao: UserDao) {

    companion object {
        @Volatile
        private var instance: RepositoryUser? = null
        fun getInstance(userDao: UserDao) = instance ?: synchronized(this) {
            instance ?: RepositoryUser(userDao).also { instance = it }
        }
    }

    private val userObserver = Observer<User?> { user ->
        liveDataUser.postValue(user)
    }

    var userId: String = USER_ID_TEST
        set(value) {
            field = value
            liveDataUserRoom.removeObserver(userObserver)
            liveDataUserRoom = userDao.findById(value)
            subscribeLiveDataUser()
        }
    private var liveDataUserRoom = userDao.findById(userId)
    var liveDataUser = MutableLiveData<User>(liveDataUserRoom.value)
    val liveDataIsActualVersion = MutableLiveData<Boolean>(true)

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
                var pushToken = MyFirebaseMessagingService.pushToken

                if (pushToken.isEmpty()) {
                    pushToken = SharedHelper.getPushToken()
                }
                if (pushToken.isEmpty()) {
                    pushToken = FirebaseInstanceId.getInstance().token ?: ""
                }
                if (pushToken.isEmpty()) {
                    showToast(R.string.error_push_token)
                    return@launch
                }

                val answerInitData = ApiHelper.requestInit(firebaseUser, tokenId, pushToken)

                if (answerInitData?.result != null && answerInitData.result.isSuccess() && answerInitData.user?.userId?.isNotEmpty() == true) {
                    userDao.insert(answerInitData.user)
                    userId = answerInitData.user.userId
                    SharedHelper.setLearnLanguageType(answerInitData.user.learnLanguageType)

                    if (!answerInitData.isActualVersion) {
                        liveDataIsActualVersion.value = answerInitData.isActualVersion
                    }
                } else if (answerInitData?.result != null) {
                    showToast(getErrorString(answerInitData.result))
                } else {
                    showToast(R.string.error_initialization)
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

    suspend fun setSelectedTag(selectedTag: String) {
        if (selectedTag.isNotEmpty()) {
            val user = User(liveDataUser.value ?: return)
            user.selectedTag = selectedTag
            updateUser(user, liveDataUser.value)
        } else {
            showToast(R.string.tags_should_not_be_empty)
        }
    }

    suspend fun sendTestNotification() {
        if (FirebaseAuth.getInstance().currentUser == null) {
            showToast(getErrorString(RESULT_ERROR_UNAUTHORIZED))
            return
        }

        val firebaseUser = RepositoryCommon.getInstance().firebaseUser
        val tokenId = RepositoryCommon.getInstance().tokenId

        val answer = ApiHelper.requestSendTestNotification(firebaseUser, tokenId)

        if (answer?.result?.isSuccess() == true) {
            showToast(R.string.test_notification_sent)
            userDao.insert(answer.user ?: return)
        } else if (answer?.result != null) {
            showToast(getErrorString(answer.result))
        } else {
            showToast(R.string.error_request)
        }
    }

    //------------------------------------------------------------------------------------------------------------------
    private fun subscribeLiveDataUser() {
        liveDataUserRoom.observeForever(userObserver)
    }

    private suspend fun updateUser(user: User, oldUser: User?) {
        userDao.insert(user)

        val firebaseUser = RepositoryCommon.getInstance().firebaseUser
        val tokenId = RepositoryCommon.getInstance().tokenId
        val updateResult = ApiHelper.requestUpdateUser(firebaseUser, tokenId, user)

        if (updateResult != null && updateResult.result !== null && !updateResult.result.isSuccess()) {
            showToast(getErrorString(updateResult.result))
            userDao.insert(oldUser ?: return)
        } else if (updateResult != null && updateResult.result !== null) {
            SharedHelper.setLearnLanguageType(user.learnLanguageType)
        } else {
            showToast(R.string.error_update)
        }
    }
}