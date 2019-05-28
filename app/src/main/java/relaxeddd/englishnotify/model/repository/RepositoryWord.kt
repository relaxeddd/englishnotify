package relaxeddd.englishnotify.model.repository

import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.json.JSONArray
import org.json.JSONObject
import relaxeddd.englishnotify.App
import relaxeddd.englishnotify.R
import relaxeddd.englishnotify.common.*
import relaxeddd.englishnotify.model.db.AppDatabase
import relaxeddd.englishnotify.model.db.WordDao
import relaxeddd.englishnotify.model.http.ApiHelper
import java.util.*
import kotlin.collections.HashSet

class RepositoryWord private constructor(private val wordDao: WordDao) {

    companion object {
        @Volatile private var instance: RepositoryWord? = null
        fun getInstance(wordDao: WordDao = AppDatabase.getInstance(App.context).wordDao()) = instance ?: synchronized(this) {
            instance ?: RepositoryWord(wordDao).also { instance = it }
        }
    }

    private val ioScope = CoroutineScope(Dispatchers.IO)
    var words = wordDao.getAll()

    fun updateWord(word : Word) {
        wordDao.insertAll(word)
    }

    fun clearDictionary() {
        wordDao.deleteAll()
    }

    fun setWordLearnStageLocal(word: Word, progress: Int) {
        ioScope.launch {
            val saveWord = Word(word)
            saveWord.learnStage = progress
            saveWordLearnStage(saveWord.eng, progress)
            updateWord(saveWord)
        }
    }

    fun setWordLearnStage(word: Word, progress: Int) {
        ioScope.launch {
            val saveWord = Word(word)
            saveWord.learnStage = progress
            updateWord(saveWord)
            requestSetLearnStage(saveWord.eng, progress)
        }
    }

    fun isTrainingWordsExists() : Boolean {
        val words = this@RepositoryWord.words.value ?: ArrayList()

        for (word in words) {
            if (word.eng.split(" ").size <= 3 && word.learnStage != LEARN_STAGE_MAX) {
                return true
            }
        }

        return false
    }

    fun getOwnWords() : List<Word> {
        val allWords = words.value
        val ownWords = ArrayList<Word>()

        if (allWords != null) {
            for (word in allWords) {
                if (word.isOwnCategory) {
                    ownWords.add(word)
                }
            }
        }

        return ownWords
    }

    fun getWordCategories() : HashSet<String> {
        val categories = HashSet<String>()
        words.value?.forEach { it.tags.forEach { tag -> if (tag.isNotEmpty()) categories.add(tag) } }
        categories.add(ALL_APP_WORDS)
        return categories
    }

    fun getTrainingWordsByCategory(category: String) : ArrayList<Word> {
        val trainingWords = ArrayList<Word>()
        val words = this@RepositoryWord.words.value ?: ArrayList()

        for (word in words) {
            if ((word.tags.contains(category) || category == ALL_APP_WORDS) && word.learnStage != LEARN_STAGE_MAX
                && (word.eng.split(" ").size <= 3 || word.type == EXERCISE)) {
                trainingWords.add(word)
            }
        }

        return if (trainingWords.size >= 10) ArrayList(trainingWords.shuffled().subList(0, 10)) else ArrayList(trainingWords.shuffled())
    }

    private fun updateWords(words: List<Word>) {
        words.forEach {
            val existsWord = wordDao.findWordById(it.eng)

            if (it.timestamp == 0L) {
                it.timestamp = if (existsWord?.timestamp != 0L) (existsWord?.timestamp ?: System.currentTimeMillis()) else System.currentTimeMillis()
            }
            if (it.tags.contains(OWN)) {
                val tags = ArrayList(it.tags)
                tags.remove(OWN)
                it.tags = tags
                it.isOwnCategory = true
                it.isCreatedByUser = true
            }

            wordDao.insertAll(it)
        }
    }

    private fun saveWordLearnStage(wordId: String, learnStage: Int) {
        val learnStage0 = SharedHelper.getLearnStage0()
        val learnStage1 = SharedHelper.getLearnStage1()
        val learnStage2 = SharedHelper.getLearnStage2()
        val learnStage3 = SharedHelper.getLearnStage3()

        learnStage0.remove(wordId)
        learnStage1.remove(wordId)
        learnStage2.remove(wordId)
        learnStage3.remove(wordId)
        when(learnStage) {
            0 -> learnStage0.add(wordId)
            1 -> learnStage1.add(wordId)
            2 -> learnStage2.add(wordId)
            3 -> learnStage3.add(wordId)
        }
        SharedHelper.setLearnStage0(learnStage0)
        SharedHelper.setLearnStage1(learnStage1)
        SharedHelper.setLearnStage2(learnStage2)
        SharedHelper.setLearnStage3(learnStage3)
    }

