package relaxeddd.englishnotify.model.http

import com.google.firebase.FirebaseNetworkException
import com.google.firebase.auth.FirebaseUser
import kotlinx.coroutines.tasks.await
import okhttp3.OkHttpClient
import relaxeddd.englishnotify.BuildConfig
import relaxeddd.englishnotify.common.*
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit

object ApiHelper {

    private const val TOKEN_PREFIX = "Bearer "
    private val apiHelper: IApi

    init {
        val okHttpClient = OkHttpClient().newBuilder()
            .connectTimeout(60, TimeUnit.SECONDS)
            .readTimeout(60, TimeUnit.SECONDS)
            .writeTimeout(60, TimeUnit.SECONDS)
            .build()
        val retrofit = Retrofit.Builder()
            .client(okHttpClient)
            .addConverterFactory(GsonConverterFactory.create())
            .baseUrl(URL_FIREBASE)
            .build()
        apiHelper = retrofit.create(IApi::class.java)
    }

    suspend fun requestInit(firebaseUser: FirebaseUser?, tokenId: String?) : InitData? {
        val userId = firebaseUser?.uid ?: ""
        val email = firebaseUser?.email ?: ""

        if (!isNetworkAvailable() || tokenId.isNullOrBlank() || userId.isBlank() || email.isBlank()) {
            return InitData(Result(RESULT_ERROR_INTERNET), User())
        }

        return executeRequest( suspend {
            apiHelper.requestInit(TOKEN_PREFIX + tokenId, userId, BuildConfig.VERSION_CODE, "", email)
        }, InitData(Result(RESULT_ERROR_INTERNET), User()))
    }

    suspend fun requestLogout(firebaseUser: FirebaseUser?, tokenId: String?) : LogoutResult? {
        val userId = firebaseUser?.uid ?: ""

        if (!isNetworkAvailable() || tokenId.isNullOrBlank() || userId.isBlank()) {
            return LogoutResult(Result(RESULT_ERROR_INTERNET))
        }

        return executeRequest( suspend {
            apiHelper.requestLogout(TOKEN_PREFIX + tokenId, userId, "")
        }, LogoutResult(Result(RESULT_ERROR_INTERNET)))
    }

    suspend fun requestSendTestNotification(firebaseUser: FirebaseUser?, tokenId: String?) : Result? {
        val userId = firebaseUser?.uid ?: ""

        if (!isNetworkAvailable() || tokenId.isNullOrBlank() || userId.isBlank()) {
            return Result(RESULT_ERROR_INTERNET)
        }

        return executeRequest( suspend {
            apiHelper.requestSendTestNotification(TOKEN_PREFIX + tokenId, userId)
        }, Result(RESULT_ERROR_INTERNET))
    }

    suspend fun requestUpdateUser(firebaseUser: FirebaseUser?, tokenId: String?, notificationsTimeType: Int,
                                  receiveNotifications: Boolean, learnLanguageType: Int, selectedTag: String) : UpdateUserResult? {
        val userId = firebaseUser?.uid ?: ""

        if (!isNetworkAvailable() || tokenId.isNullOrBlank() || userId.isBlank()) {
            return UpdateUserResult(Result(RESULT_ERROR_INTERNET), User())
        }

        return executeRequest( suspend {
            apiHelper.requestUpdateUser(TOKEN_PREFIX + tokenId, userId, receiveNotifications,
                notificationsTimeType, learnLanguageType, selectedTag)
        }, UpdateUserResult(Result(RESULT_ERROR_INTERNET), User()))
    }

    suspend fun requestVerifyPurchase(firebaseUser: FirebaseUser?, tokenId: String?, purchaseTokenId: String,
                                      signature: String, originalJson: String, itemType: String) : PurchaseResult? {
        val userId = firebaseUser?.uid ?: ""

        if (!isNetworkAvailable() || tokenId.isNullOrBlank() || userId.isBlank()) {
            return PurchaseResult(Result(RESULT_ERROR_INTERNET))
        }

        return executeRequest( suspend {
            apiHelper.requestVerifyPurchase(TOKEN_PREFIX + tokenId, userId, purchaseTokenId, signature,
                originalJson, itemType)
        }, PurchaseResult(Result(RESULT_ERROR_INTERNET)))
    }

    suspend fun requestTranslation(firebaseUser: FirebaseUser?, tokenId: String?, translationText: String,
                                   translateFromLanguage: String, translateToLanguage: String) : TranslationResult? {
        val userId = firebaseUser?.uid ?: ""

        if (!isNetworkAvailable() || tokenId.isNullOrBlank() || userId.isBlank()) {
            return TranslationResult(Result(RESULT_ERROR_INTERNET), translationText, translateFromLanguage, translateToLanguage)
        }

        return executeRequest( suspend {
            apiHelper.requestTranslation(TOKEN_PREFIX + tokenId, userId, translationText,
                translateFromLanguage, translateToLanguage)
        }, TranslationResult(Result(RESULT_ERROR_INTERNET), translationText, translateFromLanguage, translateToLanguage))
    }

    suspend fun requestSaveWords(firebaseUser: FirebaseUser?, tokenId: String?, words: List<Word>) : Result? {
        val userId = firebaseUser?.uid ?: ""

        if (!isNetworkAvailable() || tokenId.isNullOrBlank() || userId.isBlank()) {
            return Result(RESULT_ERROR_INTERNET)
        }

        return executeRequest( suspend {
            apiHelper.requestSaveWords(TOKEN_PREFIX + tokenId, userId, words)
        }, Result(RESULT_ERROR_INTERNET))
    }

    suspend fun requestLoadWords(firebaseUser: FirebaseUser?, tokenId: String?) : WordsResult? {
        val userId = firebaseUser?.uid ?: ""

        if (!isNetworkAvailable() || tokenId.isNullOrBlank() || userId.isBlank()) {
            return WordsResult(Result(RESULT_ERROR_INTERNET), ArrayList())
        }

        return executeRequest( suspend {
            apiHelper.requestLoadWords(TOKEN_PREFIX + tokenId, userId)
        }, WordsResult(Result(RESULT_ERROR_INTERNET), ArrayList()))
    }

    //------------------------------------------------------------------------------------------------------------------
    suspend fun initUserTokenId(firebaseUser: FirebaseUser?) : Resource<String> {
        return try {
            val tokenResult = firebaseUser?.getIdToken(true)?.await()

            if (tokenResult?.token?.isNotEmpty() == true) {
                Resource(status = RESULT_OK, value = tokenResult.token)
            } else {
                Resource(errorStr = ERROR_NOT_AUTHORIZED)
            }
        } catch(e: FirebaseNetworkException) {
            Resource(errorStr = ERROR_FIREBASE_NETWORK)
        }
    }

    //------------------------------------------------------------------------------------------------------------------
    private suspend fun <T> executeRequest(request: suspend () -> T, defaultAnswer: T) = try {
        request()
    } catch (e: Exception) {
        defaultAnswer
    }
}
