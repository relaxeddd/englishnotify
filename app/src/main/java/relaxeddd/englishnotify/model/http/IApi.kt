package relaxeddd.englishnotify.model.http

import androidx.annotation.Keep
import kotlinx.coroutines.Deferred
import relaxeddd.englishnotify.common.*
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
                    @Query("pushToken") pushToken: String,
                    @Query("email") email: String) : Deferred<InitData?>

    @GET(FUNC_REQUEST_VERIFY_PURCHASE)
    fun requestVerifyPurchase(@Header("Authorization") idToken: String,
                              @Query("requestId") requestId: String,
                              @Query("userId") userId: String,
                              @Query("purchaseTokenId") purchaseTokenId: String,
                              @Query("signature") signature: String,
                              @Query("originalJson") originalJson: String,
                              @Query("itemType") itemType: String) : Deferred<PurchaseResult?>

    @GET(FUNC_REQUEST_SEND_FEEDBACK)
    fun requestSendFeedback(@Header("Authorization") idToken: String,
                            @Query("requestId") requestId: String,
                            @Query("userId") userId: String,
                            @Query("message") message: String) : Deferred<Result?>

    @GET(FUNC_REQUEST_UPDATE_USER)
    fun requestUpdateUser(@Header("Authorization") idToken: String,
                          @Query("requestId") requestId: String,
                          @Query("userId") userId: String,
                          @Query("receiveNotifications") receiveNotifications: Boolean,
                          @Query("notificationsTimeType") notificationsTimeType: Int,
                          @Query("learnLanguageType") learnLanguageType: Int,
                          @Query("selectedTag") selectedTag: String) : Deferred<UpdateUserResult?>
}