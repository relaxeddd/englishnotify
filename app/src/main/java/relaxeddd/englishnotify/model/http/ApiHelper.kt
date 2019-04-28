package relaxeddd.englishnotify.model.http

import com.google.firebase.auth.FirebaseUser
import okhttp3.OkHttpClient
import relaxeddd.englishnotify.BuildConfig
import relaxeddd.englishnotify.common.*
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.*
import java.util.concurrent.TimeUnit
import com.google.firebase.iid.FirebaseInstanceId
import com.jakewharton.retrofit2.adapter.kotlin.coroutines.CoroutineCallAdapterFactory
import java.net.SocketTimeoutException
import java.net.UnknownHostException

object ApiHelper {

    private val api: Api = Api()

    suspend fun requestInit(firebaseUser: FirebaseUser?, tokenId: String?, pushToken: String) : InitData? {
        val requestId = UUID.randomUUID().toString()
        val userId = firebaseUser?.uid ?: ""
        val email = firebaseUser?.email ?: ""
        val appVersion = BuildConfig.VERSION_CODE

        try {
            return if (tokenId?.isNotEmpty() == true) {
                api.requestInit(tokenId, requestId, userId, appVersion, pushToken, email)
            } else {
                InitData(Result(RESULT_ERROR_UNAUTHORIZED), User())
            }
        } catch (e: UnknownHostException) {
            return InitData(Result(RESULT_ERROR_INTERNET), User())
        } catch (e: SocketTimeoutException) {
            return InitData(Result(RESULT_ERROR_INTERNET), User())
        }
    }

    suspend fun requestSendFeedback(firebaseUser: FirebaseUser?, tokenId: String?, feedback: String) : Result? {
        val requestId = UUID.randomUUID().toString()
        val userId = firebaseUser?.uid ?: ""

        try {
            return if (tokenId?.isNotEmpty() == true) {
                api.requestSendFeedback(tokenId, requestId, userId, feedback)
            } else {
                Result(msg = ERROR_TOKEN_NOT_INIT)
            }
        } catch (e: UnknownHostException) {
            return Result(RESULT_ERROR_INTERNET)
        } catch (e: SocketTimeoutException) {
            return Result(RESULT_ERROR_INTERNET)
        }
    }

    suspend fun requestSendTestNotification(firebaseUser: FirebaseUser?, tokenId: String?) : Result? {
        val requestId = UUID.randomUUID().toString()
        val userId = firebaseUser?.uid ?: ""

        try {
            return if (tokenId?.isNotEmpty() == true) {
                api.requestSendTestNotfication(tokenId, requestId, userId)
            } else {
                Result(msg = ERROR_SEND_TEST_NOTIFICATION)
            }
        } catch (e: UnknownHostException) {
            return Result(RESULT_ERROR_INTERNET)
        } catch (e: SocketTimeoutException) {
            return Result(RESULT_ERROR_INTERNET)
        }
    }

    suspend fun requestUpdateUser(firebaseUser: FirebaseUser?, tokenId: String?, user: User) : UpdateUserResult? {
        val requestId = UUID.randomUUID().toString()
        val userId = firebaseUser?.uid ?: ""
        val notificationsTimeType = user.notificationsTimeType
        val receiveNotifications = user.receiveNotifications
        val learnLanguageType = user.learnLanguageType
        val selectedTag = user.selectedTag

        try {
            return if (tokenId?.isNotEmpty() == true) {
                api.requestUpdateUser(tokenId, requestId, userId, notificationsTimeType, receiveNotifications,
                    learnLanguageType, selectedTag)
            } else {
                UpdateUserResult(Result(RESULT_ERROR_UNAUTHORIZED), User())
            }
        } catch (e: UnknownHostException) {
            return UpdateUserResult(Result(RESULT_ERROR_INTERNET), User())
        } catch (e: SocketTimeoutException) {
            return UpdateUserResult(Result(RESULT_ERROR_INTERNET), User())
        }
    }

