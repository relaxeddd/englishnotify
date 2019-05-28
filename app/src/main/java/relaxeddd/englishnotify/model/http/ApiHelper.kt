package relaxeddd.englishnotify.model.http

import com.google.firebase.auth.FirebaseUser
import okhttp3.OkHttpClient
import relaxeddd.englishnotify.BuildConfig
import relaxeddd.englishnotify.common.*
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit
import com.google.firebase.iid.FirebaseInstanceId
import com.jakewharton.retrofit2.adapter.kotlin.coroutines.CoroutineCallAdapterFactory
import okhttp3.internal.http2.StreamResetException
import org.json.JSONArray
import retrofit2.HttpException
import java.net.ConnectException
import java.net.SocketTimeoutException
import java.net.UnknownHostException
import javax.net.ssl.SSLHandshakeException

object ApiHelper {

    private val api: Api = Api()

    suspend fun requestInit(firebaseUser: FirebaseUser?, tokenId: String?, pushToken: String, learnStage0: JSONArray,
                            learnStage1: JSONArray, learnStage2: JSONArray, learnStage3: JSONArray) : InitData? {
        val userId = firebaseUser?.uid ?: ""
        val email = firebaseUser?.email ?: ""
        if (!isNetworkAvailable() || tokenId?.isNotEmpty() != true || userId.isEmpty() || email.isEmpty()) {
            return InitData(Result(RESULT_ERROR_INTERNET), User())
        }
        return executeRequest( suspend { api.requestInit(tokenId, userId, BuildConfig.VERSION_CODE, pushToken, email,
            learnStage0, learnStage1, learnStage2, learnStage3) }, InitData(Result(RESULT_ERROR_INTERNET), User()))
    }

    suspend fun requestSendFeedback(firebaseUser: FirebaseUser?, tokenId: String?, feedback: String) : Result? {
        val userId = firebaseUser?.uid ?: ""

        return if (tokenId?.isNotEmpty() == true) {
            return executeRequest( suspend { api.requestSendFeedback(tokenId, requestId, userId, feedback) }, Result(RESULT_ERROR_INTERNET))
        } else {
            Result(msg = ERROR_TOKEN_NOT_INIT)
        }
        return executeRequest( suspend { api.requestSendFeedback(tokenId, userId, feedback) }, Result(RESULT_ERROR_INTERNET))
    }

    suspend fun requestSendTestNotification(firebaseUser: FirebaseUser?, tokenId: String?) : Result? {
        val userId = firebaseUser?.uid ?: ""
        if (!isNetworkAvailable() || tokenId?.isNotEmpty() != true || userId.isEmpty()) {
            return Result(RESULT_ERROR_INTERNET)
        }
        return executeRequest( suspend { api.requestSendTestNotification(tokenId, userId) }, Result(RESULT_ERROR_INTERNET))
    }

    suspend fun requestUpdateUser(firebaseUser: FirebaseUser?, tokenId: String?, notificationsTimeType: Int,
                                  receiveNotifications: Boolean, learnLanguageType: Int, selectedTag: String) : UpdateUserResult? {
        val userId = firebaseUser?.uid ?: ""

        return if (tokenId?.isNotEmpty() == true) {
            return executeRequest( suspend { api.requestSendTestNotification(tokenId, requestId, userId) }, Result(RESULT_ERROR_INTERNET))
        } else {
            Result(msg = ERROR_SEND_TEST_NOTIFICATION)
        }
    }

    suspend fun requestUpdateUser(firebaseUser: FirebaseUser?, tokenId: String?, user: User) : UpdateUserResult? {
        if (!isNetworkAvailable()) {
            return UpdateUserResult(Result(RESULT_ERROR_INTERNET), User())
        }
        return executeRequest( suspend { api.requestUpdateUser(tokenId, userId, notificationsTimeType, receiveNotifications,
            learnLanguageType, selectedTag) }, UpdateUserResult(Result(RESULT_ERROR_INTERNET), User()))
    }

    suspend fun requestVerifyPurchase(firebaseUser: FirebaseUser?, tokenId: String?, purchaseTokenId: String,
                                      signature: String, originalJson: String, itemType: String) : PurchaseResult? {
        val userId = firebaseUser?.uid ?: ""
        if (!isNetworkAvailable() || tokenId?.isNotEmpty() != true || userId.isEmpty()) {
            return PurchaseResult(Result(RESULT_ERROR_INTERNET))
        }
        return executeRequest( suspend { api.requestVerifyPurchase(tokenId, userId, purchaseTokenId, signature, originalJson, itemType) },
            PurchaseResult(Result(RESULT_ERROR_INTERNET)))
    }

