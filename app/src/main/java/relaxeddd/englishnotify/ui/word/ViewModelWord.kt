package relaxeddd.englishnotify.ui.word

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import relaxeddd.englishnotify.R
import relaxeddd.englishnotify.common.*
import relaxeddd.englishnotify.model.repository.RepositoryWord

class ViewModelWord : ViewModelBase() {

    var existsWordId = ""

    private var findWord: Word? = null
    private var updateEng: String = ""
    private var updateTranscription: String = ""
    private var updateRus: String = ""

    fun createOwnWord(eng: String, transcription: String, rus: String) {
        ioScope.launch {
            val existsWord = RepositoryWord.getInstance().getWord(eng)

            if (existsWordId.isNotEmpty()) {
                if (existsWord == null || existsWord.eng != eng || existsWord.rus != rus || existsWord.transcription != transcription) {
                    RepositoryWord.getInstance().insertOwnCategoryWord(eng, eng, rus, transcription)
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

                        withContext(Dispatchers.Main) {
                            navigateEvent.value = Event(NAVIGATION_WORD_EXISTS_DIALOG)
                        }
                    }
                    return@launch
                } else {
                    RepositoryWord.getInstance().insertOwnCategoryWord(eng, eng, rus, transcription)
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

            val updateWord = Word(findWord.id, eng, rus, transcription, findWord.tags, findWord.sampleEng, findWord.sampleRus,
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
}
