package relaxeddd.englishnotify.model.http

import androidx.annotation.Keep
import kotlinx.coroutines.Deferred
import org.json.JSONArray
import relaxeddd.englishnotify.common.*
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.Query

@Keep
interface IApi {

    @GET(FUNC_REQUEST_INIT)
    fun requestInitAsync(@Header("Authorization") idToken: String,
                         @Query("userId") userId: String,
                         @Query("appVersion") appVersion: Int,
                         @Query("pushToken") pushToken: String,
                         @Query("email") email: String,
                         @Query("learnStage0") learnStage0: JSONArray,
                         @Query("learnStage1") learnStage1: JSONArray,
                         @Query("learnStage2") learnStage2: JSONArray,
                         @Query("learnStage3") learnStage3: JSONArray) : Deferred<InitData?>

    @GET(FUNC_REQUEST_VERIFY_PURCHASE)
    fun requestVerifyPurchaseAsync(@Header("Authorization") idToken: String,
                                   @Query("userId") userId: String,
                                   @Query("purchaseTokenId") purchaseTokenId: String,
                                   @Query("signature") signature: String,
                                   @Query("originalJson") originalJson: String,
                                   @Query("itemType") itemType: String) : Deferred<PurchaseResult?>

    @GET(FUNC_REQUEST_SEND_FEEDBACK)
    fun requestSendFeedbackAsync(@Header("Authorization") idToken: String,
                                 @Query("userId") userId: String,
                                 @Query("message") message: String) : Deferred<Result?>

    @GET(FUNC_REQUEST_SEND_TEST_NOTIFICATION)
    fun requestSendTestNotificationAsync(@Header("Authorization") idToken: String,
                                         @Query("userId") userId: String) : Deferred<Result?>

    @GET(FUNC_REQUEST_UPDATE_USER)
    fun requestUpdateUserAsync(@Header("Authorization") idToken: String,
                               @Query("userId") userId: String,
                               @Query("receiveNotifications") receiveNotifications: Boolean,
                               @Query("notificationsTimeType") notificationsTimeType: Int,
                               @Query("learnLanguageType") learnLanguageType: Int,
                               @Query("selectedTag") selectedTag: String) : Deferred<UpdateUserResult?>

    @GET(FUNC_REQUEST_INSERT_OWN_WORD)
    fun requestInsertOwnWordAsync(@Header("Authorization") idToken: String,
                                  @Query("userId") userId: String,
                                  @Query("wordId") word: String,
                                  @Query("eng") eng: String,
                                  @Query("rus") rus: String,
                                  @Query("transcription") transcription: String) : Deferred<CreateWordResult?>

    @GET(FUNC_REQUEST_UPDATE_WORD_LEARN_STAGE)
    fun requestUpdateWordLearnStageAsync(@Header("Authorization") idToken: String,
                                         @Query("userId") userId: String,
                                         @Query("wordId") wordId: String,
                                         @Query("learnStage") learnStage: Int) : Deferred<Result?>

    @GET(FUNC_REQUEST_UPDATE_WORDS)
    fun requestUpdateWordsAsync(@Header("Authorization") idToken: String,
                                @Query("userId") userId: String,
                                @Query("wordIds") wordIds: JSONArray,
                                @Query("isDeleted") isDeleted: Boolean,
                                @Query("isOwnCategory") isOwnCategory: Boolean) : Deferred<Result?>

    @GET(FUNC_REQUEST_UPDATE_WORDS)
    fun requestDeleteWordsAsync(@Header("Authorization") idToken: String,
                                @Query("userId") userId: String,
                                @Query("wordIds") wordIds: JSONArray,
                                @Query("isDeleted") isDeleted: Boolean = true) : Deferred<Result?>

    @GET(FUNC_REQUEST_UPDATE_WORDS)
    fun requestSetIsOwnCategoryWordsAsync(@Header("Authorization") idToken: String,
                                          @Query("userId") userId: String,
                                          @Query("wordIds") wordIds: JSONArray,
                                          @Query("isOwnCategory") isOwnCategory: Boolean) : Deferred<Result?>

    @GET(FUNC_REQUEST_SET_NICKNAME)
    fun requestSetNicknameAsync(@Header("Authorization") idToken: String,
                                @Query("userId") userId: String,
                                @Query("name") name: String) : Deferred<Result?>
}