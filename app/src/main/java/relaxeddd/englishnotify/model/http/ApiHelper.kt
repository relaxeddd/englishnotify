package relaxeddd.englishnotify.model.http

import android.system.ErrnoException
import com.google.firebase.auth.FirebaseUser
import okhttp3.OkHttpClient
import relaxeddd.englishnotify.BuildConfig
import relaxeddd.englishnotify.common.*
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit
import com.google.firebase.iid.FirebaseInstanceId
import okhttp3.internal.http2.StreamResetException
import org.json.JSONArray
import retrofit2.HttpException
import java.net.ConnectException
import java.net.SocketTimeoutException
import java.net.UnknownHostException
import javax.net.ssl.SSLHandshakeException

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

    suspend fun requestInit(firebaseUser: FirebaseUser?, tokenId: String?, pushToken: String) : InitData? {
        val userId = firebaseUser?.uid ?: ""
        val email = firebaseUser?.email ?: ""

        if (!isNetworkAvailable() || tokenId?.isNotEmpty() != true || userId.isEmpty() || email.isEmpty()) {
            return InitData(Result(RESULT_ERROR_INTERNET), User())
        }

        return executeRequest( suspend {
            apiHelper.requestInit(TOKEN_PREFIX + tokenId, userId, BuildConfig.VERSION_CODE, pushToken, email)
        }, InitData(Result(RESULT_ERROR_INTERNET), User()))
    }

    suspend fun requestSendFeedback(firebaseUser: FirebaseUser?, tokenId: String?, feedback: String) : Result? {
        val userId = firebaseUser?.uid ?: ""

        if (!isNetworkAvailable() || tokenId?.isNotEmpty() != true || userId.isEmpty()) {
            return Result(RESULT_ERROR_INTERNET)
        }

        return executeRequest( suspend {
            apiHelper.requestSendFeedback(TOKEN_PREFIX + tokenId, userId, feedback)
        }, Result(RESULT_ERROR_INTERNET))
    }

    suspend fun requestSendTestNotification(firebaseUser: FirebaseUser?, tokenId: String?) : Result? {
        val userId = firebaseUser?.uid ?: ""

        if (!isNetworkAvailable() || tokenId?.isNotEmpty() != true || userId.isEmpty()) {
            return Result(RESULT_ERROR_INTERNET)
        }

        return executeRequest( suspend {
            apiHelper.requestSendTestNotification(TOKEN_PREFIX + tokenId, userId)
        }, Result(RESULT_ERROR_INTERNET))
    }

    suspend fun requestUpdateUser(firebaseUser: FirebaseUser?, tokenId: String?, notificationsTimeType: Int,
                                  receiveNotifications: Boolean, learnLanguageType: Int, selectedTag: String) : UpdateUserResult? {
        val userId = firebaseUser?.uid ?: ""

        if (!isNetworkAvailable() || tokenId?.isNotEmpty() != true || userId.isEmpty()) {
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

        if (!isNetworkAvailable() || tokenId?.isNotEmpty() != true || userId.isEmpty()) {
            return PurchaseResult(Result(RESULT_ERROR_INTERNET))
        }

        return executeRequest( suspend {
            apiHelper.requestVerifyPurchase(TOKEN_PREFIX + tokenId, userId, purchaseTokenId, signature,
                originalJson, itemType)
        }, PurchaseResult(Result(RESULT_ERROR_INTERNET)))
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
    } catch (e: ErrnoException) {
        defaultAnswer
    }
}