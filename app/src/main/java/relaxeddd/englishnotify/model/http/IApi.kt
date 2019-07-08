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
                            @Query("email") email: String,
                            @Query("learnStage0") learnStage0: JSONArray,
                            @Query("learnStage1") learnStage1: JSONArray,
                            @Query("learnStage2") learnStage2: JSONArray,
                            @Query("learnStage3") learnStage3: JSONArray) : InitData?

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

    @GET(FUNC_REQUEST_INSERT_OWN_WORD)
    suspend fun requestInsertOwnWord(@Header("Authorization") idToken: String,
                                     @Query("userId") userId: String,
                                     @Query("wordId") word: String,
                                     @Query("eng") eng: String,
                                     @Query("rus") rus: String,
                                     @Query("transcription") transcription: String) : CreateWordResult?

    @GET(FUNC_REQUEST_UPDATE_WORD_LEARN_STAGE)
    suspend fun requestUpdateWordLearnStage(@Header("Authorization") idToken: String,
                                            @Query("userId") userId: String,
                                            @Query("wordId") wordId: String,
                                            @Query("learnStage") learnStage: Int) : Result?

    @GET(FUNC_REQUEST_UPDATE_WORDS)
    suspend fun requestUpdateWords(@Header("Authorization") idToken: String,
                                   @Query("userId") userId: String,
                                   @Query("wordIds") wordIds: JSONArray,
                                   @Query("isDeleted") isDeleted: Boolean,
                                   @Query("isOwnCategory") isOwnCategory: Boolean) : Result?

    @GET(FUNC_REQUEST_UPDATE_WORDS)
    suspend fun requestDeleteWords(@Header("Authorization") idToken: String,
                                   @Query("userId") userId: String,
                                   @Query("wordIds") wordIds: JSONArray,
                                   @Query("isDeleted") isDeleted: Boolean = true) : Result?

    @GET(FUNC_REQUEST_UPDATE_WORDS)
    suspend fun requestSetIsOwnCategoryWords(@Header("Authorization") idToken: String,
                                             @Query("userId") userId: String,
                                             @Query("wordIds") wordIds: JSONArray,
                                             @Query("isOwnCategory") isOwnCategory: Boolean) : Result?

    @GET(FUNC_REQUEST_SET_NICKNAME)
    suspend fun requestSetNickname(@Header("Authorization") idToken: String,
                                   @Query("userId") userId: String,
                                   @Query("name") name: String) : Result?
}