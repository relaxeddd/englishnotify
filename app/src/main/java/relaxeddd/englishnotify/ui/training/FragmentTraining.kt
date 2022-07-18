package relaxeddd.englishnotify.ui.training

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.AccelerateInterpolator
import android.view.animation.AlphaAnimation
import android.view.animation.Animation
import android.view.animation.AnimationSet
import android.view.animation.TranslateAnimation
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.RadioButton
import androidx.core.content.ContextCompat
import androidx.core.view.doOnLayout
import androidx.core.view.isInvisible
import androidx.core.view.isVisible
import androidx.fragment.app.viewModels
import relaxeddd.englishnotify.R
import relaxeddd.englishnotify.common.*
import relaxeddd.englishnotify.databinding.FragmentTrainingBinding
import relaxeddd.englishnotify.preferences.Preferences
import relaxeddd.englishnotify.preferences.utils.ALL_APP_WORDS
import relaxeddd.englishnotify.ui.main.MainActivity
import relaxeddd.englishnotify.view_base.BaseFragment
import kotlin.random.Random

class FragmentTraining: BaseFragment<ViewModelTraining, FragmentTrainingBinding>() {

    private val prefs = Preferences.getInstance()

    private var toolbarTitleTraining: String = "Training"

    private val clickListenerOk = View.OnClickListener {
        val answer = binding?.editTextTrainingAnswer?.text?.toString() ?: ""
        binding?.editTextTrainingAnswer?.setText("")
        viewModel.onClickOk(answer)
    }

    private val clickListenerOneMore = View.OnClickListener {
        viewModel.onClickOneMore()
    }

    override fun getToolbarTitle() = toolbarTitleTraining
    override fun isHomeMenuButtonEnabled() = true
    override fun getHomeMenuButtonIconResId() = R.drawable.ic_back
    override fun getHomeMenuButtonListener(): () -> Unit = {
        onNavigationEvent(NAVIGATION_ACTIVITY_BACK)
    }

    override val viewModel: ViewModelTraining by viewModels()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        binding = FragmentTrainingBinding.inflate(inflater)
        return binding?.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding?.apply {
            editTextTrainingAnswer.setOnEditorActionListener { view, _, event ->
                if (event == null || !event.isShiftPressed) {
                    clickListenerOk.onClick(view)
                    true
                } else {
                    false
                }
            }

            imageTrainingMicrophone.setOnClickListener {
                onNavigationEvent(NAVIGATION_HIDE_KEYBOARD)

                val selectedLanguage = spinnerTrainingLanguage.selectedItem as? String ?: ""
                (activity as? MainActivity)?.requestRecognizeSpeech(selectedLanguage) {
                    if (it == null) {
                        prefs.setShowVoiceInput(false)
                        imageTrainingMicrophone.visibility = View.GONE
                        spinnerTrainingLanguage.visibility = View.GONE
                    } else {
                        editTextTrainingAnswer.setText(it)
                    }
                }
            }
            ArrayAdapter.createFromResource(
                context ?: return,
                R.array.array_languages,
                android.R.layout.simple_spinner_item
            ).also { adapter ->
                adapter.setDropDownViewResource(R.layout.view_item_spinner)
                spinnerTrainingLanguage.adapter = adapter
            }
            spinnerTrainingLanguage.setSelection(prefs.getSelectedLocaleTraining())
            spinnerTrainingLanguage.onItemSelectedListener = object: AdapterView.OnItemSelectedListener {
                override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                    prefs.setSelectedLocaleTraining(position)
                }

                override fun onNothingSelected(parent: AdapterView<*>?) {}
            }

            val isShowVoiceInput = prefs.isShowVoiceInput()
            imageTrainingMicrophone.isVisible = isShowVoiceInput
            spinnerTrainingLanguage.isVisible = isShowVoiceInput

            editTextTrainingAnswer.doOnLayout {
                showKeyboard(it)
            }

            radioButtonTraining1.setOnClickListener(viewModel.clickListenerRadioButton1)
            radioButtonTraining2.setOnClickListener(viewModel.clickListenerRadioButton2)
            radioButtonTraining3.setOnClickListener(viewModel.clickListenerRadioButton3)
            radioButtonTraining4.setOnClickListener(viewModel.clickListenerRadioButton4)
            radioButtonTraining5.setOnClickListener(viewModel.clickListenerRadioButton5)
            radioButtonTraining6.setOnClickListener(viewModel.clickListenerRadioButton6)
            radioButtonTraining7.setOnClickListener(viewModel.clickListenerRadioButton7)
            radioButtonTraining8.setOnClickListener(viewModel.clickListenerRadioButton8)
            radioButtonTraining9.setOnClickListener(viewModel.clickListenerRadioButton9)
            radioButtonTraining10.setOnClickListener(viewModel.clickListenerRadioButton10)