    suspend fun requestInsertOwnWord(firebaseUser: FirebaseUser?, tokenId: String?, wordId: String, eng: String,
                                     rus: String, transcription: String) : CreateWordResult? {
        val userId = firebaseUser?.uid ?: ""
        if (!isNetworkAvailable() || tokenId?.isNotEmpty() != true || userId.isEmpty()) {
            return CreateWordResult(Result(RESULT_ERROR_INTERNET))
        }
        return executeRequest( suspend { api.requestInsertOwnWord(tokenId, userId, wordId, eng, rus, transcription) },
            CreateWordResult(Result(RESULT_ERROR_INTERNET)))
    }

    suspend fun requestUpdateWordLearnStage(firebaseUser: FirebaseUser?, tokenId: String?, wordId: String, learnStage: Int) : Result? {
        val userId = firebaseUser?.uid ?: ""
        if (!isNetworkAvailable() || tokenId?.isNotEmpty() != true || userId.isEmpty()) {
            return Result(RESULT_ERROR_INTERNET)
        }
        return executeRequest( suspend { api.requestUpdateWordLearnStage(tokenId, userId, wordId, learnStage) }, Result(RESULT_ERROR_INTERNET))
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
        val userId = firebaseUser?.uid ?: ""
        val wordIdsJson = JSONArray()
        for (wordId in wordIds) {
            if (wordId.isNotEmpty()) wordIdsJson.put(wordId)
        }

        if (!isNetworkAvailable() || tokenId?.isNotEmpty() != true || userId.isEmpty() || wordIds.isEmpty()) {
            return Result(RESULT_ERROR_INTERNET)
        }

        return if (isChangeIsDeleted && isChangeIsOwnCategory) {
            executeRequest( suspend { api.requestUpdateWords(tokenId, userId, wordIdsJson, isDeleted, isOwnCategory) }, Result(RESULT_ERROR_INTERNET))
        } else if (isChangeIsDeleted) {
            executeRequest( suspend { api.requestDeleteWords(tokenId, userId, wordIdsJson) }, Result(RESULT_ERROR_INTERNET))
        } else {
            executeRequest( suspend { api.requestRemoveFromOwnWords(tokenId, userId, wordIdsJson, isOwnCategory) }, Result(RESULT_ERROR_INTERNET))
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
                .baseUrl(URL_FIREBASE)
                .build()
            apiHelper = retrofit.create(IApi::class.java)
        }

        //--------------------------------------------------------------------------------------------------------------
        suspend fun requestInit(tokenId: String, userId: String, appVersion: Int, pushToken: String,
                                email: String, learnStage0: JSONArray, learnStage1: JSONArray, learnStage2: JSONArray,
                                learnStage3: JSONArray) : InitData? {
            return apiHelper.requestInitAsync(tokenPrefix + tokenId, userId, appVersion, pushToken,
                email, learnStage0, learnStage1, learnStage2, learnStage3).await()
        }

        suspend fun requestVerifyPurchase(tokenId: String, userId: String, purchaseTokenId: String,
                                          signature: String, originalJson: String, itemType: String) : PurchaseResult? {
            return apiHelper.requestVerifyPurchaseAsync(tokenPrefix + tokenId, userId, purchaseTokenId,
                signature, originalJson, itemType).await()
        }

        suspend fun requestSendFeedback(tokenId: String, userId: String, message: String) : Result? {
            return apiHelper.requestSendFeedbackAsync(tokenPrefix + tokenId, userId, message).await()
        }

        suspend fun requestSendTestNotification(tokenId: String, userId: String) : Result? {
            return apiHelper.requestSendTestNotificationAsync(tokenPrefix + tokenId, userId).await()
        }

        suspend fun requestUpdateUser(tokenId: String, userId: String, notificationsTimeType: Int,
                                      receiveNotifications: Boolean, learnLanguageType: Int,
                                      selectedTag: String): UpdateUserResult? {
            return apiHelper.requestUpdateUserAsync(tokenPrefix + tokenId, userId,
                receiveNotifications, notificationsTimeType, learnLanguageType, selectedTag).await()
        }

        suspend fun requestInsertOwnWord(tokenId: String, userId: String, wordId: String, eng: String,
                                         rus: String, transcription: String) : CreateWordResult? {
            return apiHelper.requestInsertOwnWordAsync(tokenPrefix + tokenId, userId, wordId, eng, rus, transcription).await()
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