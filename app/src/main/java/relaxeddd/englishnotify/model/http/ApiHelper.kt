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
import okhttp3.internal.http2.StreamResetException
import org.json.JSONArray
import org.json.JSONObject
import retrofit2.HttpException
import java.net.ConnectException
import java.net.SocketTimeoutException
import java.net.UnknownHostException
import javax.net.ssl.SSLHandshakeException

object ApiHelper {

    private val api: Api = Api()

    suspend fun requestInit(firebaseUser: FirebaseUser?, tokenId: String?, pushToken: String) : InitData? {
        if (!isNetworkAvailable()) {
            return InitData(Result(RESULT_ERROR_INTERNET), User())
        }

        val requestId = UUID.randomUUID().toString()
        val userId = firebaseUser?.uid ?: ""
        val email = firebaseUser?.email ?: ""
        val appVersion = BuildConfig.VERSION_CODE

        return if (tokenId?.isNotEmpty() == true) {
            return executeRequest( suspend { api.requestInit(tokenId, requestId, userId, appVersion, pushToken, email) },
                InitData(Result(RESULT_ERROR_INTERNET), User()))
        } else {
            InitData(Result(RESULT_ERROR_UNAUTHORIZED), User())
        }
    }

    suspend fun requestSendFeedback(firebaseUser: FirebaseUser?, tokenId: String?, feedback: String) : Result? {
        if (!isNetworkAvailable()) {
            return Result(RESULT_ERROR_INTERNET)
        }

        val requestId = UUID.randomUUID().toString()
        val userId = firebaseUser?.uid ?: ""

        return if (tokenId?.isNotEmpty() == true) {
            return executeRequest( suspend { api.requestSendFeedback(tokenId, requestId, userId, feedback) }, Result(RESULT_ERROR_INTERNET))
        } else {
            Result(message = ERROR_TOKEN_NOT_INIT)
        }
    }

    suspend fun requestSendTestNotification(firebaseUser: FirebaseUser?, tokenId: String?) : Result? {
        if (!isNetworkAvailable()) {
            return Result(RESULT_ERROR_INTERNET)
        }

        val requestId = UUID.randomUUID().toString()
        val userId = firebaseUser?.uid ?: ""

        return if (tokenId?.isNotEmpty() == true) {
            return executeRequest( suspend { api.requestSendTestNotification(tokenId, requestId, userId) }, Result(RESULT_ERROR_INTERNET))
        } else {
            Result(message = ERROR_SEND_TEST_NOTIFICATION)
        }
    }

    suspend fun requestUpdateUser(firebaseUser: FirebaseUser?, tokenId: String?, user: User) : UpdateUserResult? {
        if (!isNetworkAvailable()) {
            return UpdateUserResult(Result(RESULT_ERROR_INTERNET), User())
        }

        val requestId = UUID.randomUUID().toString()
        val userId = firebaseUser?.uid ?: ""
        val notificationsTimeType = user.notificationsTimeType
        val receiveNotifications = user.receiveNotifications
        val learnLanguageType = user.learnLanguageType
        val selectedTag = user.selectedTag

        return if (tokenId?.isNotEmpty() == true) {
            return executeRequest( suspend { api.requestUpdateUser(tokenId, requestId, userId, notificationsTimeType, receiveNotifications,
                learnLanguageType, selectedTag) }, UpdateUserResult(Result(RESULT_ERROR_INTERNET), User()))
        } else {
            UpdateUserResult(Result(RESULT_ERROR_UNAUTHORIZED), User())
        }
    }

    suspend fun requestVerifyPurchase(firebaseUser: FirebaseUser?, tokenId: String?, purchaseTokenId: String,
                                      signature: String, originalJson: String, itemType: String) : PurchaseResult? {
        if (!isNetworkAvailable()) {
            return PurchaseResult(Result(RESULT_ERROR_INTERNET))
        }

        val requestId = UUID.randomUUID().toString()
        val userId = firebaseUser?.uid ?: ""

        return if (tokenId?.isNotEmpty() == true) {
            return executeRequest( suspend { api.requestVerifyPurchase(tokenId, requestId, userId, purchaseTokenId, signature, originalJson, itemType) },
                PurchaseResult(Result(RESULT_ERROR_INTERNET)))
        } else {
            PurchaseResult(Result(RESULT_PURCHASE_VERIFIED_ERROR))
        }
    }

