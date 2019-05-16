package relaxeddd.englishnotify.ui.dictionary_exercises

import relaxeddd.englishnotify.common.EXERCISE
import relaxeddd.englishnotify.common.LEARN_STAGE_MAX
import relaxeddd.englishnotify.common.Word
import relaxeddd.englishnotify.model.repository.RepositoryUser
import relaxeddd.englishnotify.model.repository.RepositoryWord
import relaxeddd.englishnotify.ui.dictionary.ViewModelDictionary

class ViewModelDictionaryExercises(repositoryWord: RepositoryWord, repositoryUser: RepositoryUser) : ViewModelDictionary(repositoryWord, repositoryUser) {

    override fun filterWords(items: HashSet<Word>) : HashSet<Word> {
        return items.filter { it.type == EXERCISE && it.learnStage != LEARN_STAGE_MAX }.toHashSet()
    }
}