    suspend fun requestVerifyPurchase(firebaseUser: FirebaseUser?, tokenId: String?, purchaseTokenId: String,
                                      signature: String, originalJson: String, itemType: String) : PurchaseResult? {
        val requestId = UUID.randomUUID().toString()
        val userId = firebaseUser?.uid ?: ""

        try {
            return if (tokenId?.isNotEmpty() == true) {
                api.requestVerifyPurchase(tokenId, requestId, userId, purchaseTokenId, signature, originalJson, itemType)
            } else {
                PurchaseResult(Result(RESULT_PURCHASE_VERIFIED_ERROR))
            }
        } catch (e: UnknownHostException) {
            return PurchaseResult(Result(RESULT_ERROR_INTERNET))
        } catch (e: SocketTimeoutException) {
            return PurchaseResult(Result(RESULT_ERROR_INTERNET))
        }
    }

    //------------------------------------------------------------------------------------------------------------------
    fun initUserTokenId(firebaseUser: FirebaseUser?, resultListener: (tokenId: Resource<String>) -> Unit) {
        firebaseUser?.getIdToken(false)?.addOnCompleteListener {
            if (it.isSuccessful) {
                val receivedTokenId = it.result?.token ?: ""
                resultListener(Resource(status = RESULT_OK, value = receivedTokenId))
            } else {
                resultListener(Resource(errorStr = ERROR_NOT_AUTHORIZED))
            }
        } ?: resultListener(Resource(errorStr = ERROR_NOT_AUTHORIZED))
    }

    fun initPushTokenId(resultListener: (tokenId: Resource<String>) -> Unit) {
        FirebaseInstanceId.getInstance().instanceId.addOnCompleteListener {
            if (it.isSuccessful) {
                val receivedTokenId = it.result?.token ?: ""
                resultListener(Resource(status = RESULT_OK, value = receivedTokenId))
            } else {
                resultListener(Resource(errorStr = ERROR_NOT_AUTHORIZED))
            }
        }
    }

    private class Api {

        private val tokenPrefix = "Bearer "
        private val apiHelper: IApi

        init {
            val okHttpClient = OkHttpClient().newBuilder()
                .connectTimeout(60, TimeUnit.SECONDS)
                .readTimeout(60, TimeUnit.SECONDS)
                .writeTimeout(60, TimeUnit.SECONDS)
                .build()

            val retrofit = Retrofit.Builder()
                .client(okHttpClient)
                .addCallAdapterFactory(CoroutineCallAdapterFactory())
                .addConverterFactory(GsonConverterFactory.create())
                .baseUrl(URL_FIREBASE) //URL_LOCAL or URL_FIREBASE
                .build()

            apiHelper = retrofit.create(IApi::class.java)
        }

        //--------------------------------------------------------------------------------------------------------------
        suspend fun requestInit(tokenId: String, requestId: String, userId: String, appVersion: Int, pushToken: String,
                                email: String) : InitData? {
            return apiHelper.requestInit(tokenPrefix + tokenId, requestId, userId, appVersion, pushToken,
                email).await()
        }

        suspend fun requestVerifyPurchase(tokenId: String, requestId: String, userId: String, purchaseTokenId: String,
                                          signature: String, originalJson: String, itemType: String) : PurchaseResult? {
            return apiHelper.requestVerifyPurchase(tokenPrefix + tokenId, requestId, userId, purchaseTokenId,
                                                   signature, originalJson, itemType).await()
        }

        suspend fun requestSendFeedback(tokenId: String, requestId: String, userId: String, message: String) : Result? {
            return apiHelper.requestSendFeedback(tokenPrefix + tokenId, requestId, userId, message).await()
        }

        suspend fun requestSendTestNotfication(tokenId: String, requestId: String, userId: String) : Result? {
            return apiHelper.requestSendTestNotification(tokenPrefix + tokenId, requestId, userId).await()
        }

        suspend fun requestUpdateUser(tokenId: String, requestId: String, userId: String, notificationsTimeType: Int,
                                      receiveNotifications: Boolean, learnLanguageType: Int,
                                      selectedTag: String): UpdateUserResult? {
            return apiHelper.requestUpdateUser(tokenPrefix + tokenId, requestId, userId,
                receiveNotifications, notificationsTimeType, learnLanguageType, selectedTag).await()
        }
    }
}