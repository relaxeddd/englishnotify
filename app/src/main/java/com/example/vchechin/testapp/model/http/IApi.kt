package com.example.vchechin.testapp.model.http

import androidx.annotation.Keep
import com.example.vchechin.testapp.common.*
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.Query

@Keep
interface IApi {

    @GET(FUNC_REQUEST_INIT)
    fun requestInit(@Header("Authorization") idToken: String,
                    @Query("requestId") requestId: String,
                    @Query("userId") userId: String,
                    @Query("appVersion") appVersion: Int,
                    @Query("pushToken") pushToken: String) : Single<InitData>

    @GET(FUNC_REQUEST_VERIFY_PURCHASE)
    fun requestVerifyPurchase(@Header("Authorization") idToken: String,
                              @Query("requestId") requestId: String,
                              @Query("userId") userId: String,
                              @Query("purchaseTokenId") purchaseTokenId: String,
                              @Query("signature") signature: String,
                              @Query("originalJson") originalJson: String,
                              @Query("itemType") itemType: String) : Single<PurchaseResult>

    @GET(FUNC_REQUEST_SET_NICKNAME)
    fun requestSetNickname(@Header("Authorization") idToken: String,
                           @Query("requestId") requestId: String,
                           @Query("userId") userId: String,
                           @Query("nickname") nickname: String) : Single<Result>

    @GET(FUNC_REQUEST_SEND_FEEDBACK)
    fun requestSendFeedback(@Header("Authorization") idToken: String,
                            @Query("requestId") requestId: String,
                            @Query("userId") userId: String,
                            @Query("message") message: String,
                            @Query("contactInfo") contactInfo: String) : Single<Result>
}