package relaxeddd.englishnotify.ui.word

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.google.android.material.radiobutton.MaterialRadioButton
import kotlinx.coroutines.launch
import relaxeddd.englishnotify.App
import relaxeddd.englishnotify.R
import relaxeddd.englishnotify.common.CategoryItem
import relaxeddd.englishnotify.common.ISelectCategory
import relaxeddd.englishnotify.common.NAVIGATION_ACTIVITY_BACK
import relaxeddd.englishnotify.common.NAVIGATION_WORD_EXISTS_DIALOG
import relaxeddd.englishnotify.common.NAVIGATION_WORD_EXISTS_ERROR
import relaxeddd.englishnotify.common.showToast
import relaxeddd.englishnotify.domain_words.entity.Word
import relaxeddd.englishnotify.domain_words.repository.RepositoryWords
import relaxeddd.englishnotify.preferences.Preferences
import relaxeddd.englishnotify.view_base.ViewModelBase
import relaxeddd.englishnotify.view_base.models.Event

class ViewModelWord : ViewModelBase(), ISelectCategory {

    private val prefs = Preferences.getInstance()

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

    override fun getSelectedCategory() = checkedItem?.key
    override fun setSelectedCategory(item: CategoryItem?) {
        checkedItem = item
    }
    override fun onRadioButtonInit(category: String, radioButton: MaterialRadioButton) {}

    fun createOwnWord(eng: String, transcription: String, rus: String, ownTag: String) {
        val finalOwnTag = ownTag.ifBlank { checkedItem?.key ?: "" }

        if (finalOwnTag.isNotBlank()) {
            prefs.setLastOwnCategory(finalOwnTag)
        }
        val tags = when {
            finalOwnTag.isNotEmpty() -> listOf(finalOwnTag)
            else -> emptyList()
        }

        viewModelScope.launch {
            var existsWord = RepositoryWords.getInstance(App.context).getWord(eng)

            if (existsWordId.isNotEmpty()) {
                existsWord = RepositoryWords.getInstance(App.context).getWord(existsWordId)
                val updateIsRequired = existsWord == null || existsWord.eng != eng || existsWord.rus != rus
                        || existsWord.transcription != transcription
                        || tags.size != existsWord.tags.size || tags.firstOrNull() != existsWord.tags.firstOrNull()

                if (updateIsRequired) {
                    RepositoryWords.getInstance(App.context).insertOwnCategoryWord(eng, eng, rus, transcription, tags)

                    if (eng != this@ViewModelWord.existsWordId) {
                        RepositoryWords.getInstance(App.context).removeWordFromDb(this@ViewModelWord.existsWordId)
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
                    RepositoryWords.getInstance(App.context).insertOwnCategoryWord(eng, eng, rus, transcription, tags)
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

            RepositoryWords.getInstance(App.context).updateWord(updateWord)
            if (updateEng != oldWordId) {
                RepositoryWords.getInstance(App.context).removeWordFromDb(oldWordId)
            }

            navigateEvent.value = Event(NAVIGATION_ACTIVITY_BACK)
        }
    }

    private fun updateCategories() {
        viewModelScope.launch {
            val list = ArrayList<CategoryItem>()
            val allTags = RepositoryWords.getInstance(App.context).getOwnWordCategories()

            for (tag in allTags) {
                list.add(CategoryItem(tag))
            }

            val lastCategory = CategoryItem(prefs.getLastOwnCategory())

            if (existsWordId.isNotEmpty()) {
                val existsWord = RepositoryWords.getInstance(App.context).getWord(existsWordId)
                val existsWordCategoryKey = existsWord?.tags?.firstOrNull()

                if (existsWordCategoryKey != null && list.contains(CategoryItem(existsWordCategoryKey))) {
                    checkedItem = CategoryItem(existsWordCategoryKey)
                }
            } else if (lastCategory.key.isNotEmpty()) {
                if (list.contains(lastCategory)) {
                    checkedItem = lastCategory
                }
            }

            categories.postValue(list)
        }
    }
}
