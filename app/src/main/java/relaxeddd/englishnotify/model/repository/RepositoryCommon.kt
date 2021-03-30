package relaxeddd.englishnotify.model.repository

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import relaxeddd.englishnotify.common.*
import relaxeddd.englishnotify.model.http.ApiHelper
import relaxeddd.englishnotify.push.MyFirebaseMessagingService

class RepositoryCommon private constructor() {

    companion object {
        @Volatile private var instance: RepositoryCommon? = null
        fun getInstance() = instance ?: synchronized(this) {
            instance ?: RepositoryCommon().also { instance = it }
        }
    }

    var firebaseUser: FirebaseUser? = null
    var tokenId: String? = null

    suspend fun initFirebase() : Boolean {
        if (FirebaseAuth.getInstance().currentUser == null) {
            showToast(getErrorString(RESULT_ERROR_UNAUTHORIZED))
            return false
        }

        firebaseUser = FirebaseAuth.getInstance().currentUser

        val tokenResult = ApiHelper.initUserTokenId(firebaseUser)
        val isTokenInitialized = tokenResult.isSuccess() && tokenResult.value != null

        if (isTokenInitialized) {
            val pushTokenResult = ApiHelper.initPushTokenId()

            if (pushTokenResult.isSuccess() && pushTokenResult.value != null) {
                MyFirebaseMessagingService.pushToken = pushTokenResult.value
            }
            tokenId = tokenResult.value
        }

        if (!isTokenInitialized) {
            showToast(getErrorString(RESULT_ERROR_UNAUTHORIZED))
        }

        return isTokenInitialized
    }

    suspend fun requestTranslation(translationText: String, translateFromLanguage: String, translateToLanguage: String) : String {
        if (FirebaseAuth.getInstance().currentUser == null) {
            showToast(getErrorString(RESULT_ERROR_UNAUTHORIZED))
            return ""
        }
        if (translationText.isEmpty() || translateFromLanguage.isEmpty() || translateToLanguage.isEmpty()) {
            showToast(getErrorString(RESULT_ERROR_TRANSLATION))
            return ""
        }
        if (translateFromLanguage == translateToLanguage) {
            return translationText
        }

        val answer = ApiHelper.requestTranslation(firebaseUser, tokenId, translationText, translateFromLanguage, translateToLanguage)

        if (answer?.result?.isSuccess() == true) {
            return if (answer.translations is List<*>) answer.translations[0] as? String ?: "" else ""
        } else {
            return ""
        }
    }
}
