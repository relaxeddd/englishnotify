package relaxeddd.englishnotify.ui.parse

import androidx.lifecycle.MutableLiveData
import com.google.android.material.radiobutton.MaterialRadioButton
import relaxeddd.englishnotify.R
import relaxeddd.englishnotify.common.*
import relaxeddd.englishnotify.model.repository.RepositoryUser
import relaxeddd.englishnotify.model.repository.RepositoryWord

class ViewModelParse : ViewModelBase(), ISelectCategory {

    val categories = MutableLiveData<List<CategoryItem>>(ArrayList())
    private var checkedItem: CategoryItem? = null

    init {
        updateCategories()
    }

    override fun getSelectedCategory() = checkedItem?.key
    override fun setSelectedCategory(item: CategoryItem?) {
        checkedItem = item
    }
    override fun onRadioButtonInit(category: String, radioButton: MaterialRadioButton) {}

    fun onClickedParseWords(wordsText: String, delimiterInner: String, delimiterWords: String, ownCategory: String) {
        val finalOwnTag = if (ownCategory.isBlank()) checkedItem?.key ?: "" else "!$ownCategory"
        val parsedWords = ArrayList<Word>()
        val notParsedWords = wordsText.split(delimiterWords)

        for (notParsedWord in notParsedWords) {
            var wordText = ""
            var transcription = ""
            var translation = ""
            val parts = notParsedWord.trim().split(delimiterInner)

            if (parts.size == 2) {
                wordText = parts[0].trim()
                translation = parts[1].trim()
            } else if (parts.size == 3) {
                wordText = parts[0].trim()
                transcription = parts[1].trim()
                translation = parts[2].trim()
            }

            if (wordText.isNotBlank()) {
                parsedWords.add(Word(wordText, wordText, translation, transcription, if (finalOwnTag.isNotBlank()) listOf(finalOwnTag) else emptyList(),
                        timestamp = System.currentTimeMillis(), isCreatedByUser = true, isOwnCategory = true))
            }
        }

        if (parsedWords.isEmpty()) {
            showToast(R.string.no_words_recognized)
        } else {
            RepositoryWord.getInstance().tempParsedWords.clear()
            RepositoryWord.getInstance().tempParsedWords.addAll(parsedWords)
            navigateEvent.value = Event(NAVIGATION_FRAGMENT_PARSED_WORDS)
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
}
