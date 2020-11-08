package relaxeddd.englishnotify.model.http

import androidx.annotation.Keep
import relaxeddd.englishnotify.common.*
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.POST
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

    @GET(FUNC_REQUEST_TRANSLATION)
    suspend fun requestTranslation(@Header("Authorization") idToken: String,
                                  @Query("userId") userId: String,
                                  @Query("translationText") translationText: String,
                                  @Query("translateFromLanguage") translateFromLanguage: String,
                                  @Query("translateToLanguage") translateToLanguage: String) : TranslationResult?

    @POST(FUNC_REQUEST_SAVE_WORDS)
    suspend fun requestSaveWords(@Header("Authorization") idToken: String,
                                 @Query("userId") userId: String,
                                 @Body words: List<Word>) : Result?

    @GET(FUNC_REQUEST_LOAD_WORDS)
    suspend fun requestLoadWords(@Header("Authorization") idToken: String,
                                 @Query("userId") userId: String) : WordsResult?
}
