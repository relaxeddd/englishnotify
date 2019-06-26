package relaxeddd.englishnotify.model.repository

import android.os.Build
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import relaxeddd.englishnotify.common.RESULT_ERROR_FEEDBACK_TOO_SHORT
import relaxeddd.englishnotify.common.RESULT_ERROR_UNAUTHORIZED
import relaxeddd.englishnotify.common.getErrorString
import relaxeddd.englishnotify.common.showToast
import relaxeddd.englishnotify.R
import relaxeddd.englishnotify.model.http.ApiHelper
import relaxeddd.englishnotify.push.MyFirebaseMessagingService

class RepositoryCommon private constructor() {

    companion object {
        @Volatile private var instance: RepositoryCommon? = null
        fun getInstance() = instance ?: synchronized(this) {
            instance
                ?: RepositoryCommon().also { instance = it }
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

        if (answer?.isSuccess() == true) {
            showToast(R.string.thank_you)
        } else {
            showToast(getErrorString(answer))
        }
    }

    suspend fun sendVote(vote: Int) : Boolean {
        if (FirebaseAuth.getInstance().currentUser == null) {
            showToast(getErrorString(RESULT_ERROR_UNAUTHORIZED))
            return false
        }

        val sdk = Build.VERSION.SDK_INT
        val model = Build.MODEL ?: ""
        val manufacturer = Build.MANUFACTURER ?: ""
        val device = Build.DEVICE ?: ""
        val brand = Build.BRAND ?: ""
        val product = Build.PRODUCT ?: ""

        val answer = ApiHelper.requestVote(firebaseUser, tokenId, vote, sdk, model, manufacturer, device, brand, product)

        if (answer?.isSuccess() == true) {
            showToast(R.string.thank_you)
        } else {
            showToast(getErrorString(answer))
        }

        return answer?.isSuccess() == true
    }
}