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

    suspend fun requestUpdateLearnStages(firebaseUser: FirebaseUser?, tokenId: String?, learnStage0: JSONArray,
                                         learnStage1: JSONArray, learnStage2: JSONArray, learnStage3: JSONArray) : OwnWordsResult? {
        if (!isNetworkAvailable()) {
            return OwnWordsResult(Result(RESULT_ERROR_INTERNET))
        }

        val requestId = UUID.randomUUID().toString()
        val userId = firebaseUser?.uid ?: ""

        return if (tokenId?.isNotEmpty() == true) {
            return executeRequest( suspend { api.requestUpdateLearnStages(tokenId, requestId, userId, learnStage0,
                learnStage1, learnStage2, learnStage3) }, OwnWordsResult(Result(RESULT_ERROR_INTERNET)))
        } else {
            OwnWordsResult(Result(RESULT_ERROR_INTERNET))
        }
    }

    suspend fun requestSetLearnStage(firebaseUser: FirebaseUser?, tokenId: String?, wordId: String, learnStage: Int) : Result? {
        if (!isNetworkAvailable()) {
            return Result(RESULT_ERROR_INTERNET)
        }

        val requestId = UUID.randomUUID().toString()
        val userId = firebaseUser?.uid ?: ""

        return if (tokenId?.isNotEmpty() == true) {
            return executeRequest( suspend { api.requestSetLearnStage(tokenId, requestId, userId, wordId, learnStage) }, Result(RESULT_ERROR_INTERNET))
        } else {
            Result(RESULT_ERROR_INTERNET)
        }
    }

    suspend fun requestUpdateWord(firebaseUser: FirebaseUser?, tokenId: String?, wordId: String, eng: String, rus: String,
                                  transcription: String, isDeleted: Boolean, isOwnCategory: Boolean, learnStage: Int) : Result? {
        if (!isNetworkAvailable()) {
            return Result(RESULT_ERROR_INTERNET)
        }

        val userId = firebaseUser?.uid ?: ""

        return if (tokenId?.isNotEmpty() == true && userId.isNotEmpty()) {
            return executeRequest( suspend { api.requestUpdateWord(tokenId, userId, wordId, eng, rus, transcription,
                isDeleted, isOwnCategory, learnStage) }, Result(RESULT_ERROR_INTERNET))
        } else {
            Result(RESULT_ERROR_INTERNET)
        }
    }

    suspend fun requestUpdateWordLearnStage(firebaseUser: FirebaseUser?, tokenId: String?, wordId: String,
                                            learnStage: Int) : Result? {
        if (!isNetworkAvailable()) {
            return Result(RESULT_ERROR_INTERNET)
        }

        val userId = firebaseUser?.uid ?: ""

        return if (tokenId?.isNotEmpty() == true && userId.isNotEmpty()) {
            return executeRequest( suspend { api.requestUpdateWordLearnStage(tokenId, userId, wordId, learnStage) }, Result(RESULT_ERROR_INTERNET))
        } else {
            Result(RESULT_ERROR_INTERNET)
        }
    }

    suspend fun requestDeleteWords(firebaseUser: FirebaseUser?, tokenId: String?, wordIds: List<String>) : Result? {
        return requestUpdateWords(firebaseUser, tokenId, wordIds, isChangeIsDeleted = true, isChangeIsOwnCategory = false,
            isDeleted = true, isOwnCategory = false)
    }

    suspend fun requestAddToOwnWords(firebaseUser: FirebaseUser?, tokenId: String?, wordIds: List<String>) : Result? {
        return requestUpdateWords(firebaseUser, tokenId, wordIds, isChangeIsDeleted = false, isChangeIsOwnCategory = true,
            isDeleted = false, isOwnCategory = true)
    }

    suspend fun requestRemoveFromOwnWords(firebaseUser: FirebaseUser?, tokenId: String?, wordIds: List<String>) : Result? {
        return requestUpdateWords(firebaseUser, tokenId, wordIds, isChangeIsDeleted = false, isChangeIsOwnCategory = true,
            isDeleted = false, isOwnCategory = false)
    }

    private suspend fun requestUpdateWords(firebaseUser: FirebaseUser?, tokenId: String?, wordIds: List<String>,
                                   isChangeIsDeleted: Boolean, isChangeIsOwnCategory: Boolean,
                                   isDeleted: Boolean = false, isOwnCategory: Boolean = false) : Result? {
        if (!isNetworkAvailable()) {
            return Result(RESULT_ERROR_INTERNET)
        }

        val userId = firebaseUser?.uid ?: ""
        val wordIdsJson = JSONArray()
        for (wordId in wordIds) {
            if (wordId.isNotEmpty()) wordIdsJson.put(wordId)
        }

        return if (tokenId?.isNotEmpty() == true && userId.isNotEmpty()) {
            return if (isChangeIsDeleted && isChangeIsOwnCategory) {
                executeRequest( suspend { api.requestUpdateWords(tokenId, userId, wordIdsJson, isDeleted, isOwnCategory) }, Result(RESULT_ERROR_INTERNET))
            } else if (isChangeIsDeleted) {
                executeRequest( suspend { api.requestDeleteWords(tokenId, userId, wordIdsJson) }, Result(RESULT_ERROR_INTERNET))
            } else {
                executeRequest( suspend { api.requestRemoveFromOwnWords(tokenId, userId, wordIdsJson, isOwnCategory) }, Result(RESULT_ERROR_INTERNET))
            }
        } else {
            Result(RESULT_ERROR_INTERNET)
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

        suspend fun requestUpdateLearnStages(tokenId: String, requestId: String, userId: String, learnStage0: JSONArray,
                                             learnStage1: JSONArray, learnStage2: JSONArray, learnStage3: JSONArray) : OwnWordsResult? {
            return apiHelper.requestUpdateLearnStagesAsync(tokenPrefix + tokenId, requestId, userId, learnStage0,
                learnStage1, learnStage2, learnStage3).await()
        }

        suspend fun requestSetLearnStage(tokenId: String, requestId: String, userId: String, wordId: String,
                                             learnStage: Int) : Result? {
            return apiHelper.requestSetLearnStageAsync(tokenPrefix + tokenId, requestId, userId, wordId, learnStage).await()
        }

        suspend fun requestUpdateWord(tokenId: String, userId: String, wordId: String, eng: String, rus: String, transcription: String,
                                      isDeleted: Boolean, isOwnCategory: Boolean, learnStage: Int) : Result? {
            return apiHelper.requestUpdateWordAsync(tokenPrefix + tokenId, userId, wordId, eng, rus, transcription,
                isDeleted, isOwnCategory, learnStage).await()
        }

        suspend fun requestUpdateWordLearnStage(tokenId: String, userId: String, wordId: String, learnStage: Int) : Result? {
            return apiHelper.requestUpdateWordLearnStageAsync(tokenPrefix + tokenId, userId, wordId, learnStage).await()
        }

        suspend fun requestUpdateWords(tokenId: String, userId: String, wordIds: JSONArray, isDeleted: Boolean, isOwnCategory: Boolean) : Result? {
            return apiHelper.requestUpdateWordsAsync(tokenPrefix + tokenId, userId, wordIds, isDeleted, isOwnCategory).await()
        }

        suspend fun requestDeleteWords(tokenId: String, userId: String, wordIds: JSONArray) : Result? {
            return apiHelper.requestDeleteWordsAsync(tokenPrefix + tokenId, userId, wordIds).await()
        }

        suspend fun requestRemoveFromOwnWords(tokenId: String, userId: String, wordIds: JSONArray, isOwnCategory: Boolean) : Result? {
            return apiHelper.requestSetIsOwnCategoryWordsAsync(tokenPrefix + tokenId, userId, wordIds, isOwnCategory).await()
        }
    }
}