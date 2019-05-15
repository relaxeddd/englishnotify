package relaxeddd.englishnotify.ui.dictionary_all

import relaxeddd.englishnotify.common.EXERCISE
import relaxeddd.englishnotify.common.Word
import relaxeddd.englishnotify.model.repository.RepositoryUser
import relaxeddd.englishnotify.model.repository.RepositoryWord
import relaxeddd.englishnotify.ui.dictionary.ViewModelDictionary

class ViewModelDictionaryAll(repositoryWord: RepositoryWord, repositoryUser: RepositoryUser) : ViewModelDictionary(repositoryWord, repositoryUser) {

    override fun filterWords(items: HashSet<Word>) : HashSet<Word> {
        return items.filter { it.type != EXERCISE }.toHashSet()
    }
}