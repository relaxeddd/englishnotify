package relaxeddd.englishnotify.ui.dictionary_own

import androidx.lifecycle.MutableLiveData
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import relaxeddd.englishnotify.common.LEARN_STAGE_MAX
import relaxeddd.englishnotify.common.Word
import relaxeddd.englishnotify.model.repository.RepositoryUser
import relaxeddd.englishnotify.model.repository.RepositoryWord
import relaxeddd.englishnotify.ui.dictionary.ViewModelDictionary

class ViewModelDictionaryOwn(repositoryWord: RepositoryWord, repositoryUser: RepositoryUser) : ViewModelDictionary(repositoryWord, repositoryUser) {

    val isVisibleLoadingView = MutableLiveData<Boolean>(false)

    override fun filterWords(items: HashSet<Word>) : HashSet<Word> {
        return items.filter { it.isOwnCategory && it.learnStage != LEARN_STAGE_MAX }.toHashSet()
    }

    fun requestOwnWords() {
        if (!repositoryWord.isOwnWordsExists()) {
            isVisibleLoadingView.value = true
        }
        ioScope.launch {
            repositoryUser.requestOwnWords()
            withContext(Dispatchers.Main) {
                isVisibleLoadingView.value = false
            }
        }
    }
}