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
import kotlin.collections.ArrayList
import kotlin.collections.HashMap
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
    private var tagsInfo: List<TagInfo> = ArrayList()

    fun clearDictionary() {
        wordDao.deleteAll()
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
        categories.add(OWN)
        categories.add(ALL_APP_WORDS)
        return categories
    }

    fun getTrainingWordsByCategory(category: String) : ArrayList<Word> {
        val trainingWords = ArrayList<Word>()
        val words = this@RepositoryWord.words.value ?: ArrayList()

        for (word in words) {
            if ((word.tags.contains(category) || category == ALL_APP_WORDS || (category == OWN && word.isOwnCategory))
                && word.learnStage != LEARN_STAGE_MAX && !word.isDeleted) {
                trainingWords.add(word)
            }
        }

        return if (trainingWords.size >= 10) ArrayList(trainingWords.shuffled().subList(0, 10)) else ArrayList(trainingWords.shuffled())
    }

    fun setWordLearnStage(word: Word, progress: Int, isRemoteSave: Boolean = true) {
        word.learnStage = progress
        ioScope.launch {
            val saveWord = Word(word)

            saveWord.learnStage = progress
            updateWord(saveWord)
            SharedHelper.setWordLearnStage(saveWord.id, progress)
            if (isRemoteSave) requestSetLearnStage(saveWord.id, progress)
        }
    }

    fun updateTagsInfo(tagsInfo: List<TagInfo>) {
        this.tagsInfo = tagsInfo
    }

    fun calculateTagsInfo() : List<TagInfo> {
        val tagsInfo = ArrayList(this.tagsInfo)
        val tagsInfoMap: HashMap<String, TagInfo> = HashMap()
        val words = words.value ?: ArrayList()

        for (tagInfo in tagsInfo) {
            tagInfo.learned = 0
            tagInfo.received = 0
            tagsInfoMap[tagInfo.key] = tagInfo
        }

        val tagInfoAll = tagsInfoMap[ALL_APP_WORDS] ?: TagInfo(ALL_APP_WORDS)
        val tagInfoAll5 = tagsInfoMap[ALL_APP_WORDS_WITHOUT_SIMPLE] ?: TagInfo(ALL_APP_WORDS_WITHOUT_SIMPLE)
        val tagInfoOwn = tagsInfoMap[OWN] ?: TagInfo(OWN)

        tagInfoAll.received = 0
        tagInfoAll.learned = 0
        tagInfoAll5.received = 0
        tagInfoAll5.learned = 0
        tagInfoOwn.received = 0
        tagInfoOwn.learned = 0

        if (!tagsInfo.contains(tagInfoOwn)) tagsInfo.add(tagInfoOwn)

        for (word in words) {
            if (word.isCreatedByUser) {
                tagInfoOwn.received++
                tagInfoOwn.total++
                if (word.learnStage == LEARN_STAGE_MAX) tagInfoOwn.learned++
            } else {
                tagInfoAll.received++
                if (word.level >= 5) tagInfoAll5.received++
                if (word.learnStage == LEARN_STAGE_MAX) {
                    tagInfoAll.learned++
                    if (word.level >= 5) tagInfoAll5.learned++
                }
            }

            for (tag in word.tags) {
                val tagInfo = tagsInfoMap[tag] ?: continue

                if (!word.isCreatedByUser) {
                    tagInfo.received++
                    if (word.learnStage == LEARN_STAGE_MAX) tagInfo.learned++
                }
            }
        }

        return ArrayList(tagsInfo)
    }

    fun insertWord(word : Word, wordDao: WordDao = this.wordDao) {
        wordDao.insertAll(word)
    }

    fun updateWords(words: List<Word>) {
        val existWords = this.words.value
        val idsSet = HashSet<String>()
        var isAllExists = true

        existWords?.forEach { idsSet.add(it.id) }
        words.forEach { if (!idsSet.contains(it.id)) isAllExists = false; }

        if (existWords == null || !isAllExists || existWords.size != words.size) wordDao.deleteAll()
        words.forEach {
            wordDao.insertAll(it)
        }
    }

    private fun updateWord(word : Word) {
        wordDao.insertAll(word)
    }

    //------------------------------------------------------------------------------------------------------------------
    suspend fun insertOwnCategoryWord(wordId: String, eng: String, rus: String, transcription: String) : Boolean {
        if (FirebaseAuth.getInstance().currentUser == null) {
            withContext(Dispatchers.Main) { showToast(getErrorString(RESULT_ERROR_UNAUTHORIZED)) }
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
                withContext(Dispatchers.Main) { showToast(getErrorString(answer.result)) }
                false
            }
            else -> {
                withContext(Dispatchers.Main) { showToast(R.string.error_request) }
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
            withContext(Dispatchers.Main) { showToast(getErrorString(RESULT_ERROR_UNAUTHORIZED)) }
            return false
        }

        val firebaseUser = RepositoryCommon.getInstance().firebaseUser
        val tokenId = RepositoryCommon.getInstance().tokenId

        val answer = ApiHelper.requestSetIsOwnCategoryWords(firebaseUser, tokenId, wordIds, isOwnCategory)

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
                withContext(Dispatchers.Main) { showToast(getErrorString(answer)) }
                false
            }
            else -> {
                withContext(Dispatchers.Main) { showToast(R.string.error_request) }
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
            withContext(Dispatchers.Main) { showToast(getErrorString(RESULT_ERROR_UNAUTHORIZED)) }
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
                withContext(Dispatchers.Main) { showToast(getErrorString(answer)) }
                false
            }
            else -> {
                withContext(Dispatchers.Main) { showToast(R.string.error_request) }
                false
            }
        }
    }

    //------------------------------------------------------------------------------------------------------------------
    private suspend fun requestSetLearnStage(wordId: String, learnStage: Int) {
        if (!isNetworkAvailable()) return
        if (FirebaseAuth.getInstance().currentUser == null) {
            withContext(Dispatchers.Main) { showToast(getErrorString(RESULT_ERROR_UNAUTHORIZED)) }
            return
        }

        val firebaseUser = RepositoryCommon.getInstance().firebaseUser
        val tokenId = RepositoryCommon.getInstance().tokenId

        val answer = ApiHelper.requestUpdateWordLearnStage(firebaseUser, tokenId, wordId, learnStage)

        if (answer?.isSuccess() == true) {
            SharedHelper.deleteWordLearnStage(wordId)
        } else {
            if (answer != null && !answer.isSuccess()) {
                withContext(Dispatchers.Main) { showToast(getErrorString(answer)) }
            } else {
                withContext(Dispatchers.Main) { showToast(R.string.error_request) }
            }
        }
    }
}