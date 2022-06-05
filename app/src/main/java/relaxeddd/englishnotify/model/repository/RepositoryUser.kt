package relaxeddd.englishnotify.model.repository

import androidx.lifecycle.MutableLiveData
import relaxeddd.englishnotify.model.http.ApiHelper
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.messaging.FirebaseMessaging
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext
import relaxeddd.englishnotify.common.*
import relaxeddd.englishnotify.R
import relaxeddd.englishnotify.model.preferences.SharedHelper
import relaxeddd.englishnotify.push.MyFirebaseMessagingService

class RepositoryUser private constructor() {

    companion object {
        @Volatile
        private var instance: RepositoryUser? = null
        fun getInstance() = instance ?: synchronized(this) {
            instance ?: RepositoryUser().also { instance = it }
        }
    }

    var liveDataUser = MutableLiveData<User?>(null)
    var liveDataHideSignIn = MutableLiveData(SharedHelper.isHideSignIn())
    var liveDataIsInitInProgress = MutableLiveData(false)
    val liveDataIsActualVersion = MutableLiveData(true)

    fun isAuthorized() = FirebaseAuth.getInstance().currentUser != null
    fun isInit() = liveDataUser.value != null

    //------------------------------------------------------------------------------------------------------------------
    suspend fun init() : Boolean {
        liveDataIsInitInProgress.postValue(true)
        if (liveDataUser.value != null) {
            liveDataIsInitInProgress.postValue(false)
            return true
        }

        val isFirebaseInitialized = RepositoryCommon.getInstance().initFirebase()

        if (!isFirebaseInitialized) {
            liveDataIsInitInProgress.postValue(false)
            return false
        }

        val firebaseUser = RepositoryCommon.getInstance().firebaseUser
        val tokenId = RepositoryCommon.getInstance().tokenId
        var pushToken = MyFirebaseMessagingService.pushToken

        if (pushToken.isBlank()) {
            pushToken = SharedHelper.getPushToken()
        }
        if (pushToken.isBlank()) {
            pushToken = FirebaseMessaging.getInstance().token.await() ?: ""
        }
        if (pushToken.isBlank()) {
            showToast(R.string.error_push_token)
        }
        if (firebaseUser == null) {
            showToast(getErrorString(RESULT_ERROR_UNAUTHORIZED))
            liveDataIsInitInProgress.postValue(false)
            return false
        }

        val answerInitData = ApiHelper.requestInit(firebaseUser, tokenId, pushToken)

        if (answerInitData?.result != null && answerInitData.result.isSuccess()
            && answerInitData.user?.userId?.isNotEmpty() == true) {
            liveDataUser.value = answerInitData.user
            SharedHelper.setLearnLanguageType(answerInitData.user.learnLanguageType)
            SharedHelper.setSelectedCategory(answerInitData.user.selectedTag)
            if (!answerInitData.isActualVersion) {
                liveDataIsActualVersion.value = answerInitData.isActualVersion
            }

            /*if (answerInitData.words != null) {
                withContext(Dispatchers.IO) { RepositoryWord.getInstance().updateWords(answerInitData.words) }
                RepositoryWord.getInstance().updateTagsInfo(answerInitData.tagsInfo ?: ArrayList())
            }*/

            liveDataIsInitInProgress.postValue(false)
            return true
        } else if (answerInitData?.result != null) {
            showToast(getErrorString(answerInitData.result))
            liveDataIsInitInProgress.postValue(false)
            return false
        } else {
            showToast(R.string.error_initialization)
            liveDataIsInitInProgress.postValue(false)
            return false
        }
    }

    suspend fun deleteUserInfo() {
        withContext(Dispatchers.Main) {
            liveDataUser.value = null
        }
    }

    fun hideSignIn() {
        SharedHelper.setHideSignIn(true)
        liveDataHideSignIn.value = true
    }

    //------------------------------------------------------------------------------------------------------------------
    fun setSavedWordsCount(count: Int) {
        val user = User(liveDataUser.value ?: return)
        user.savedWordsCount = count
        liveDataUser.postValue(user)
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

    suspend fun setSelectedTag(selectedTag: String) : Boolean {
        if (selectedTag.isNotEmpty()) {
            val user = User(liveDataUser.value ?: return false)
            user.selectedTag = selectedTag
            SharedHelper.setSelectedCategory(selectedTag)
            return updateUser(user, liveDataUser.value)
        } else {
            showToast(R.string.tags_should_not_be_empty)
            return false
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

        when {
            answer?.isSuccess() == true -> {
                showToast(R.string.test_notification_sent)
                val user = User(liveDataUser.value ?: return)
                user.testCount -= 1
                liveDataUser.postValue(user)
            }
            answer != null -> showToast(getErrorString(answer))
            else -> showToast(R.string.error_request)
        }
    }

    suspend fun logout() : Boolean {
        if (FirebaseAuth.getInstance().currentUser == null) {
            showToast(getErrorString(RESULT_ERROR_UNAUTHORIZED))
            return false
        }

        val firebaseUser = RepositoryCommon.getInstance().firebaseUser
        val tokenId = RepositoryCommon.getInstance().tokenId
        var pushToken = MyFirebaseMessagingService.pushToken

        if (pushToken.isBlank()) {
            pushToken = SharedHelper.getPushToken()
        }
        if (pushToken.isBlank()) {
            pushToken = FirebaseMessaging.getInstance().token.await() ?: ""
        }
        if (pushToken.isBlank()) {
            showToast(R.string.error_push_token)
        }
        if (firebaseUser == null) {
            showToast(getErrorString(RESULT_ERROR_UNAUTHORIZED))
            return false
        }

        val answerLogout = ApiHelper.requestLogout(firebaseUser, tokenId, pushToken)

        return if (answerLogout?.result != null && answerLogout.result.isSuccess()) {
            SharedHelper.setPushToken("")
            true
        } else if (answerLogout?.result != null) {
            showToast(getErrorString(answerLogout.result))
            false
        } else {
            showToast(getErrorString(RESULT_ERROR_LOGOUT))
            false
        }
    }

    //------------------------------------------------------------------------------------------------------------------
    private suspend fun updateUser(user: User, oldUser: User?) : Boolean {
        liveDataUser.postValue(user)

        val firebaseUser = RepositoryCommon.getInstance().firebaseUser
        val tokenId = RepositoryCommon.getInstance().tokenId
        val updateResult = ApiHelper.requestUpdateUser(firebaseUser, tokenId, user.notificationsTimeType,
            user.receiveNotifications, user.learnLanguageType, user.selectedTag)

        return if (updateResult != null && updateResult.result !== null && !updateResult.result.isSuccess()) {
            withContext(Dispatchers.Main) { showToast(getErrorString(updateResult.result)) }
            liveDataUser.postValue(oldUser)
            false
        } else if (updateResult != null && updateResult.result !== null) {
            SharedHelper.setLearnLanguageType(user.learnLanguageType)
            true
        } else {
            withContext(Dispatchers.Main) { showToast(R.string.error_update) }
            false
        }
    }
}
