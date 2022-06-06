package relaxeddd.englishnotify.model.repository

import androidx.lifecycle.MutableLiveData
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import relaxeddd.englishnotify.R
import relaxeddd.englishnotify.common.RESULT_ERROR_LOGOUT
import relaxeddd.englishnotify.common.RESULT_ERROR_UNAUTHORIZED
import relaxeddd.englishnotify.common.User
import relaxeddd.englishnotify.common.getErrorString
import relaxeddd.englishnotify.common.showToast
import relaxeddd.englishnotify.model.http.ApiHelper
import relaxeddd.englishnotify.model.preferences.SharedHelper

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

        if (firebaseUser == null) {
            showToast(getErrorString(RESULT_ERROR_UNAUTHORIZED))
            liveDataIsInitInProgress.postValue(false)
            return false
        }

        val answerInitData = ApiHelper.requestInit(firebaseUser, tokenId)

        if (answerInitData?.result != null && answerInitData.result.isSuccess()
            && answerInitData.user?.userId?.isNotEmpty() == true) {
            liveDataUser.value = answerInitData.user
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

    suspend fun logout() : Boolean {
        if (FirebaseAuth.getInstance().currentUser == null) {
            showToast(getErrorString(RESULT_ERROR_UNAUTHORIZED))
            return false
        }

        val firebaseUser = RepositoryCommon.getInstance().firebaseUser
        val tokenId = RepositoryCommon.getInstance().tokenId

        if (firebaseUser == null) {
            showToast(getErrorString(RESULT_ERROR_UNAUTHORIZED))
            return false
        }

        val answerLogout = ApiHelper.requestLogout(firebaseUser, tokenId)

        return if (answerLogout?.result != null && answerLogout.result.isSuccess()) {
            true
        } else if (answerLogout?.result != null) {
            showToast(getErrorString(answerLogout.result))
            false
        } else {
            showToast(getErrorString(RESULT_ERROR_LOGOUT))
            false
        }
    }
}
