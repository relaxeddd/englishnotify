package relaxeddd.englishnotify.ui.word

import androidx.lifecycle.MutableLiveData
import com.google.android.material.radiobutton.MaterialRadioButton
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import relaxeddd.englishnotify.R
import relaxeddd.englishnotify.common.*
import relaxeddd.englishnotify.model.preferences.SharedHelper
import relaxeddd.englishnotify.model.repository.RepositoryCommon
import relaxeddd.englishnotify.model.repository.RepositoryUser
import relaxeddd.englishnotify.model.repository.RepositoryWord

class ViewModelWord : ViewModelBase(), ISelectCategory {

    val isEnabledOwnCategories = MutableLiveData(true)
    val categories = MutableLiveData<List<CategoryItem>>(ArrayList())
    var checkedItem: CategoryItem? = null
    var existsWordId = ""

    private var findWord: Word? = null
    private var updateEng: String = ""
    private var updateTranscription: String = ""
    private var updateRus: String = ""
    private var updateOwnTag: String = ""

    private var isTranslating = false
    private var lastTranslationText = ""
    private var lastTranslationResult = ""
    private var lastTranslationLanguage = ""

    init {
        updateCategories()
        updateOwnCategoriesAvailability()
    }

    override fun getSelectedCategory() = checkedItem?.key
    override fun setSelectedCategory(item: CategoryItem?) {
        checkedItem = item
    }
    override fun onRadioButtonInit(category: String, radioButton: MaterialRadioButton) {}

    fun createOwnWord(eng: String, transcription: String, rus: String, ownTag: String) {
        val finalOwnTag = if (ownTag.isBlank()) checkedItem?.key ?: "" else "!$ownTag"

        ioScope.launch {
            val existsWord = RepositoryWord.getInstance().getWord(eng)

            if (existsWordId.isNotEmpty()) {
                if (existsWord == null || existsWord.eng != eng || existsWord.rus != rus || existsWord.transcription != transcription
                        || (finalOwnTag.isNotBlank() && !existsWord.tags.contains(finalOwnTag))) {
                    RepositoryWord.getInstance().insertOwnCategoryWord(eng, eng, rus, transcription, finalOwnTag)
                    if (eng != this@ViewModelWord.existsWordId) {
                        RepositoryWord.getInstance().removeWordFromDb(this@ViewModelWord.existsWordId)
                    }
                }
            } else {
                if (existsWord != null) {
                    if (!existsWord.isCreatedByUser) {
                        withContext(Dispatchers.Main) {
                            navigateEvent.value = Event(NAVIGATION_WORD_EXISTS_ERROR)
                        }
                    } else {
                        findWord = existsWord
                        updateEng = eng
                        updateTranscription = transcription
                        updateRus = rus
                        updateOwnTag = finalOwnTag

                        withContext(Dispatchers.Main) {
                            navigateEvent.value = Event(NAVIGATION_WORD_EXISTS_DIALOG)
                        }
                    }
                    return@launch
                } else {
                    RepositoryWord.getInstance().insertOwnCategoryWord(eng, eng, rus, transcription, finalOwnTag)
                }
            }

            withContext(Dispatchers.Main) {
                navigateEvent.value = Event(NAVIGATION_ACTIVITY_BACK)
            }
        }
    }

    fun forceUpdateFindWord() {
        ioScope.launch {
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
            val tags = if (updateOwnTag.isNotEmpty()) listOf(updateOwnTag) else emptyList()

            val updateWord = Word(findWord.id, eng, rus, transcription, tags, findWord.sampleEng, findWord.sampleRus,
                findWord.v2, findWord.v3, findWord.timestamp, false, 0, findWord.type, findWord.isCreatedByUser,
                true, findWord.level)

            RepositoryWord.getInstance().updateWord(updateWord)
            if (updateEng != oldWordId) {
                RepositoryWord.getInstance().removeWordFromDb(oldWordId)
            }

            withContext(Dispatchers.Main) {
                navigateEvent.value = Event(NAVIGATION_ACTIVITY_BACK)
            }
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
        ioScope.launch {
            isTranslating = false

            val translation = RepositoryCommon.getInstance().requestTranslation(text, fromLanguage, toLanguage)

            lastTranslationText = text
            lastTranslationResult = translation
            lastTranslationLanguage = toLanguage
            uiScope.launch {
                callback(translation)
            }
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
        val list = ArrayList<CategoryItem>()
        val allTags = RepositoryWord.getInstance().getOwnWordCategories()

        for (tag in allTags) {
            list.add(CategoryItem(tag))
        }
        categories.postValue(list)
    }

    private fun updateOwnCategoriesAvailability() {
        val user = RepositoryUser.getInstance().liveDataUser.value
        isEnabledOwnCategories.value = user != null && user.isSubscribed()
    }
}
