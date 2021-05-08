package relaxeddd.englishnotify.ui.word

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.google.android.material.radiobutton.MaterialRadioButton
import kotlinx.coroutines.launch
import relaxeddd.englishnotify.R
import relaxeddd.englishnotify.common.*
import relaxeddd.englishnotify.model.preferences.SharedHelper
import relaxeddd.englishnotify.model.repository.RepositoryCommon
import relaxeddd.englishnotify.model.repository.RepositoryUser
import relaxeddd.englishnotify.model.repository.RepositoryWord

class ViewModelWord : ViewModelBase(), ISelectCategory {

    val isReadyToRateApp: Boolean
    val isEnabledOwnCategories = MutableLiveData(true)
    val categories = MutableLiveData<List<CategoryItem>>(ArrayList())
    private var checkedItem: CategoryItem? = null
    var existsWordId = ""
        set(value) {
            field = value
            updateCategories()
        }

    private var findWord: Word? = null
    private var updateEng: String = ""
    private var updateTranscription: String = ""
    private var updateRus: String = ""
    private var updateOwnTag: String? = ""

    private var isTranslating = false
    private var lastTranslationText = ""
    private var lastTranslationResult = ""
    private var lastTranslationLanguage = ""

    init {
        val user = RepositoryUser.getInstance().liveDataUser.value
        val isEnabledOwnCategoriesValue = user != null && user.isSubscribed()
        isEnabledOwnCategories.value = isEnabledOwnCategoriesValue

        val isRated = SharedHelper.isCancelledRateDialog()
        isReadyToRateApp = !isRated && RepositoryWord.getInstance().isEnoughLearnedWordsToRate()
    }

    override fun getSelectedCategory() = checkedItem?.key
    override fun setSelectedCategory(item: CategoryItem?) {
        checkedItem = item
    }
    override fun onRadioButtonInit(category: String, radioButton: MaterialRadioButton) {}

    fun createOwnWord(eng: String, transcription: String, rus: String, ownTag: String) {
        val finalOwnTag = if (isEnabledOwnCategories.value == true) {
            if (ownTag.isBlank()) checkedItem?.key ?: "" else "!$ownTag"
        } else {
            null
        }
        if (finalOwnTag != null) {
            SharedHelper.setLastOwnCategory(finalOwnTag)
        }

        viewModelScope.launch {
            var existsWord = RepositoryWord.getInstance().getWord(eng)

            if (existsWordId.isNotEmpty()) {
                existsWord = RepositoryWord.getInstance().getWord(existsWordId)
                val tags = when {
                    finalOwnTag == null -> null
                    finalOwnTag.isNotEmpty() -> listOf(finalOwnTag)
                    else -> emptyList()
                }

                if (existsWord == null || existsWord.eng != eng || existsWord.rus != rus || existsWord.transcription != transcription
                        || (tags != null && (tags.size != existsWord.tags.size || !tags.containsAll(existsWord.tags)))) {
                    RepositoryWord.getInstance().insertOwnCategoryWord(eng, eng, rus, transcription, tags ?: existsWord?.tags ?: emptyList())
                    if (eng != this@ViewModelWord.existsWordId) {
                        RepositoryWord.getInstance().removeWordFromDb(this@ViewModelWord.existsWordId)
                    }
                }
            } else {
                if (existsWord != null) {
                    if (!existsWord.isCreatedByUser) {
                        navigateEvent.value = Event(NAVIGATION_WORD_EXISTS_ERROR)
                    } else {
                        findWord = existsWord
                        updateEng = eng
                        updateTranscription = transcription
                        updateRus = rus
                        updateOwnTag = finalOwnTag

                        navigateEvent.value = Event(NAVIGATION_WORD_EXISTS_DIALOG)
                    }
                    return@launch
                } else {
                    val tags = if (finalOwnTag?.isNotEmpty() == true) listOf(finalOwnTag) else emptyList()
                    RepositoryWord.getInstance().insertOwnCategoryWord(eng, eng, rus, transcription, tags)
                }
            }

            navigateEvent.value = Event(NAVIGATION_ACTIVITY_BACK)
        }
    }

