package relaxeddd.pushenglish.push

import com.google.firebase.iid.FirebaseInstanceId
import com.google.firebase.iid.FirebaseInstanceIdService

class MyFirebaseInstanceIDService : FirebaseInstanceIdService() {

    override fun onTokenRefresh() {
        //val refreshedToken = FirebaseInstanceId.getInstance().token
        sendRegistrationToServer()
    }

    private fun sendRegistrationToServer() {}
}