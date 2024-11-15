package relaxeddd.englishnotify.ui.training

import android.content.Context
import android.view.View
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Observer
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch
import relaxeddd.englishnotify.R
import relaxeddd.englishnotify.common.*
import relaxeddd.englishnotify.domain_words.entity.Word
import relaxeddd.englishnotify.domain_words.repository.RepositoryWords
import relaxeddd.englishnotify.preferences.Preferences
import relaxeddd.englishnotify.preferences.utils.ALL_APP_WORDS
import relaxeddd.englishnotify.view_base.ViewModelBase
import relaxeddd.englishnotify.view_base.models.Event
import javax.inject.Inject
import kotlin.random.Random

class ViewModelTraining @Inject constructor(
    private val context: Context,
    prefs: Preferences,
    private val repositoryWords: RepositoryWords,
) : ViewModelBase() {

    companion object {
        private const val STATE_ANSWER = 0
        private const val STATE_SUCCESS = 1
        private const val STATE_WRONG = 2

        const val RESULT_WRONG = 0
        const val RESULT_RIGHT = 1
        const val RESULT_LEARNED = 2
        const val RESULT_MEMORIZE = 3
    }

    private val learnStageMax = prefs.getTrueAnswersToLearn()
    private val isEnabledSecondaryProgress = prefs.isEnabledSecondaryProgress()
    private val isCheckLearnedWords = prefs.isCheckLearnedWords()
    private val isListeningTraining = prefs.isListeningTraining()
    private val isShowProgressInTraining = prefs.isShowProgressInTraining()
    private val isHearAnswer = prefs.isHearAnswer()

    var category = ALL_APP_WORDS
    var trainingType = TRAINING_ENG_TO_RUS
    var isCurrentEngTraining = true

    private var trainingWords = ArrayList<Word>()
    private var answers = ArrayList<String>()
    private var learned = 0
    private var total = 0
    private val savedMixedTrainingTypes = HashMap<Int, Int>()

    var current = MutableLiveData(0)
    val wordsSize = MutableLiveData(0)

    val resultText = MutableLiveData("")
    val answer = MutableLiveData("")
    val wordText = MutableLiveData("")
    val transcription = MutableLiveData("")
    val translation = MutableLiveData("")
    val textButtonOk = MutableLiveData(context.getString(R.string.confirm))
    val textButtonOneMore = MutableLiveData(context.getString(R.string.one_more))
    val textStatistic = MutableLiveData("$learned/$total")
    val isVisibleTranscription = MutableLiveData(false)
    val isVisibleTranslation = MutableLiveData(false)
    val isVisibleButtonOk = MutableLiveData(true)
    val isVisibleButtonOneMore = MutableLiveData(false)
    val isVisibleInputAnswer = MutableLiveData(true)
    val isVisibleAnswer = MutableLiveData(false)
    val isVisibleResultText = MutableLiveData(false)
    val isVisibleWordProgress = MutableLiveData(false)
    val isVisibleWordProgressSecondary = MutableLiveData(false)
    val isVisibleButtonListen = MutableLiveData(prefs.isListeningTraining())
    val isVisibleTextWord = MutableLiveData(!prefs.isListeningTraining())
    val wordProgress = MutableLiveData(0)
    val wordProgressSecondary = MutableLiveData(0)
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

        val currentResult = getResultLiveDataByIx(currentIx).value ?: 0
        val word = trainingWords[currentIx]
        val eng = word.eng
        val rus = word.rus
        val transcriptionStr = if (word.transcription.isEmpty()) "-" else word.transcription
        val transcriptionValue = if (word.type != EXERCISE) "[$transcriptionStr]" else transcriptionStr

        if (trainingType == TRAINING_MIXED && savedMixedTrainingTypes.containsKey(currentIx)) {
            isCurrentEngTraining = savedMixedTrainingTypes[currentIx] == TRAINING_ENG_TO_RUS
        } else {
            isCurrentEngTraining = isEngTraining(word, currentResult, currentIx)
        }

        wordText.value = if (isCurrentEngTraining) eng else rus
        translation.value = if (isCurrentEngTraining) rus else eng
        transcription.value = transcriptionValue
        wordProgress.value = word.getLearnProgress(learnStageMax)
        wordProgressSecondary.value = word.getLearnProgressSecondary(learnStageMax)
        resultText.value = if (currentResult == STATE_SUCCESS) {
            getAppString(context, R.string.correct_answer)
        } else {
            getAppString(context, R.string.incorrect_answer)
        }
        if (answers.size > currentIx) {
            var savedAnswer = answers[currentIx]
            if (savedAnswer.isEmpty()) savedAnswer = "-"
            answer.value = getAppString(context, R.string.your_answer, savedAnswer)
        } else {
            answer.value = getAppString(context, R.string.your_answer, "-")
        }

        isVisibleTranslation.value = currentResult != STATE_ANSWER
        isVisibleInputAnswer.value = currentResult == STATE_ANSWER
        isVisibleAnswer.value = currentResult != STATE_ANSWER
        isVisibleTranscription.value = currentResult != STATE_ANSWER
        isVisibleResultText.value = currentResult != STATE_ANSWER
        isVisibleWordProgress.value = currentResult != STATE_ANSWER || isShowProgressInTraining
        isVisibleWordProgressSecondary.value = (currentResult != STATE_ANSWER || isShowProgressInTraining) && isEnabledSecondaryProgress
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
        val allWords = ArrayList(repositoryWords.words.value ?: ArrayList()).filter { !it.isDeleted }
        total = allWords.size
        learned = allWords.filter { it.isLearned(isEnabledSecondaryProgress, learnStageMax) }.size
        val words = repositoryWords.getTrainingWordsByCategory(category, isCheckLearnedWords, trainingType)

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
            else -> {
                current.value = currentIx + 1
                navigateEvent.value = Event(NAVIGATION_SHOW_KEYBOARD)
            }
        }
    }

    fun onClickOneMore() {
        clearState()
        onBind()
        navigateEvent.value = Event(NAVIGATION_SHOW_KEYBOARD)
    }

    fun getCurrentWord() : Word? {
        val currentIx = current.value ?: trainingWords.size
        return if (currentIx < trainingWords.size) trainingWords[currentIx] else null
    }

    private fun checkResult(ix: Int, textAnswer: String) = viewModelScope.launch {
        if (ix >= trainingWords.size) {
            return@launch
        }

        val word = trainingWords[ix]
        val answer = if (isCurrentEngTraining) word.rus else word.eng
        val isCorrectAnswer = isCorrectAnswer(textAnswer, answer)
        val result = if (isCorrectAnswer) STATE_SUCCESS else STATE_WRONG
        val currentIx = current.value ?: 0
        val isSecondaryProgress = isEnabledSecondaryProgress && !isCurrentEngTraining

        answers.add(textAnswer)
        getResultLiveDataByIx(ix).value = result
        if (result == STATE_SUCCESS) {
            val resultLearnStage = if (isSecondaryProgress) (word.learnStageSecondary + 1) else (word.learnStage + 1)
            val isLearned = if (isEnabledSecondaryProgress) {
                (word.learnStageSecondary + (if (!isCurrentEngTraining) 1 else 0)) >= learnStageMax && (word.learnStage + (if (!isCurrentEngTraining) 0 else 1)) >= learnStageMax
            } else {
                (word.learnStage + 1) >= learnStageMax
            }

            repositoryWords.setWordLearnStage(word, resultLearnStage, isSecondaryProgress)
            if (currentIx >= trainingWords.size - 1) {
                current.value = currentIx
            } else {
                current.value = currentIx + 1
            }

            resultAnimationType.value = if (isLearned) RESULT_LEARNED else RESULT_RIGHT
            navigateEvent.value = Event(NAVIGATION_ANIMATE_RESULT)
            if (isLearned && !isCheckLearnedWords) {
                learned++
                textStatistic.value = "$learned/$total"
                navigateEvent.value = Event(NAVIGATION_ANIMATE_LEARNED_COUNT)
            }
        } else {
            if (isHearAnswer && !isCurrentEngTraining) {
                navigateEvent.value = Event(NAVIGATION_PLAY_WORD)
            }

            repositoryWords.setWordLearnStage(word, 0, isSecondaryProgress)
            if (isCheckLearnedWords) {
                learned--
                textStatistic.value = "$learned/$total"
                navigateEvent.value = Event(NAVIGATION_ANIMATE_LEARNED_COUNT_MINUS)
            }

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
            currentResult == STATE_ANSWER -> textButtonOk.value = getAppString(context, R.string.answer)
            currentIx == (size - 1) -> textButtonOk.value = getAppString(context, R.string.end)
            else -> textButtonOk.value = getAppString(context, R.string.next)
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

    private fun isEngTraining(word: Word, state: Int, currentIx: Int) : Boolean {
        var trainingType = this.trainingType
        var randomTrainingType = Random.nextInt(2)

        if (isEnabledSecondaryProgress) {
            if (randomTrainingType == TRAINING_RUS_TO_ENG && word.learnStageSecondary >= learnStageMax) {
                randomTrainingType = TRAINING_ENG_TO_RUS
            } else if (randomTrainingType == TRAINING_ENG_TO_RUS && word.learnStage >= learnStageMax) {
                randomTrainingType = TRAINING_RUS_TO_ENG
            }
        }
        if (trainingType == TRAINING_MIXED) {
            trainingType = randomTrainingType
            savedMixedTrainingTypes[currentIx] = trainingType
        }

        return when (trainingType) {
            TRAINING_RUS_TO_ENG -> state != STATE_ANSWER || word.type == EXERCISE
            else -> true // ENG_TO_RUS
        }
    }

    private fun clearState() {
        answers.clear()
        savedMixedTrainingTypes.clear()

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
        wordProgressSecondary.value = 0

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
