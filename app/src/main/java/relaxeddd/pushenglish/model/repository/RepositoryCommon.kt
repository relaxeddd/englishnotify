package relaxeddd.pushenglish.model.repository

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import relaxeddd.pushenglish.R
import relaxeddd.pushenglish.common.ERROR_NOT_AUTHORIZED
import relaxeddd.pushenglish.common.showToast
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
            showToast(ERROR_NOT_AUTHORIZED)
            initCallback(false)
            return
        }

        firebaseUser = FirebaseAuth.getInstance().currentUser

        ApiHelper.initUserTokenId(firebaseUser) {
            if (it.isSuccess() && it.value != null) {
                tokenId = it.value
                initCallback(true)
            } else {
                showToast(ERROR_NOT_AUTHORIZED)
                initCallback(false)
            }
        }
    }

    suspend fun sendFeedback(feedback: String) {
        if (FirebaseAuth.getInstance().currentUser == null) {
            showToast(ERROR_NOT_AUTHORIZED)
            return
        }
        if (feedback.isEmpty()) {
            showToast(R.string.feedback_is_empty)
            return
        }

        val answer = ApiHelper.requestSendFeedback(firebaseUser, tokenId, feedback)

        if (answer.isSuccess()) {
            showToast(R.string.thank_you)
        } else {
            showToast(answer.errorStr)
        }
    }
}