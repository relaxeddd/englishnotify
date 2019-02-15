package relaxeddd.pushenglish.model.http

import com.google.firebase.auth.FirebaseUser
import com.jakewharton.retrofit2.adapter.kotlin.coroutines.CoroutineCallAdapterFactory
import okhttp3.OkHttpClient
import relaxeddd.pushenglish.BuildConfig
import relaxeddd.pushenglish.common.*
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.*
import java.util.concurrent.TimeUnit

object ApiHelper {

    private val api: Api = Api()

    suspend fun requestInit(firebaseUser: FirebaseUser?, tokenId: String?, pushToken: String) : Resource<InitData> {
        val requestId = UUID.randomUUID().toString()
        val userId = firebaseUser?.uid ?: ""
        val appVersion = BuildConfig.VERSION_CODE

        return if (tokenId?.isNotEmpty() == true) {
            Resource(status = STATUS_OK, value = api.requestInit(tokenId, requestId, userId, appVersion, pushToken)
            )
        } else {
            Resource(errorStr = ERROR_TOKEN_NOT_INIT)
        }
    }

    /*fun getVerifyPurchaseObservable(purchaseTokenId: String, signature: String, originalJson: String, itemType: String) : Single<PurchaseResult> {
        val requestId = UUID.randomUUID().toString()
        val firebaseUser: FirebaseUser? = Cache.firebaseUser
        val userId = firebaseUser?.uid ?: ""

        return initUserTokenId(firebaseUser)
            .flatMap{ tokenId -> api.requestVerifyPurchase(tokenId, requestId, userId, purchaseTokenId, signature, originalJson, itemType) }
            .observeOn(AndroidSchedulers.mainThread())
    }

    fun getSendFeedbackObservable(message: String, contactInfo: String) : Single<Result> {
        val requestId = UUID.randomUUID().toString()
        val firebaseUser: FirebaseUser? = Cache.firebaseUser
        val userId = firebaseUser?.uid ?: ""

        return initUserTokenId(firebaseUser)
            .flatMap{ tokenId -> api.requestSendFeedback(tokenId, requestId, userId, message, contactInfo) }
            .observeOn(AndroidSchedulers.mainThread())
    }*/

    //------------------------------------------------------------------------------------------------------------------
    fun initUserTokenId(firebaseUser: FirebaseUser?, resultListener: (tokenId: Resource<String>) -> Unit) {
        firebaseUser?.getIdToken(false)?.addOnCompleteListener {
            if (it.isSuccessful) {
                val receivedTokenId = it.result?.token ?: ""
                resultListener(Resource(status = STATUS_OK, value = receivedTokenId))
            } else {
                resultListener(Resource(errorStr = ERROR_NOT_AUTHORIZED))
            }
        } ?: resultListener(Resource(errorStr = ERROR_NOT_AUTHORIZED))
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
        suspend fun requestInit(tokenId: String, requestId: String, userId: String, appVersion: Int, pushToken: String)
                : InitData {
            return apiHelper.requestInit(tokenPrefix + tokenId, requestId, userId, appVersion, pushToken).await()
        }

        suspend fun requestVerifyPurchase(tokenId: String, requestId: String, userId: String, purchaseTokenId: String,
                                          signature: String, originalJson: String, itemType: String) : PurchaseResult {
            return apiHelper.requestVerifyPurchase(tokenPrefix + tokenId, requestId, userId, purchaseTokenId,
                                                   signature, originalJson, itemType).await()
        }

        suspend fun requestSendFeedback(tokenId: String, requestId: String, userId: String, message: String,
                                        contactInfo: String) : Result {
            return apiHelper.requestSendFeedback(tokenPrefix + tokenId, requestId, userId, message,
                                                 contactInfo).await()
        }
    }
}