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

    fun initFirebase(initCallback: (isSuccess: Boolean) -> Unit) {
        if (FirebaseAuth.getInstance().currentUser == null) {
            showToast(getErrorString(RESULT_ERROR_UNAUTHORIZED))
            initCallback(false)
            return
        }

        firebaseUser = FirebaseAuth.getInstance().currentUser

        ApiHelper.initUserTokenId(firebaseUser) {
            if (it.isSuccess() && it.value != null) {
                ApiHelper.initPushTokenId { pushTokenAnswer ->
                    if (pushTokenAnswer.isSuccess() && pushTokenAnswer.value != null) {
                        MyFirebaseMessagingService.pushToken = pushTokenAnswer.value
                    }
                    tokenId = it.value
                    initCallback(true)
                }
            } else {
                showToast(getErrorString(RESULT_ERROR_UNAUTHORIZED))
                initCallback(false)
            }
        }
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
