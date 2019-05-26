package relaxeddd.englishnotify.model.repository

import androidx.lifecycle.MutableLiveData
import relaxeddd.englishnotify.model.http.ApiHelper
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.iid.FirebaseInstanceId
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.json.JSONArray
import org.json.JSONObject
import relaxeddd.englishnotify.App
import relaxeddd.englishnotify.common.*
import relaxeddd.englishnotify.R
import relaxeddd.englishnotify.model.db.AppDatabase
import relaxeddd.englishnotify.push.MyFirebaseMessagingService
import java.util.*
import kotlin.collections.ArrayList

class RepositoryUser private constructor() {

    companion object {
        @Volatile
        private var instance: RepositoryUser? = null
        fun getInstance() = instance ?: synchronized(this) {
            instance ?: RepositoryUser().also { instance = it }
        }
    }

    var liveDataUser = MutableLiveData<User>(null)
    val liveDataIsActualVersion = MutableLiveData<Boolean>(true)

    fun isAuthorized() = FirebaseAuth.getInstance().currentUser != null

    //------------------------------------------------------------------------------------------------------------------
    suspend fun initUser(listener: ListenerResult<Boolean>? = null) {
        RepositoryCommon.getInstance().initFirebase { isSuccess ->
            CoroutineScope(Dispatchers.Main).launch {
                if (!isSuccess) {
                    listener?.onResult(false)
                    return@launch
                }

                val firebaseUser = RepositoryCommon.getInstance().firebaseUser
                val tokenId = RepositoryCommon.getInstance().tokenId
                var pushToken = MyFirebaseMessagingService.pushToken

                if (pushToken.isEmpty()) {
                    pushToken = SharedHelper.getPushToken()
                }
                if (pushToken.isEmpty()) {
                    @Suppress("DEPRECATION")
                    pushToken = FirebaseInstanceId.getInstance().token ?: ""
                }
                if (pushToken.isEmpty()) {
                    showToast(R.string.error_push_token)
                    listener?.onResult(false)
                    return@launch
                }

                val answerInitData = ApiHelper.requestInit(firebaseUser, tokenId, pushToken)

                if (answerInitData?.result != null && answerInitData.result.isSuccess() && answerInitData.user?.userId?.isNotEmpty() == true) {
                    liveDataUser.value = answerInitData.user
                    SharedHelper.setLearnLanguageType(answerInitData.user.learnLanguageType)

                    if (!answerInitData.isActualVersion) {
                        liveDataIsActualVersion.value = answerInitData.isActualVersion
                    }
                    listener?.onResult(true)
                } else if (answerInitData?.result != null) {
                    showToast(getErrorString(answerInitData.result))
                    listener?.onResult(false)
                } else {
                    showToast(R.string.error_initialization)
                    listener?.onResult(false)
                }
            }
        }
    }

    suspend fun deleteUserInfo() {
        withContext(Dispatchers.Main) {
            liveDataUser.value = null
            showToast(R.string.logout_success)
        }
    }

    //------------------------------------------------------------------------------------------------------------------
    suspend fun setReceiveNotifications(isReceive: Boolean) {
        val user = User(liveDataUser.value ?: return)
        user.receiveNotifications = isReceive
        updateUser(user, liveDataUser.value)
    }

    suspend fun setLearnLanguageType(timeType: Int) {
        val user = User(liveDataUser.value ?: return)
        user.learnLanguageType = timeType
        updateUser(user, liveDataUser.value)
    }

    suspend fun setNotificationsTimeType(timeType: Int) {
        val user = User(liveDataUser.value ?: return)
        user.notificationsTimeType = timeType
        updateUser(user, liveDataUser.value)
    }

    suspend fun setSelectedTag(selectedTag: String) : Boolean {
        if (selectedTag.isNotEmpty()) {
            val user = User(liveDataUser.value ?: return false)
            user.selectedTag = selectedTag
            return updateUser(user, liveDataUser.value)
        } else {
            showToast(R.string.tags_should_not_be_empty)
            return false
        }
    }

    suspend fun sendTestNotification() {
        if (FirebaseAuth.getInstance().currentUser == null) {
            showToast(getErrorString(RESULT_ERROR_UNAUTHORIZED))
            return
        }

        val firebaseUser = RepositoryCommon.getInstance().firebaseUser
        val tokenId = RepositoryCommon.getInstance().tokenId

        val answer = ApiHelper.requestSendTestNotification(firebaseUser, tokenId)

        when {
            answer?.isSuccess() == true -> {
                showToastLong(R.string.test_notification_sent)
                val user = User(liveDataUser.value ?: return)
                user.testCount -= 1
                liveDataUser.postValue(user)
            }
            answer != null -> showToast(getErrorString(answer))
            else -> showToastLong(R.string.error_request)
        }
    }

