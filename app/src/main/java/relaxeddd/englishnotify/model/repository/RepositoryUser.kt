package relaxeddd.englishnotify.model.repository

import androidx.lifecycle.MutableLiveData
import relaxeddd.englishnotify.model.http.ApiHelper
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.iid.FirebaseInstanceId
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.json.JSONArray
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
    val liveDataIsActualVersion = MutableLiveData(true)
    var rating: List<RatingItem> = ArrayList()
        private set

    fun isAuthorized() = FirebaseAuth.getInstance().currentUser != null

    //------------------------------------------------------------------------------------------------------------------
    suspend fun init(listener: ListenerResult<Boolean>? = null) {
        RepositoryCommon.getInstance().initFirebase { isSuccess ->
            CoroutineScope(Dispatchers.Main).launch {
                if (!isSuccess) {
                    listener?.onResult(false)
                    return@launch
                }

                val firebaseUser = RepositoryCommon.getInstance().firebaseUser
                val tokenId = RepositoryCommon.getInstance().tokenId
                var pushToken = MyFirebaseMessagingService.pushToken

                if (pushToken.isEmpty()) {
                    pushToken = SharedHelper.getPushToken()
                }
                if (pushToken.isEmpty()) {
                    @Suppress("DEPRECATION")
                    pushToken = FirebaseInstanceId.getInstance().token ?: ""
                }
                if (pushToken.isEmpty()) {
                    showToast(R.string.error_push_token)
                }
                if (firebaseUser == null) {
                    showToast(getErrorString(RESULT_ERROR_UNAUTHORIZED))
                    listener?.onResult(false)
                    return@launch
                }

                val learnStage0 = JSONArray()
                for (learnStage in SharedHelper.getLearnStage0()) {
                    if (learnStage.isNotEmpty()) {
                        learnStage0.put(learnStage)
                    }
                }
                val learnStage1 = JSONArray()
                for (learnStage in SharedHelper.getLearnStage1()) {
                    if (learnStage.isNotEmpty()) {
                        learnStage1.put(learnStage)
                    }
                }
                val learnStage2 = JSONArray()
                for (learnStage in SharedHelper.getLearnStage2()) {
                    if (learnStage.isNotEmpty()) {
                        learnStage2.put(learnStage)
                    }
                }
                val learnStage3 = JSONArray()
                for (learnStage in SharedHelper.getLearnStage3()) {
                    if (learnStage.isNotEmpty()) {
                        learnStage3.put(learnStage)
                    }
                }

                val answerInitData = ApiHelper.requestInit(firebaseUser, tokenId, pushToken, learnStage0, learnStage1,
                    learnStage2, learnStage3)

                if (answerInitData?.result != null && answerInitData.result.isSuccess()
                    && answerInitData.user?.userId?.isNotEmpty() == true) {
                    liveDataUser.value = answerInitData.user
                    SharedHelper.setLearnLanguageType(answerInitData.user.learnLanguageType)
                    if (!answerInitData.isActualVersion) {
                        liveDataIsActualVersion.value = answerInitData.isActualVersion
                    }

                    if (answerInitData.rating != null) rating = answerInitData.rating
                    if (answerInitData.words != null) {
                        withContext(Dispatchers.IO) {
                            RepositoryWord.getInstance().updateWords(answerInitData.words)
                        }
                        RepositoryWord.getInstance().updateTagsInfo(answerInitData.tagsInfo ?: ArrayList())
                        SharedHelper.setLearnStage0(HashSet())
                        SharedHelper.setLearnStage1(HashSet())
                        SharedHelper.setLearnStage2(HashSet())
                        SharedHelper.setLearnStage3(HashSet())
                    }

                    listener?.onResult(true)
                } else if (answerInitData?.result != null) {
                    showToast(getErrorString(answerInitData.result))
                    listener?.onResult(false)
                } else {
                    showToast(R.string.error_initialization)
                    listener?.onResult(false)
                }
            }
        }
    }

    suspend fun deleteUserInfo() {
        withContext(Dispatchers.Main) {
            liveDataUser.value = null
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

    suspend fun setNotificationsTimeType(timeType: Int) : Boolean {
        val user = User(liveDataUser.value ?: return false)
        user.notificationsTimeType = timeType
        return updateUser(user, liveDataUser.value)
    }

    suspend fun setSelectedTag(selectedTag: String) : Boolean {
        if (selectedTag.isNotEmpty()) {
            val user = User(liveDataUser.value ?: return false)
            user.selectedTag = selectedTag
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

    suspend fun setNickname(name: String) {
        if (FirebaseAuth.getInstance().currentUser == null) {
            showToast(getErrorString(RESULT_ERROR_UNAUTHORIZED))
            return
        }

        val firebaseUser = RepositoryCommon.getInstance().firebaseUser
        val tokenId = RepositoryCommon.getInstance().tokenId

        val answer = ApiHelper.requestSetNickname(firebaseUser, tokenId, name)

        if (answer != null && answer.isSuccess()) {
            liveDataUser.value?.name = name
            showToast(android.R.string.ok)
        }else if (answer != null) {
            showToast(getErrorString(answer))
        } else {
            showToast(R.string.error_request)
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
            showToast(getErrorString(updateResult.result))
            liveDataUser.postValue(oldUser)
            false
        } else if (updateResult != null && updateResult.result !== null) {
            SharedHelper.setLearnLanguageType(user.learnLanguageType)
            true
        } else {
            showToast(R.string.error_update)
            false
        }
    }
}