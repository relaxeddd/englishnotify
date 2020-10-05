package relaxeddd.englishnotify.ui.training

import android.view.View
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Observer
import relaxeddd.englishnotify.App
import relaxeddd.englishnotify.R
import relaxeddd.englishnotify.common.*
import relaxeddd.englishnotify.model.repository.RepositoryWord
import kotlin.random.Random

class ViewModelTraining(private val repositoryWord: RepositoryWord) : ViewModelBase() {

    companion object {
        private const val STATE_ANSWER = 0
        private const val STATE_SUCCESS = 1
        private const val STATE_WRONG = 2

        const val RESULT_WRONG = 0
        const val RESULT_RIGHT = 1
        const val RESULT_LEARNED = 2
        const val RESULT_MEMORIZE = 3
    }

    var category = ALL_APP_WORDS
    var trainingType = TRAINING_ENG_TO_RUS
    var isCurrentEngTraining = true

    private var trainingWords = ArrayList<Word>()
    private var answers = ArrayList<String>()
    private var learned = 0
    private var total = 0

    var current = MutableLiveData(0)
    val wordsSize = MutableLiveData(0)

    val resultText = MutableLiveData("")
    val answer = MutableLiveData("")
    val wordText = MutableLiveData("")
    val transcription = MutableLiveData("")
    val translation = MutableLiveData("")
    val textButtonOk = MutableLiveData(App.context.getString(R.string.confirm))
    val textButtonOneMore = MutableLiveData(App.context.getString(R.string.one_more))
    val textStatistic = MutableLiveData("$learned/$total")
    val isVisibleTranscription = MutableLiveData(false)
    val isVisibleTranslation = MutableLiveData(false)
    val isVisibleButtonOk = MutableLiveData(true)
    val isVisibleButtonOneMore = MutableLiveData(false)
    val isVisibleInputAnswer = MutableLiveData(true)
    val isVisibleAnswer = MutableLiveData(false)
    val isVisibleResultText = MutableLiveData(false)
    val isVisibleWordProgress = MutableLiveData(false)
    val isVisibleButtonListen = MutableLiveData(SharedHelper.isListeningTraining())
    val isVisibleTextWord = MutableLiveData(!SharedHelper.isListeningTraining())
    val wordProgress = MutableLiveData(0)
    val resultAnimationType = MutableLiveData(RESULT_RIGHT)

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

    val clickListenerPlayWord = View.OnClickListener {
        navigateEvent.value = Event(NAVIGATION_PLAY_WORD)
    }

    val clickListenerListen = View.OnClickListener {
        navigateEvent.value = Event(NAVIGATION_PLAY_WORD_DEPENDS_ON_TRANSLATION)
    }

    private val currentObserver = Observer<Int> { currentIx ->
        if (trainingWords.size <= currentIx) {
            return@Observer
        }

        val isListeningTraining = SharedHelper.isListeningTraining()
        val currentResult = getResultLiveDataByIx(currentIx).value ?: 0
        val word = trainingWords[currentIx]
        isCurrentEngTraining = isEngTraining(word, currentResult)
        val eng = word.eng
        val rus = word.rus
        val transcriptionStr = if (word.transcription.isEmpty()) "-" else word.transcription
        val transcriptionValue = if (word.type != EXERCISE) "[$transcriptionStr]" else transcriptionStr

        wordText.value = if (isCurrentEngTraining) eng else rus
        translation.value = if (isCurrentEngTraining) rus else eng
        transcription.value = transcriptionValue
        wordProgress.value = 100 / 3 * word.learnStage
        resultText.value = if (currentResult == STATE_SUCCESS) getAppString(R.string.correct_answer) else getAppString(R.string.incorrect_answer)
        if (answers.size > currentIx) {
            var savedAnswer = answers[currentIx]
            if (savedAnswer.isEmpty()) savedAnswer = "-"
            answer.value = getAppString(R.string.your_answer, savedAnswer)
        } else {
            answer.value = getAppString(R.string.your_answer, "-")
        }

        isVisibleTranslation.value = currentResult != STATE_ANSWER
        isVisibleInputAnswer.value = currentResult == STATE_ANSWER
        isVisibleAnswer.value = currentResult != STATE_ANSWER
        isVisibleTranscription.value = currentResult != STATE_ANSWER
        isVisibleResultText.value = currentResult != STATE_ANSWER
        isVisibleWordProgress.value = currentResult != STATE_ANSWER
        isVisibleButtonListen.value = isListeningTraining && currentResult == STATE_ANSWER
        isVisibleTextWord.value = !isListeningTraining || currentResult != STATE_ANSWER

        if (isListeningTraining && currentResult == STATE_ANSWER) {
            navigateEvent.value = Event(NAVIGATION_PLAY_WORD_DEPENDS_ON_TRANSLATION)
        }

        updateButtonOk()
        updateButtonOneMore()

        /*if (currentResult == STATE_ANSWER) {
            navigateEvent.value = Event(NAVIGATION_SHOW_KEYBOARD)
        }*/
    }

    init {
        current.observeForever(currentObserver)
    }

