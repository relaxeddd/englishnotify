package com.example.vchechin.testapp.model.http

import com.example.vchechin.testapp.BuildConfig
import com.example.vchechin.testapp.common.*
import com.google.firebase.auth.FirebaseUser
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.*
import java.util.concurrent.TimeUnit

object ApiHelper {

    private val api: Api = Api()

    fun getInitObservable(pushToken: String) : Single<InitData> {
        val requestId = UUID.randomUUID().toString()
        val firebaseUser: FirebaseUser? = Cache.firebaseUser
        val userId = firebaseUser?.uid ?: ""
        var appVersion = BuildConfig.VERSION_CODE

        return getCheckUserObservable(firebaseUser)
            .flatMap{ tokenId -> api.requestInit(tokenId, requestId, userId, appVersion, pushToken) }
            .observeOn(AndroidSchedulers.mainThread())
    }

    fun getVerifyPurchaseObservable(purchaseTokenId: String, signature: String, originalJson: String, itemType: String) : Single<PurchaseResult> {
        val requestId = UUID.randomUUID().toString()
        val firebaseUser: FirebaseUser? = Cache.firebaseUser
        val userId = firebaseUser?.uid ?: ""

        return getCheckUserObservable(firebaseUser)
            .flatMap{ tokenId -> api.requestVerifyPurchase(tokenId, requestId, userId, purchaseTokenId, signature, originalJson, itemType) }
            .observeOn(AndroidSchedulers.mainThread())
    }

    fun getSetNicknameObservable(nickname: String) : Single<Result> {
        val requestId = UUID.randomUUID().toString()
        val firebaseUser: FirebaseUser? = Cache.firebaseUser
        val userId = firebaseUser?.uid ?: ""

        return getCheckUserObservable(firebaseUser)
            .flatMap{ tokenId -> api.requestSetNickname(tokenId, requestId, userId, nickname) }
            .observeOn(AndroidSchedulers.mainThread())
    }

    fun getSendFeedbackObservable(message: String, contactInfo: String) : Single<Result> {
        val requestId = UUID.randomUUID().toString()
        val firebaseUser: FirebaseUser? = Cache.firebaseUser
        val userId = firebaseUser?.uid ?: ""

        return getCheckUserObservable(firebaseUser)
            .flatMap{ tokenId -> api.requestSendFeedback(tokenId, requestId, userId, message, contactInfo) }
            .observeOn(AndroidSchedulers.mainThread())
    }

    //------------------------------------------------------------------------------------------------------------------
    private fun getCheckUserObservable(firebaseUser: FirebaseUser?) : Single<String> {
        return Single.create<String>({ e ->
            val tokenId = Cache.tokenId

            when {
                tokenId == null && firebaseUser != null -> {
                    firebaseUser.getIdToken(false).addOnCompleteListener {
                        if (it.isSuccessful) {
                            val receivedTokenId = it.result.token ?: ""
                            Cache.tokenId = receivedTokenId
                            e.onSuccess(receivedTokenId)
                        } else {
                            e.onError(Throwable(ERROR_NOT_AUTHORIZED))
                        }
                    }
                }
                tokenId != null -> {
                    e.onSuccess(tokenId)
                }
                else -> {
                    e.onError(Throwable(ERROR_NOT_AUTHORIZED))
                }
            }
        })
            .subscribeOn(Schedulers.io())
            .observeOn(Schedulers.io())
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
                .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                .addConverterFactory(GsonConverterFactory.create())
                .baseUrl(if (BuildConfig.IS_LOCAL_FUNCTIONS) URL_LOCAL else URL_FIREBASE)
                .build()

            apiHelper = retrofit.create(IApi::class.java)
        }

        fun requestInit(tokenId: String, requestId: String, userId: String, appVersion: Int, pushToken: String) : Single<InitData> {
            return apiHelper.requestInit(tokenPrefix + tokenId, requestId, userId, appVersion, pushToken)
        }

        fun requestVerifyPurchase(tokenId: String, requestId: String, userId: String, purchaseTokenId: String, signature: String,
                                  originalJson: String, itemType: String) : Single<PurchaseResult> {
            return apiHelper.requestVerifyPurchase(tokenPrefix + tokenId, requestId, userId, purchaseTokenId, signature, originalJson, itemType)
        }

        fun requestSetNickname(tokenId: String, requestId: String, userId: String, nickname: String) : Single<Result> {
            return apiHelper.requestSetNickname(tokenPrefix + tokenId, requestId, userId, nickname)
        }

        fun requestSendFeedback(tokenId: String, requestId: String, userId: String, message: String, contactInfo: String) : Single<Result> {
            return apiHelper.requestSendFeedback(tokenPrefix + tokenId, requestId, userId, message, contactInfo)
        }
    }
}