    suspend fun requestInsertOwnWord(firebaseUser: FirebaseUser?, tokenId: String?, word: JSONObject) : Result? {
        if (!isNetworkAvailable()) {
            return Result(RESULT_ERROR_INTERNET)
        }

        val requestId = UUID.randomUUID().toString()
        val userId = firebaseUser?.uid ?: ""

        return if (tokenId?.isNotEmpty() == true) {
            return executeRequest( suspend { api.requestInsertOwnWord(tokenId, requestId, userId, word) }, Result(RESULT_ERROR_INTERNET))
        } else {
            Result(RESULT_ERROR_OWN_WORD)
        }
    }

    suspend fun requestDeleteOwnWords(firebaseUser: FirebaseUser?, tokenId: String?, wordIds: JSONArray) : Result? {
        if (!isNetworkAvailable()) {
            return Result(RESULT_ERROR_INTERNET)
        }

        val requestId = UUID.randomUUID().toString()
        val userId = firebaseUser?.uid ?: ""

        return if (tokenId?.isNotEmpty() == true) {
            return executeRequest( suspend { api.requestDeleteOwnWords(tokenId, requestId, userId, wordIds) }, Result(RESULT_ERROR_INTERNET))
        } else {
            Result(RESULT_ERROR_OWN_WORD)
        }
    }

    suspend fun requestOwnWords(firebaseUser: FirebaseUser?, tokenId: String?) : OwnWordsResult? {
        if (!isNetworkAvailable()) {
            return OwnWordsResult(Result(RESULT_ERROR_INTERNET))
        }

        val requestId = UUID.randomUUID().toString()
        val userId = firebaseUser?.uid ?: ""

        return if (tokenId?.isNotEmpty() == true) {
            return executeRequest( suspend { api.requestOwnWords(tokenId, requestId, userId) }, OwnWordsResult(Result(RESULT_ERROR_INTERNET)))
        } else {
            OwnWordsResult(Result(RESULT_ERROR_OWN_GET))
        }
    }

    //------------------------------------------------------------------------------------------------------------------
    private suspend fun <T> executeRequest(request: suspend () -> T, defaultAnswer: T) = try {
        request()
    } catch (e: UnknownHostException) {
        defaultAnswer
    } catch (e: SocketTimeoutException) {
        defaultAnswer
    } catch (e: StreamResetException) {
        defaultAnswer
    } catch (e: HttpException) {
        defaultAnswer
    } catch (e: ConnectException) {
        defaultAnswer
    } catch (e: SSLHandshakeException) {
        defaultAnswer
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
            return apiHelper.requestInitAsync(tokenPrefix + tokenId, requestId, userId, appVersion, pushToken,
                email).await()
        }

        suspend fun requestVerifyPurchase(tokenId: String, requestId: String, userId: String, purchaseTokenId: String,
                                          signature: String, originalJson: String, itemType: String) : PurchaseResult? {
            return apiHelper.requestVerifyPurchaseAsync(tokenPrefix + tokenId, requestId, userId, purchaseTokenId,
                                                   signature, originalJson, itemType).await()
        }

        suspend fun requestSendFeedback(tokenId: String, requestId: String, userId: String, message: String) : Result? {
            return apiHelper.requestSendFeedbackAsync(tokenPrefix + tokenId, requestId, userId, message).await()
        }

        suspend fun requestSendTestNotification(tokenId: String, requestId: String, userId: String) : Result? {
            return apiHelper.requestSendTestNotificationAsync(tokenPrefix + tokenId, requestId, userId).await()
        }

        suspend fun requestUpdateUser(tokenId: String, requestId: String, userId: String, notificationsTimeType: Int,
                                      receiveNotifications: Boolean, learnLanguageType: Int,
                                      selectedTag: String): UpdateUserResult? {
            return apiHelper.requestUpdateUserAsync(tokenPrefix + tokenId, requestId, userId,
                receiveNotifications, notificationsTimeType, learnLanguageType, selectedTag).await()
        }

        suspend fun requestInsertOwnWord(tokenId: String, requestId: String, userId: String, word: JSONObject) : Result? {
            return apiHelper.requestInsertOwnWordAsync(tokenPrefix + tokenId, requestId, userId, word).await()
        }

        suspend fun requestDeleteOwnWords(tokenId: String, requestId: String, userId: String, wordIds: JSONArray) : Result? {
            return apiHelper.requestDeleteOwnWordsAsync(tokenPrefix + tokenId, requestId, userId, wordIds).await()
        }

        suspend fun requestOwnWords(tokenId: String, requestId: String, userId: String) : OwnWordsResult? {
            return apiHelper.requestOwnWordsAsync(tokenPrefix + tokenId, requestId, userId).await()
        }
    }
}