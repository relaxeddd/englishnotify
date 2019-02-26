package relaxeddd.pushenglish.model.repository

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import relaxeddd.pushenglish.R
import relaxeddd.pushenglish.common.*
import relaxeddd.pushenglish.model.http.ApiHelper

class RepositoryCommon private constructor() {

    companion object {
        @Volatile private var instance: RepositoryCommon? = null
        fun getInstance() = instance ?: synchronized(this) {
            instance ?: RepositoryCommon().also { instance = it }
        }
    }

    var firebaseUser: FirebaseUser? = null
    var tokenId: String? = null

    suspend fun initFirebase(initCallback: (isSuccess: Boolean) -> Unit) {
        if (FirebaseAuth.getInstance().currentUser == null) {
            showToast(getErrorString(RESULT_ERROR_UNAUTHORIZED))
            initCallback(false)
            return
        }

        firebaseUser = FirebaseAuth.getInstance().currentUser

        ApiHelper.initUserTokenId(firebaseUser) {
            if (it.isSuccess() && it.value != null) {
                tokenId = it.value
                initCallback(true)
            } else {
                showToast(getErrorString(RESULT_ERROR_UNAUTHORIZED))
                initCallback(false)
            }
        }
    }

    suspend fun sendFeedback(feedback: String) {
        if (FirebaseAuth.getInstance().currentUser == null) {
            showToast(getErrorString(RESULT_ERROR_UNAUTHORIZED))
            return
        }
        if (feedback.isEmpty() || feedback.length < 6) {
            showToast(getErrorString(RESULT_ERROR_FEEDBACK_TOO_SHORT))
            return
        }

        val answer = ApiHelper.requestSendFeedback(firebaseUser, tokenId, feedback)

        if (answer.isSuccess()) {
            showToast(R.string.thank_you)
        } else {
            showToast(getErrorString(answer))
        }
    }
}