    fun onBind() {
        val allWords = ArrayList(repositoryWord.words.value ?: ArrayList()).filter { !it.isDeleted }
        total = allWords.size
        learned = allWords.filter { it.learnStage == LEARN_STAGE_MAX }.size
        val words = repositoryWord.getTrainingWordsByCategory(category)

        trainingWords = words
        wordsSize.value = words.size
        current.value = 0
        textStatistic.value = "$learned/$total"
    }

    fun onClickOk(textAnswer: String) {
        val currentIx = current.value ?: 0
        val size = trainingWords.size
        val currentResult = getResultLiveDataByIx(currentIx).value ?: 0

        when {
            currentResult == STATE_ANSWER -> checkResult(currentIx, textAnswer)
            currentIx >= (size - 1) -> navigateEvent.value = Event(NAVIGATION_ACTIVITY_BACK)
            else -> current.value = currentIx + 1
        }
    }

    fun onClickOneMore() {
        clearState()
        onBind()
    }

    fun getCurrentWord() : Word? {
        val currentIx = current.value ?: trainingWords.size
        return if (currentIx < trainingWords.size) trainingWords[currentIx] else null
    }

    private fun checkResult(ix: Int, textAnswer: String) {
        if (ix >= trainingWords.size) {
            return
        }

        val word = trainingWords[ix]
        val answer = if (isCurrentEngTraining) word.rus else word.eng
        val isCorrectAnswer = isCorrectAnswer(textAnswer, answer)
        val result = if (isCorrectAnswer) STATE_SUCCESS else STATE_WRONG
        val currentIx = current.value ?: 0

        answers.add(textAnswer)
        getResultLiveDataByIx(ix).value = result
        if (result == STATE_SUCCESS) {
            val resultLearnStage = word.learnStage + 1

            repositoryWord.setWordLearnStage(word, resultLearnStage)
            if (currentIx >= trainingWords.size - 1) {
                current.value = currentIx
            } else {
                current.value = currentIx + 1
            }

            resultAnimationType.value = if (resultLearnStage == LEARN_STAGE_MAX) RESULT_LEARNED else RESULT_RIGHT
            navigateEvent.value = Event(NAVIGATION_ANIMATE_RESULT)
            if (resultLearnStage == LEARN_STAGE_MAX) {
                learned++
                textStatistic.value = "$learned/$total"
                navigateEvent.value = Event(NAVIGATION_ANIMATE_LEARNED_COUNT)
            }
        } else {
            if (SharedHelper.isHearAnswer() && !isCurrentEngTraining) {
                navigateEvent.value = Event(NAVIGATION_PLAY_WORD)
            }
            navigateEvent.value = Event(NAVIGATION_HIDE_KEYBOARD)
            repositoryWord.setWordLearnStage(word, 0)
            current.value = currentIx

            resultAnimationType.value = if (textAnswer.isEmpty()) RESULT_MEMORIZE else RESULT_WRONG
            navigateEvent.value = Event(NAVIGATION_ANIMATE_RESULT)
        }
    }

    private fun updateButtonOk() {
        val currentIx = current.value ?: 0
        val size = trainingWords.size
        val currentResult = getResultLiveDataByIx(currentIx).value ?: 0

        when {
            currentResult == STATE_ANSWER -> textButtonOk.value = getAppString(R.string.answer)
            currentIx == (size - 1) -> textButtonOk.value = getAppString(R.string.end)
            else -> textButtonOk.value = getAppString(R.string.next)
        }
        isVisibleButtonOk.value = (currentIx == size - 1) || (currentResult == STATE_ANSWER) || getResultLiveDataByIx(currentIx + 1).value == 0
    }

    private fun updateButtonOneMore() {
        val currentIx = current.value ?: 0
        val size = trainingWords.size
        val currentResult = getResultLiveDataByIx(currentIx).value ?: 0

        isVisibleButtonOneMore.value = currentIx == (size - 1) && currentResult != STATE_ANSWER
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

    private fun isEngTraining(word: Word, state: Int) = when (trainingType) {
        TRAINING_RUS_TO_ENG -> state != STATE_ANSWER || word.type == EXERCISE
        TRAINING_MIXED -> state != STATE_ANSWER || word.type == EXERCISE || Random.nextInt(2) == 0
        else -> true
    }

    private fun clearState() {
        answers.clear()

        resultText.value = ""
        answer.value = ""
        wordText.value = ""
        transcription.value = ""
        translation.value = ""
        isVisibleTranscription.value = false
        isVisibleTranslation.value = false
        isVisibleButtonOk.value = true
        isVisibleButtonOneMore.value = false
        isVisibleInputAnswer.value = true
        isVisibleAnswer.value = false
        isVisibleResultText.value = false
        wordProgress.value = 0

        result1.value = STATE_ANSWER
        result2.value = STATE_ANSWER
        result3.value = STATE_ANSWER
        result4.value = STATE_ANSWER
        result5.value = STATE_ANSWER
        result6.value = STATE_ANSWER
        result7.value = STATE_ANSWER
        result8.value = STATE_ANSWER
        result9.value = STATE_ANSWER
        result10.value = STATE_ANSWER
    }
}
