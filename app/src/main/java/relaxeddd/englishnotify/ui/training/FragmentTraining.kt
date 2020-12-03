package relaxeddd.englishnotify.ui.training

import android.content.Context
import android.view.View
import android.view.animation.*
import android.view.inputmethod.InputMethodManager
import android.widget.AdapterView
import android.widget.ArrayAdapter
import androidx.core.content.ContextCompat
import kotlinx.android.synthetic.main.fragment_training.*
import relaxeddd.englishnotify.R
import relaxeddd.englishnotify.common.*
import relaxeddd.englishnotify.databinding.FragmentTrainingBinding
import relaxeddd.englishnotify.model.preferences.SharedHelper
import relaxeddd.englishnotify.ui.main.MainActivity
import kotlin.random.Random

class FragmentTraining : BaseFragment<ViewModelTraining, FragmentTrainingBinding>() {

    private var toolbarTitleTraining: String = "Training"

    private val clickListenerOk = View.OnClickListener {
        val answer = edit_text_training_answer.text.toString()
        edit_text_training_answer.setText("")
        viewModel.onClickOk(answer)
    }

    private val clickListenerOneMore = View.OnClickListener {
        viewModel.onClickOneMore()
    }

    override fun getLayoutResId() = R.layout.fragment_training
    override fun getToolbarTitle() = toolbarTitleTraining
    override fun getViewModelFactory() = InjectorUtils.provideTrainingViewModelFactory(requireContext())
    override fun getViewModelClass() = ViewModelTraining::class.java
    override fun isHomeMenuButtonEnabled() = true
    override fun getHomeMenuButtonIconResId() = R.drawable.ic_back
    override fun getHomeMenuButtonListener(): () -> Unit = {
        onNavigationEvent(NAVIGATION_ACTIVITY_BACK)
    }

    override fun configureBinding() {
        super.configureBinding()
        val category = arguments?.getString(CATEGORY) ?: ALL_APP_WORDS
        toolbarTitleTraining = getStringByResName(category).replaceFirst(OWN_KEY_SYMBOL, "")
        viewModel.category = category
        viewModel.trainingType = arguments?.getInt(TRAINING_TYPE) ?: TRAINING_ENG_TO_RUS
        binding.clickListenerOk = clickListenerOk
        binding.clickListenerOneMore = clickListenerOneMore
        binding.viewModel = viewModel
        binding.editTextTrainingAnswer.setOnEditorActionListener { view, _, event ->
            if (event == null || !event.isShiftPressed) {
                clickListenerOk.onClick(view)
                true
            } else {
                false
            }
        }

        binding.imageTrainingMicrophone.setOnClickListener {
            onNavigationEvent(NAVIGATION_HIDE_KEYBOARD)

            val selectedLanguage = binding.spinnerTrainingLanguage.selectedItem as? String ?: ""
            (activity as? MainActivity)?.requestRecognizeSpeech(selectedLanguage) {
                if (it == null) {
                    SharedHelper.setShowVoiceInput(false)
                    binding.imageTrainingMicrophone.visibility = View.GONE
                    binding.spinnerTrainingLanguage.visibility = View.GONE
                } else {
                    binding.editTextTrainingAnswer.setText(it)
                }
            }
        }
        ArrayAdapter.createFromResource(context ?: return, R.array.array_languages, android.R.layout.simple_spinner_item).also { adapter ->
            adapter.setDropDownViewResource(R.layout.view_item_spinner)
            binding.spinnerTrainingLanguage.adapter = adapter
        }
        binding.spinnerTrainingLanguage.setSelection(SharedHelper.getSelectedLocaleTraining())
        binding.spinnerTrainingLanguage.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                SharedHelper.setSelectedLocaleTraining(position)
            }
            override fun onNothingSelected(parent: AdapterView<*>?) {}
        }

        val isShowVoiceInput = SharedHelper.isShowVoiceInput()
        binding.imageTrainingMicrophone.visibility = if (isShowVoiceInput) View.VISIBLE else View.GONE
        binding.spinnerTrainingLanguage.visibility = if (isShowVoiceInput) View.VISIBLE else View.GONE

        viewModel.onBind()

        val isListeningTraining = SharedHelper.isListeningTraining()

        if (isListeningTraining) {
            onNavigationEvent(NAVIGATION_PLAY_WORD_DEPENDS_ON_TRANSLATION)
        }
    }

    override fun onNavigationEvent(eventId: Int) {
        when (eventId) {
            NAVIGATION_HIDE_KEYBOARD -> hideKeyboard(binding.editTextTrainingAnswer)
            NAVIGATION_PLAY_WORD -> {
                val ac = activity
                if (ac is MainActivity) {
                    ac.playWord(viewModel.getCurrentWord())
                }
            }
            NAVIGATION_SHOW_KEYBOARD -> {
                binding.editTextTrainingAnswer.requestFocus()
                val inputMethodManager = context?.getSystemService(Context.INPUT_METHOD_SERVICE) as? InputMethodManager
                inputMethodManager?.showSoftInput(binding.editTextTrainingAnswer, InputMethodManager.SHOW_IMPLICIT)
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
                hideKeyboard(binding.editTextTrainingAnswer)
                super.onNavigationEvent(eventId)
            }
            else -> super.onNavigationEvent(eventId)
        }
    }

    private fun animateLearnedCount(isPlusOne: Boolean = true) {
        binding.textTrainingLearnedPlus.text = if (isPlusOne) "+1" else "-1"
        binding.textTrainingLearnedPlus.setTextColor(ContextCompat.getColor(context ?: return, if (isPlusOne) R.color.green_success else R.color.red_wrong))
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
                text = getString(when(Random.nextInt(3)) {
                    0 -> R.string.incorrect
                    1 -> R.string.wrong
                    else -> R.string.false_answer
                })
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
                text = getString(when(Random.nextInt(3)) {
                    0 -> R.string.correct
                    1 -> R.string.right
                    else -> R.string.true_answer
                })
                color = R.color.green_success
            }
        }

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
}