    //------------------------------------------------------------------------------------------------------------------
    suspend fun requestOwnWords() {
        if (FirebaseAuth.getInstance().currentUser == null) {
            withContext(Dispatchers.Main) {
                showToast(getErrorString(RESULT_ERROR_UNAUTHORIZED))
            }
            return
        }

        val firebaseUser = RepositoryCommon.getInstance().firebaseUser
        val tokenId = RepositoryCommon.getInstance().tokenId

        val answer = ApiHelper.requestOwnWords(firebaseUser, tokenId)

        if (answer?.result?.isSuccess() == true && answer.words != null) {
            RepositoryWord.getInstance(AppDatabase.getInstance(App.context).wordDao()).updateOwsWords(answer.words)
        } else if (answer?.result != null) {
            withContext(Dispatchers.Main) {
                showToast(getErrorString(answer.result))
            }
        } else {
            withContext(Dispatchers.Main) {
                showToastLong(R.string.error_request)
            }
        }
    }

    suspend fun insertOwnWord(word: Word) : Boolean {
        if (FirebaseAuth.getInstance().currentUser == null) {
            withContext(Dispatchers.Main) {
                showToast(getErrorString(RESULT_ERROR_UNAUTHORIZED))
            }
            return false
        }
        if (word.isOwnCategory) {
            withContext(Dispatchers.Main) {
                showToast(getErrorString(RESULT_ERROR_OWN_WORD_TYPE))
            }
            return false
        }

        val firebaseUser = RepositoryCommon.getInstance().firebaseUser
        val tokenId = RepositoryCommon.getInstance().tokenId
        val wordJson = JSONObject()
        val tagsJson = JSONArray()

        for (tag in word.tags) {
            if (tag.isNotEmpty()) {
                tagsJson.put(tag)
            }
        }
        wordJson.put(ENG, word.eng)
        wordJson.put(RUS, word.rus)
        wordJson.put(TRANSCRIPTION, word.transcription)
        wordJson.put(TAGS, tagsJson)
        wordJson.put(TYPE, word.type)
        wordJson.put(TIMESTAMP, word.timestamp)
        wordJson.put(IS_CREATED_BY_USER, word.isCreatedByUser)
        wordJson.put(IS_OWN_CATEGORY, true)

        val answer = ApiHelper.requestInsertOwnWord(firebaseUser, tokenId, wordJson)

        return when {
            answer?.isSuccess() == true -> {
                val saveWord = Word(word)
                saveWord.isOwnCategory = true
                RepositoryWord.getInstance().updateWord(saveWord)
                true
            }
            answer != null -> {
                withContext(Dispatchers.Main) {
                    showToast(getErrorString(answer))
                }
                false
            }
            else -> {
                withContext(Dispatchers.Main) {
                    showToastLong(R.string.error_request)
                }
                false
            }
        }
    }

    suspend fun deleteOwnWord(wordId: String) : Boolean {
        return deleteOwnWords(Collections.singletonList(wordId))
    }

    suspend fun deleteOwnWords(wordIds: List<String>) : Boolean {
        if (FirebaseAuth.getInstance().currentUser == null) {
            withContext(Dispatchers.Main) {
                showToast(getErrorString(RESULT_ERROR_UNAUTHORIZED))
            }
            return false
        }

        val firebaseUser = RepositoryCommon.getInstance().firebaseUser
        val tokenId = RepositoryCommon.getInstance().tokenId
        val wordIdsJson = JSONArray()

        for (wordId in wordIds) {
            wordIdsJson.put(wordId)
        }

        val answer = ApiHelper.requestDeleteOwnWords(firebaseUser, tokenId, wordIdsJson)

        return when {
            answer?.isSuccess() == true -> {
                val wordDao = AppDatabase.getInstance(App.context).wordDao()

                for (wordId in wordIds) {
                    val word = wordDao.findWordById(wordId)

                    if (word != null) {
                        val saveWord = Word(word)

                        saveWord.isOwnCategory = false
                        RepositoryWord.getInstance(wordDao).updateWord(saveWord)
                    }
                }
                true
            }
            answer != null -> {
                withContext(Dispatchers.Main) {
                    showToast(getErrorString(answer))
                }
                false
            }
            else -> {
                withContext(Dispatchers.Main) {
                    showToastLong(R.string.error_request)
                }
                false
            }
        }
    }

    //------------------------------------------------------------------------------------------------------------------
    private suspend fun updateUser(user: User, oldUser: User?) : Boolean {
        liveDataUser.postValue(user)

        val firebaseUser = RepositoryCommon.getInstance().firebaseUser
        val tokenId = RepositoryCommon.getInstance().tokenId
        val updateResult = ApiHelper.requestUpdateUser(firebaseUser, tokenId, user)

        return if (updateResult != null && updateResult.result !== null && !updateResult.result.isSuccess()) {
            showToast(getErrorString(updateResult.result))
            liveDataUser.postValue(oldUser)
            false
        } else if (updateResult != null && updateResult.result !== null) {
            SharedHelper.setLearnLanguageType(user.learnLanguageType)
            true
        } else {
            showToast(R.string.error_update)
            false
        }
    }
}