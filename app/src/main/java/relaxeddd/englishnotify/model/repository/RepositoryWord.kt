package relaxeddd.englishnotify.model.repository

import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
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

    fun isTrainingWordsExists() : Boolean {
        val words = this@RepositoryWord.words.value ?: ArrayList()

        for (word in words) {
            if (word.learnStage != LEARN_STAGE_MAX) {
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
            if ((word.tags.contains(category) || category == ALL_APP_WORDS) && word.learnStage != LEARN_STAGE_MAX) {
                trainingWords.add(word)
            }
        }

        return if (trainingWords.size >= 10) ArrayList(trainingWords.shuffled().subList(0, 10)) else ArrayList(trainingWords.shuffled())
    }

    fun updateWords(words: List<Word>) {
        words.forEach {
            wordDao.insertAll(it)
        }
    }

    fun setWordLearnStageLocal(word: Word, progress: Int) {
        ioScope.launch {
            val saveWord = Word(word)
            saveWord.learnStage = progress
            SharedHelper.setWordLearnStage(saveWord.id, progress)
            updateWord(saveWord)
        }
    }

    fun setWordLearnStage(word: Word, progress: Int) {
        ioScope.launch {
            val saveWord = Word(word)
            saveWord.learnStage = progress
            updateWord(saveWord)
            SharedHelper.setWordLearnStage(saveWord.id, progress)
            requestSetLearnStage(saveWord.id, progress)
        }
    }

    //------------------------------------------------------------------------------------------------------------------
    suspend fun insertOwnCategoryWord(wordId: String, eng: String, rus: String, transcription: String) : Boolean {
        if (FirebaseAuth.getInstance().currentUser == null) {
            withContext(Dispatchers.Main) {
                showToast(getErrorString(RESULT_ERROR_UNAUTHORIZED))
            }
            return false
        }

        val firebaseUser = RepositoryCommon.getInstance().firebaseUser
        val tokenId = RepositoryCommon.getInstance().tokenId

        val answer = ApiHelper.requestInsertOwnWord(firebaseUser, tokenId, wordId, eng, rus, transcription)

        return when {
            answer?.result?.isSuccess() == true && answer.word != null -> {
                RepositoryWord.getInstance().updateWord(answer.word)
                true
            }
            answer?.result != null -> {
                withContext(Dispatchers.Main) {
                    showToast(getErrorString(answer.result))
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

    suspend fun removeFromOwn(wordId: String) : Boolean {
        return removeFromOwn(Collections.singletonList(wordId))
    }

    private suspend fun addToOwn(wordIds: List<String>) : Boolean {
        return setIsOwnCategory(wordIds, true)
    }

    private suspend fun removeFromOwn(wordIds: List<String>) : Boolean {
        return setIsOwnCategory(wordIds, false)
    }

    private suspend fun setIsOwnCategory(wordIds: List<String>, isOwnCategory: Boolean) : Boolean {
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
                for (wordId in wordIds) {
                    val word = wordDao.findWordById(wordId)

                    if (word != null) {
                        val saveWord = Word(word)
                        saveWord.isOwnCategory = isOwnCategory
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

        if (answer?.isSuccess() == true) {
            withContext(Dispatchers.Main) {
                SharedHelper.deleteWordLearnStage(wordId)
            }
        } else {
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