    //------------------------------------------------------------------------------------------------------------------
    suspend fun requestSyncWords() {
        if (FirebaseAuth.getInstance().currentUser == null) {
            withContext(Dispatchers.Main) {
                showToast(getErrorString(RESULT_ERROR_UNAUTHORIZED))
            }
            return
        }

        val learnStage0 = JSONArray()
        for (learnStage in SharedHelper.getLearnStage0()) {
            if (learnStage.isNotEmpty()) {
                learnStage0.put(learnStage)
            }
        }
        val learnStage1 = JSONArray()
        for (learnStage in SharedHelper.getLearnStage1()) {
            if (learnStage.isNotEmpty()) {
                learnStage1.put(learnStage)
            }
        }
        val learnStage2 = JSONArray()
        for (learnStage in SharedHelper.getLearnStage2()) {
            if (learnStage.isNotEmpty()) {
                learnStage2.put(learnStage)
            }
        }
        val learnStage3 = JSONArray()
        for (learnStage in SharedHelper.getLearnStage3()) {
            if (learnStage.isNotEmpty()) {
                learnStage3.put(learnStage)
            }
        }

        val firebaseUser = RepositoryCommon.getInstance().firebaseUser
        val tokenId = RepositoryCommon.getInstance().tokenId

        val answer = ApiHelper.requestUpdateLearnStages(firebaseUser, tokenId, learnStage0, learnStage1, learnStage2, learnStage3)

        if (answer?.result?.isSuccess() == true && answer.words != null) {
            RepositoryWord.getInstance(AppDatabase.getInstance(App.context).wordDao()).updateWords(answer.words)
            SharedHelper.setLearnStage0(HashSet())
            SharedHelper.setLearnStage1(HashSet())
            SharedHelper.setLearnStage2(HashSet())
            SharedHelper.setLearnStage3(HashSet())
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

    suspend fun insertOwnCategoryWord(word: Word) : Boolean {
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
        wordJson.put(IS_CREATED_BY_USER, true)
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

    //------------------------------------------------------------------------------------------------------------------
    suspend fun addToOwn(wordId: String) : Boolean {
        return addToOwn(Collections.singletonList(wordId))
    }

    suspend fun addToOwn(wordIds: List<String>) : Boolean {
        return setIsOwnCategory(wordIds, true)
    }

    suspend fun removeFromOwn(wordId: String) : Boolean {
        return removeFromOwn(Collections.singletonList(wordId))
    }

    suspend fun removeFromOwn(wordIds: List<String>) : Boolean {
        return setIsOwnCategory(wordIds, false)
    }

    suspend fun setIsOwnCategory(wordIds: List<String>, isOwnCategory: Boolean) : Boolean {
        if (FirebaseAuth.getInstance().currentUser == null) {
            withContext(Dispatchers.Main) {
                showToast(getErrorString(RESULT_ERROR_UNAUTHORIZED))
            }
            return false
        }

        val firebaseUser = RepositoryCommon.getInstance().firebaseUser
        val tokenId = RepositoryCommon.getInstance().tokenId

        val answer = if (isOwnCategory) {
            ApiHelper.requestAddToOwnWords(firebaseUser, tokenId, wordIds)
        } else {
            ApiHelper.requestRemoveFromOwnWords(firebaseUser, tokenId, wordIds)
        }

        return when {
            answer?.isSuccess() == true -> {
                val wordDao = AppDatabase.getInstance(App.context).wordDao()

                for (wordId in wordIds) {
                    val word = wordDao.findWordById(wordId)

                    if (word != null) {
                        val saveWord = Word(word)
                        saveWord.isOwnCategory = false
                        if (saveWord.tags.contains(OWN)) {
                            val tags = ArrayList(saveWord.tags)
                            tags.remove(OWN)
                            saveWord.tags = tags
                            saveWord.isCreatedByUser = true
                        }
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
    suspend fun deleteWord(wordId: String) : Boolean {
        return deleteWords(Collections.singletonList(wordId))
    }

    suspend fun deleteWords(wordIds: List<String>) : Boolean {
        if (FirebaseAuth.getInstance().currentUser == null) {
            withContext(Dispatchers.Main) {
                showToast(getErrorString(RESULT_ERROR_UNAUTHORIZED))
            }
            return false
        }

        val firebaseUser = RepositoryCommon.getInstance().firebaseUser
        val tokenId = RepositoryCommon.getInstance().tokenId

        val answer = ApiHelper.requestDeleteWords(firebaseUser, tokenId, wordIds)

        return when {
            answer?.isSuccess() == true -> {
                val wordDao = AppDatabase.getInstance(App.context).wordDao()

                for (wordId in wordIds) {
                    val word = wordDao.findWordById(wordId)

                    if (word != null) {
                        val saveWord = Word(word)
                        saveWord.isDeleted = true
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
    private suspend fun requestSetLearnStage(wordId: String, learnStage: Int) {
        if (FirebaseAuth.getInstance().currentUser == null) {
            withContext(Dispatchers.Main) {
                showToast(getErrorString(RESULT_ERROR_UNAUTHORIZED))
            }
            return
        }

        val firebaseUser = RepositoryCommon.getInstance().firebaseUser
        val tokenId = RepositoryCommon.getInstance().tokenId

        val answer = ApiHelper.requestUpdateWordLearnStage(firebaseUser, tokenId, wordId, learnStage)

        if (answer?.isSuccess() != true) {
            saveWordLearnStage(wordId, learnStage)
            if (answer != null && !answer.isSuccess()) {
                withContext(Dispatchers.Main) {
                    showToast(getErrorString(answer))
                }
            } else {
                withContext(Dispatchers.Main) {
                    showToastLong(R.string.error_request)
                }
            }
        }
    }
}