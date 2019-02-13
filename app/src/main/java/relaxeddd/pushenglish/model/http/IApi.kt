package relaxeddd.pushenglish.model.http

import androidx.annotation.Keep
import kotlinx.coroutines.Deferred
import relaxeddd.pushenglish.common.*
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
                    @Query("pushToken") pushToken: String) : Deferred<InitData>

    @GET(FUNC_REQUEST_VERIFY_PURCHASE)
    fun requestVerifyPurchase(@Header("Authorization") idToken: String,
                              @Query("requestId") requestId: String,
                              @Query("userId") userId: String,
                              @Query("purchaseTokenId") purchaseTokenId: String,
                              @Query("signature") signature: String,
                              @Query("originalJson") originalJson: String,
                              @Query("itemType") itemType: String) : Deferred<PurchaseResult>

    @GET(FUNC_REQUEST_SEND_FEEDBACK)
    fun requestSendFeedback(@Header("Authorization") idToken: String,
                            @Query("requestId") requestId: String,
                            @Query("userId") userId: String,
                            @Query("message") message: String,
                            @Query("contactInfo") contactInfo: String) : Deferred<Result>
}