            buttonTrainingConfirm.setOnClickListener(clickListenerOk)
            buttonTrainingOneMore.setOnClickListener(clickListenerOneMore)
            imageTrainingListen.setOnClickListener(viewModel.clickListenerListen)
            textTrainingTranscription.setOnClickListener(viewModel.clickListenerPlayWord)
        }

        val category = arguments?.getString(CATEGORY) ?: ALL_APP_WORDS
        toolbarTitleTraining = getStringByResName(category).replaceFirst(OWN_KEY_SYMBOL, "")
        viewModel.category = category
        viewModel.trainingType = arguments?.getInt(TRAINING_TYPE) ?: TRAINING_ENG_TO_RUS

        viewModel.onBind()

        val isListeningTraining = prefs.isListeningTraining()

        if (isListeningTraining) {
            onNavigationEvent(NAVIGATION_PLAY_WORD_DEPENDS_ON_TRANSLATION)
        }
    }

    override fun subscribeToViewModel() {
        super.subscribeToViewModel()

        viewModel.wordText.observe(viewLifecycleOwner) { binding?.textTrainingWord?.text = it }
        viewModel.isVisibleTextWord.observe(viewLifecycleOwner) { binding?.textTrainingWord?.isVisible = it }
        viewModel.transcription.observe(viewLifecycleOwner) { binding?.textTrainingTranscription?.text = it }
        viewModel.isVisibleTranscription.observe(viewLifecycleOwner) { binding?.textTrainingTranscription?.isInvisible = !it }
        viewModel.translation.observe(viewLifecycleOwner) { binding?.textTrainingTranslation?.text = it }
        viewModel.isVisibleTranslation.observe(viewLifecycleOwner) { binding?.textTrainingTranslation?.isInvisible = !it }
        viewModel.answer.observe(viewLifecycleOwner) { binding?.textTrainingAnswer?.text = it }
        viewModel.isVisibleAnswer.observe(viewLifecycleOwner) { binding?.textTrainingAnswer?.isInvisible = !it }
        viewModel.resultText.observe(viewLifecycleOwner) { binding?.textTrainingResultText?.text = it }
        viewModel.isVisibleResultText.observe(viewLifecycleOwner) { binding?.textTrainingResultText?.isInvisible = !it }
        viewModel.isVisibleWordProgress.observe(viewLifecycleOwner) { binding?.progressBarTrainingWordStage?.isInvisible = !it }
        viewModel.wordProgress.observe(viewLifecycleOwner) { binding?.progressBarTrainingWordStage?.progress = it }
        viewModel.isVisibleWordProgressSecondary.observe(viewLifecycleOwner) { binding?.progressBarTrainingWordStageSecondary?.isInvisible = !it }
        viewModel.wordProgressSecondary.observe(viewLifecycleOwner) { binding?.progressBarTrainingWordStageSecondary?.progress = it }
        viewModel.textStatistic.observe(viewLifecycleOwner) { binding?.textTrainingLearnedStatistic?.text = it }
        viewModel.textButtonOk.observe(viewLifecycleOwner) { binding?.buttonTrainingConfirm?.text = it }
        viewModel.isVisibleButtonOk.observe(viewLifecycleOwner) { binding?.buttonTrainingConfirm?.isInvisible = !it }
        viewModel.textButtonOneMore.observe(viewLifecycleOwner) { binding?.buttonTrainingOneMore?.text = it }
        viewModel.isVisibleButtonOneMore.observe(viewLifecycleOwner) { binding?.buttonTrainingOneMore?.isInvisible = !it }
        viewModel.isVisibleButtonListen.observe(viewLifecycleOwner) { binding?.imageTrainingListen?.isVisible = it }
        viewModel.isVisibleInputAnswer.observe(viewLifecycleOwner) {
            binding?.editTextTrainingAnswer?.isEnabled = it
            binding?.editTextTrainingAnswer?.alpha = if (it) 1f else 0.5f
        }
        viewModel.wordsSize.observe(viewLifecycleOwner) {
            binding?.radioButtonTraining1?.isVisible = it > 0
            binding?.radioButtonTraining2?.isVisible = it > 1
            binding?.radioButtonTraining3?.isVisible = it > 2
            binding?.radioButtonTraining4?.isVisible = it > 3
            binding?.radioButtonTraining5?.isVisible = it > 4
            binding?.radioButtonTraining6?.isVisible = it > 5
            binding?.radioButtonTraining7?.isVisible = it > 6
            binding?.radioButtonTraining8?.isVisible = it > 7
            binding?.radioButtonTraining9?.isVisible = it > 8
            binding?.radioButtonTraining10?.isVisible = it > 9
        }

        viewModel.result1.observe(viewLifecycleOwner) {
            updateRadioButton(0)
            updateRadioButton(1)
        }
        viewModel.result2.observe(viewLifecycleOwner) {
            updateRadioButton(1)
            updateRadioButton(2)
        }
        viewModel.result3.observe(viewLifecycleOwner) {
            updateRadioButton(2)
            updateRadioButton(3)
        }
        viewModel.result4.observe(viewLifecycleOwner) {
            updateRadioButton(3)
            updateRadioButton(4)
        }
        viewModel.result5.observe(viewLifecycleOwner) {
            updateRadioButton(4)
            updateRadioButton(5)
        }
        viewModel.result6.observe(viewLifecycleOwner) {
            updateRadioButton(5)
            updateRadioButton(6)
        }
        viewModel.result7.observe(viewLifecycleOwner) {
            updateRadioButton(6)
            updateRadioButton(7)
        }
        viewModel.result8.observe(viewLifecycleOwner) {
            updateRadioButton(7)
            updateRadioButton(8)
        }
        viewModel.result9.observe(viewLifecycleOwner) {
            updateRadioButton(8)
            updateRadioButton(9)
        }
        viewModel.result10.observe(viewLifecycleOwner) {
            updateRadioButton(9)
        }
        viewModel.current.observe(viewLifecycleOwner) {
            binding?.apply {
                viewTrainingCurrent1.isInvisible = it != 0
                viewTrainingCurrent2.isInvisible = it != 1
                viewTrainingCurrent3.isInvisible = it != 2
                viewTrainingCurrent4.isInvisible = it != 3
                viewTrainingCurrent5.isInvisible = it != 4
                viewTrainingCurrent6.isInvisible = it != 5
                viewTrainingCurrent7.isInvisible = it != 6
                viewTrainingCurrent8.isInvisible = it != 7
                viewTrainingCurrent9.isInvisible = it != 8
                viewTrainingCurrent10.isInvisible = it != 9
                for (i in 0..9) {
                    updateRadioButton(i)
                }
            }
        }
    }

    override fun onNavigationEvent(eventId: Int) {
        when(eventId) {
            NAVIGATION_SHOW_KEYBOARD -> {
                showKeyboard(binding?.editTextTrainingAnswer)
            }
            NAVIGATION_HIDE_KEYBOARD -> hideKeyboard(binding?.editTextTrainingAnswer)
            NAVIGATION_PLAY_WORD -> {
                val ac = activity
                if (ac is MainActivity) {
                    ac.playWord(viewModel.getCurrentWord())
                }
            }
            NAVIGATION_PLAY_WORD_DEPENDS_ON_TRANSLATION -> {
                val ac = activity
                if (ac is MainActivity) {
                    val word = viewModel.getCurrentWord()
                    ac.playText(if (viewModel.isCurrentEngTraining) word?.eng else word?.rus)
                }
            }
            NAVIGATION_ANIMATE_RESULT -> {
                animateResult(viewModel.resultAnimationType.value ?: return)
            }
            NAVIGATION_ANIMATE_LEARNED_COUNT -> {
                animateLearnedCount()
            }
            NAVIGATION_ANIMATE_LEARNED_COUNT_MINUS -> {
                animateLearnedCount(false)
            }
            NAVIGATION_ACTIVITY_BACK -> {
                hideKeyboard(binding?.editTextTrainingAnswer)
                super.onNavigationEvent(eventId)
            }
            else -> super.onNavigationEvent(eventId)
        }
    }

    private fun animateLearnedCount(isPlusOne: Boolean = true) {
        val binding = binding ?: return
        binding.textTrainingLearnedPlus.text = if (isPlusOne) "+1" else "-1"
        binding.textTrainingLearnedPlus.setTextColor(
            ContextCompat.getColor(
                context ?: return,
                if (isPlusOne) R.color.green_success else R.color.red_wrong
            )
        )
        binding.textTrainingLearnedPlus.visibility = View.VISIBLE

        val set = AnimationSet(true).apply {
            interpolator = AccelerateInterpolator()
            addAnimation(TranslateAnimation(0f, 0f, 0f, -180f))
            addAnimation(AlphaAnimation(1f, 0f))
            duration = 1600
            setAnimationListener(object: Animation.AnimationListener {

                override fun onAnimationRepeat(animation: Animation?) {}
                override fun onAnimationStart(animation: Animation?) {}
                override fun onAnimationEnd(animation: Animation?) {
                    binding.textTrainingLearnedPlus.visibility = View.INVISIBLE
                }
            })
        }

        binding.textTrainingLearnedPlus.startAnimation(set)
    }

    private fun animateResult(type: Int) {
        val context = context ?: return
        val text: String
        val color: Int

        when(type) {
            ViewModelTraining.RESULT_WRONG -> {
                text = getString(
                    when(Random.nextInt(3)) {
                        0 -> R.string.incorrect
                        1 -> R.string.wrong
                        else -> R.string.false_answer
                    }
                )
                color = R.color.red_wrong
            }
            ViewModelTraining.RESULT_LEARNED -> {
                text = getString(R.string.learned)
                color = R.color.green_success
            }
            ViewModelTraining.RESULT_MEMORIZE -> {
                text = getString(R.string.memorize)
                color = R.color.red_wrong
            }
            else -> {
                text = getString(
                    when(Random.nextInt(3)) {
                        0 -> R.string.correct
                        1 -> R.string.right
                        else -> R.string.true_answer
                    }
                )
                color = R.color.green_success
            }
        }

        val binding = binding ?: return
        binding.textTrainingResultAnimation.setTextColor(ContextCompat.getColor(context, color))
        binding.textTrainingResultAnimation.text = text
        binding.textTrainingResultAnimation.visibility = View.VISIBLE

        val set = AnimationSet(true).apply {
            interpolator = AccelerateInterpolator()
            addAnimation(TranslateAnimation(0f, 0f, 0f, -250f))
            addAnimation(AlphaAnimation(1f, 0f))
            duration = 1600
            setAnimationListener(object: Animation.AnimationListener {

                override fun onAnimationRepeat(animation: Animation?) {}
                override fun onAnimationStart(animation: Animation?) {}
                override fun onAnimationEnd(animation: Animation?) {
                    binding.textTrainingResultAnimation.visibility = View.INVISIBLE
                }
            })
        }

        binding.textTrainingResultAnimation.startAnimation(set)
    }

    private fun updateRadioButton(index: Int) {
        when(index) {
            0 -> updateRadioButton(binding?.radioButtonTraining1, viewModel.result1.value, 0, viewModel.current.value, index)
            1 -> updateRadioButton(binding?.radioButtonTraining2, viewModel.result2.value, viewModel.result1.value, viewModel.current.value, index)
            2 -> updateRadioButton(binding?.radioButtonTraining3, viewModel.result3.value, viewModel.result2.value, viewModel.current.value, index)
            3 -> updateRadioButton(binding?.radioButtonTraining4, viewModel.result4.value, viewModel.result3.value, viewModel.current.value, index)
            4 -> updateRadioButton(binding?.radioButtonTraining5, viewModel.result5.value, viewModel.result4.value, viewModel.current.value, index)
            5 -> updateRadioButton(binding?.radioButtonTraining6, viewModel.result6.value, viewModel.result5.value, viewModel.current.value, index)
            6 -> updateRadioButton(binding?.radioButtonTraining7, viewModel.result7.value, viewModel.result6.value, viewModel.current.value, index)
            7 -> updateRadioButton(binding?.radioButtonTraining8, viewModel.result8.value, viewModel.result7.value, viewModel.current.value, index)
            8 -> updateRadioButton(binding?.radioButtonTraining9, viewModel.result9.value, viewModel.result8.value, viewModel.current.value, index)
            9 -> updateRadioButton(binding?.radioButtonTraining10, viewModel.result10.value, viewModel.result9.value, viewModel.current.value, index)
        }
    }

    private fun updateRadioButton(radioButton: RadioButton?, result: Int?, previousResult: Int?, current: Int?, index: Int) {
        val isSelected = result != 0 || previousResult != 0 || current == index
        radioButton?.isChecked = isSelected
        radioButton?.isEnabled = isSelected
        radioButton?.buttonTintList = calculateRadioButtonTintListByResult(result)
    }

    private fun calculateRadioButtonTintListByResult(answerResult: Int?) = ContextCompat.getColorStateList(
        requireContext(), when(answerResult) {
            1 -> R.color.green_success
            2 -> R.color.red_wrong
            else -> R.color.gray
        }
    )
}
