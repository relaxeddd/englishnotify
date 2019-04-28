package relaxeddd.englishnotify.model.repository

import androidx.lifecycle.MutableLiveData
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

class RepositoryUser private constructor() {

    companion object {
        @Volatile
        private var instance: RepositoryUser? = null
        fun getInstance() = instance ?: synchronized(this) {
            instance ?: RepositoryUser().also { instance = it }
        }
    }

    var liveDataUser = MutableLiveData<User>(null)
    val liveDataIsActualVersion = MutableLiveData<Boolean>(true)

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
                    liveDataUser.value = answerInitData.user
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
        withContext(Dispatchers.Main) {
            liveDataUser.postValue(null)
            showToast(R.string.logout_success)
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

        if (answer?.isSuccess() == true) {
            showToastLong(R.string.test_notification_sent)
            val user = User(liveDataUser.value ?: return)
            user.testCount -= 1
            liveDataUser.postValue(user)
        } else if (answer != null) {
            showToast(getErrorString(answer))
        } else {
            showToastLong(R.string.error_request)
        }
    }

    //------------------------------------------------------------------------------------------------------------------
    private suspend fun updateUser(user: User, oldUser: User?) {
        liveDataUser.postValue(user)

        val firebaseUser = RepositoryCommon.getInstance().firebaseUser
        val tokenId = RepositoryCommon.getInstance().tokenId
        val updateResult = ApiHelper.requestUpdateUser(firebaseUser, tokenId, user)

        if (updateResult != null && updateResult.result !== null && !updateResult.result.isSuccess()) {
            showToast(getErrorString(updateResult.result))
            liveDataUser.postValue(oldUser)
        } else if (updateResult != null && updateResult.result !== null) {
            SharedHelper.setLearnLanguageType(user.learnLanguageType)
        } else {
            showToast(R.string.error_update)
        }
    }
}