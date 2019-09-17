package relaxeddd.englishnotify.model.http

import androidx.annotation.Keep
import org.json.JSONArray
import relaxeddd.englishnotify.common.*
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.Query

@Keep
interface IApi {

    @GET(FUNC_REQUEST_INIT)
    suspend fun requestInit(@Header("Authorization") idToken: String,
                            @Query("userId") userId: String,
                            @Query("appVersion") appVersion: Int,
                            @Query("pushToken") pushToken: String,
                            @Query("email") email: String) : InitData?

    @GET(FUNC_REQUEST_VERIFY_PURCHASE)
    suspend fun requestVerifyPurchase(@Header("Authorization") idToken: String,
                                      @Query("userId") userId: String,
                                      @Query("purchaseTokenId") purchaseTokenId: String,
                                      @Query("signature") signature: String,
                                      @Query("originalJson") originalJson: String,
                                      @Query("itemType") itemType: String) : PurchaseResult?

    @GET(FUNC_REQUEST_SEND_FEEDBACK)
    suspend fun requestSendFeedback(@Header("Authorization") idToken: String,
                                    @Query("userId") userId: String,
                                    @Query("message") message: String) : Result?

    @GET(FUNC_REQUEST_SEND_TEST_NOTIFICATION)
    suspend fun requestSendTestNotification(@Header("Authorization") idToken: String,
                                            @Query("userId") userId: String) : Result?

    @GET(FUNC_REQUEST_UPDATE_USER)
    suspend fun requestUpdateUser(@Header("Authorization") idToken: String,
                                  @Query("userId") userId: String,
                                  @Query("receiveNotifications") receiveNotifications: Boolean,
                                  @Query("notificationsTimeType") notificationsTimeType: Int,
                                  @Query("learnLanguageType") learnLanguageType: Int,
                                  @Query("selectedTag") selectedTag: String) : UpdateUserResult?
}