    fun forceUpdateFindWord() {
        viewModelScope.launch {
            val findWord = this@ViewModelWord.findWord
            val oldWordId = if (findWord != null) findWord.id else {
                showToast(R.string.error_update)
                return@launch
            }
            val eng = if (updateEng.isNotEmpty()) updateEng else {
                showToast(R.string.error_update)
                return@launch
            }
            val rus = if (updateEng.isNotEmpty()) updateRus else {
                showToast(R.string.error_update)
                return@launch
            }
            val transcription = if (updateTranscription.isNotEmpty()) updateTranscription else findWord.transcription
            val ownTag = updateOwnTag
            val tags = if (ownTag?.isNotEmpty() == true) listOf(ownTag) else emptyList()

            val updateWord = Word(findWord.id, eng, rus, transcription, tags, findWord.sampleEng, findWord.sampleRus,
                findWord.v2, findWord.v3, findWord.timestamp, false, 0, findWord.type, findWord.isCreatedByUser,
                true, findWord.level, 0)

            RepositoryWord.getInstance().updateWord(updateWord)
            if (updateEng != oldWordId) {
                RepositoryWord.getInstance().removeWordFromDb(oldWordId)
            }

            navigateEvent.value = Event(NAVIGATION_ACTIVITY_BACK)
        }
    }

    fun onClickTranslate(text: String, fromLanguage: String, toLanguage: String, callback: (String?) -> Unit) {
        val user = RepositoryUser.getInstance().liveDataUser.value

        if (user == null) {
            showToast(R.string.please_authorize)
            callback(null)
            return
        }
        if (!user.isSubscribed()) {
            navigateEvent.value = Event(NAVIGATION_DIALOG_SUBSCRIPTION_REQUIRED)
            callback(null)
            return
        }
        if (text.trim().isEmpty() || isTranslating) {
            callback(null)
            return
        }
        if (text == lastTranslationText && toLanguage == lastTranslationLanguage) {
            callback(lastTranslationResult)
            return
        }

        isTranslating = true
        viewModelScope.launch {
            isTranslating = false

            val translation = RepositoryCommon.getInstance().requestTranslation(text, fromLanguage, toLanguage)

            lastTranslationText = text
            lastTranslationResult = translation
            lastTranslationLanguage = toLanguage

            callback(translation)
        }
    }

    fun onClickOwnCategoryContent() {
        val user = RepositoryUser.getInstance().liveDataUser.value

        if (user == null) {
            showToast(R.string.please_authorize)
        } else if (isEnabledOwnCategories.value == false) {
            navigateEvent.value = Event(NAVIGATION_DIALOG_SUBSCRIPTION_REQUIRED)
        }
    }

    private fun updateCategories() {
        viewModelScope.launch {
            val list = ArrayList<CategoryItem>()
            val allTags = RepositoryWord.getInstance().getOwnWordCategories()

            for (tag in allTags) {
                list.add(CategoryItem(tag))
            }

            if (isEnabledOwnCategories.value == true) {
                val lastOwnCategory = CategoryItem(SharedHelper.getLastOwnCategory())

                if (existsWordId.isNotEmpty()) {
                    val existsWord = RepositoryWord.getInstance().getWord(existsWordId)
                    val existsWordOwnCategoryKey = existsWord?.tags?.find { isOwnCategory(it) }

                    if (existsWordOwnCategoryKey != null && list.contains(CategoryItem(existsWordOwnCategoryKey))) {
                        checkedItem = CategoryItem(existsWordOwnCategoryKey)
                    }
                } else if (lastOwnCategory.key.isNotEmpty()) {
                    if (list.contains(lastOwnCategory)) {
                        checkedItem = lastOwnCategory
                    }
                }
            }

            categories.postValue(list)
        }
    }
}
