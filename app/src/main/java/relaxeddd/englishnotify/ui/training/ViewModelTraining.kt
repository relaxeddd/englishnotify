package relaxeddd.englishnotify.ui.training

import android.view.View
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Observer
import kotlinx.coroutines.launch
import relaxeddd.englishnotify.App
import relaxeddd.englishnotify.R
import relaxeddd.englishnotify.common.*
import relaxeddd.englishnotify.model.repository.RepositoryWord

class ViewModelTraining(private val repositoryWord: RepositoryWord) : ViewModelBase() {

    companion object {
        private const val STATE_ANSWER = 0
        private const val STATE_SUCCESS = 1
        private const val STATE_WRONG = 2
    }

    var category = ALL_APP_WORDS
    var trainingType = TRAINING_ENG_TO_RUS
    var trainingWords = ArrayList<Word>()
    var answers = ArrayList<String>()

    var current = MutableLiveData(0)
    val wordsSize = MutableLiveData(0)

    val resultText = MutableLiveData<String>("")
    val answer = MutableLiveData<String>("")
    val wordText = MutableLiveData<String>("")
    val transcription = MutableLiveData<String>("")
    val translation = MutableLiveData<String>("")
    val textButtonOk = MutableLiveData<String>(App.context.getString(R.string.confirm))
    val isVisibleTranscription = MutableLiveData<Boolean>(trainingType == TRAINING_ENG_TO_RUS)
    val isVisibleTranslation = MutableLiveData<Boolean>(false)
    val isVisibleButtonOk = MutableLiveData<Boolean>(true)
    val isVisibleInputAnswer = MutableLiveData<Boolean>(true)
    val isVisibleAnswer = MutableLiveData<Boolean>(false)
    val isVisibleResultText = MutableLiveData<Boolean>(false)

    val result1 = MutableLiveData(STATE_ANSWER)
    val result2 = MutableLiveData(STATE_ANSWER)
    val result3 = MutableLiveData(STATE_ANSWER)
    val result4 = MutableLiveData(STATE_ANSWER)
    val result5 = MutableLiveData(STATE_ANSWER)
    val result6 = MutableLiveData(STATE_ANSWER)
    val result7 = MutableLiveData(STATE_ANSWER)
    val result8 = MutableLiveData(STATE_ANSWER)
    val result9 = MutableLiveData(STATE_ANSWER)
    val result10 = MutableLiveData(STATE_ANSWER)

    val clickListenerRadioButton1 = View.OnClickListener { current.value = 0 }
    val clickListenerRadioButton2 = View.OnClickListener { current.value = 1 }
    val clickListenerRadioButton3 = View.OnClickListener { current.value = 2 }
    val clickListenerRadioButton4 = View.OnClickListener { current.value = 3 }
    val clickListenerRadioButton5 = View.OnClickListener { current.value = 4 }
    val clickListenerRadioButton6 = View.OnClickListener { current.value = 5 }
    val clickListenerRadioButton7 = View.OnClickListener { current.value = 6 }
    val clickListenerRadioButton8 = View.OnClickListener { current.value = 7 }
    val clickListenerRadioButton9 = View.OnClickListener { current.value = 8 }
    val clickListenerRadioButton10 = View.OnClickListener { current.value = 9 }

    private val currentObserver = Observer<Int> { currentIx ->
        if (trainingWords.size <= currentIx) {
            return@Observer
        }

        val isEngType = trainingType == TRAINING_ENG_TO_RUS
        val eng = trainingWords[currentIx].eng
        val rus = trainingWords[currentIx].rus
        val transcriptionValue = trainingWords[currentIx].transcription
        val currentResult = getResultLiveDataByIx(currentIx).value ?: 0

        wordText.value = if (isEngType) eng else rus
        translation.value = if (isEngType) rus else eng
        transcription.value = transcriptionValue
        resultText.value = if (currentResult == STATE_SUCCESS) getString(R.string.correct_answer) else getString(R.string.incorrect_answer)
        if (answers.size > currentIx) {
            var savedAnswer = answers[currentIx]
            if (savedAnswer.isEmpty()) savedAnswer = "-"
            answer.value = getString(R.string.your_answer, savedAnswer)
        } else {
            answer.value = getString(R.string.your_answer, "-")
        }

        isVisibleTranslation.value = currentResult != STATE_ANSWER
        isVisibleInputAnswer.value = currentResult == STATE_ANSWER
        isVisibleAnswer.value = currentResult != STATE_ANSWER
        isVisibleTranscription.value = trainingType == TRAINING_ENG_TO_RUS || currentResult != STATE_ANSWER
        isVisibleResultText.value = currentResult != STATE_ANSWER

        updateButtonOk()
    }

    init {
        current.observeForever(currentObserver)
    }

    //------------------------------------------------------------------------------------------------------------------
    fun onBind() {
        val words = repositoryWord.getTrainingWordsByCategory(category)
        trainingWords = words
        wordsSize.value = words.size
        current.value = 0
    }

    fun onClickOk(textAnswer: String) {
        val currentIx = current.value ?: 0
        val size = trainingWords.size
        val currentResult = getResultLiveDataByIx(currentIx).value ?: 0

        if (currentResult == STATE_ANSWER) {
            checkResult(currentIx, textAnswer)
        } else if (currentIx == (size - 1)) {
            navigateEvent.value = Event(NAVIGATION_ACTIVITY_BACK_TWICE)
        } else {
            current.value = currentIx + 1
        }
    }

    private fun checkResult(ix: Int, textAnswer: String) {
        val word = trainingWords[ix]
        val answer = if (trainingType == TRAINING_ENG_TO_RUS) word.rus else word.eng
        val isCorrectAnswer = isCorrectAnswer(textAnswer, answer)
        val result = if (isCorrectAnswer) STATE_SUCCESS else STATE_WRONG
        val currentIx = current.value ?: 0

        answers.add(textAnswer)
        getResultLiveDataByIx(ix).value = result
        if (result == STATE_SUCCESS) {
            changeWordProgress(word, word.learnStage + 1)
            if (currentIx >= trainingWords.size - 1) {
                current.value = currentIx
            } else {
                current.value = currentIx + 1
            }
        } else {
            changeWordProgress(word, 0)
            current.value = currentIx
        }
    }

    private fun updateButtonOk() {
        val currentIx = current.value ?: 0
        val size = trainingWords.size
        val currentResult = getResultLiveDataByIx(currentIx).value ?: 0

        if (currentResult == STATE_ANSWER) {
            textButtonOk.value = getString(R.string.answer)
        } else if (currentIx == (size - 1)) {
            textButtonOk.value = getString(R.string.end)
        } else  {
            textButtonOk.value = getString(R.string.next)
        }
        isVisibleButtonOk.value = (currentIx == size - 1) || (currentResult == STATE_ANSWER) || getResultLiveDataByIx(currentIx + 1).value == 0
    }

    private fun getResultLiveDataByIx(ix: Int) = when(ix) {
        0 -> result1
        1 -> result2
        2 -> result3
        3 -> result4
        4 -> result5
        5 -> result6
        6 -> result7
        7 -> result8
        8 -> result9
        else -> result10
    }

    private fun changeWordProgress(word: Word, progress: Int) {
        ioScope.launch {
            val saveWord = Word(word)
            saveWord.learnStage = progress
            repositoryWord.updateWord(saveWord)
        }
    }
}