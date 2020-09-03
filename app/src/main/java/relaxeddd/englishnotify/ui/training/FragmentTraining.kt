package relaxeddd.englishnotify.ui.training

import android.view.View
import android.view.animation.*
import androidx.core.content.ContextCompat
import kotlinx.android.synthetic.main.fragment_training.*
import relaxeddd.englishnotify.R
import relaxeddd.englishnotify.common.*
import relaxeddd.englishnotify.databinding.FragmentTrainingBinding
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
        toolbarTitleTraining = getStringByResName(category)
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
        viewModel.onBind()
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
            NAVIGATION_ACTIVITY_BACK -> {
                hideKeyboard(binding.editTextTrainingAnswer)
                super.onNavigationEvent(eventId)
            }
            else -> super.onNavigationEvent(eventId)
        }
    }

    private fun animateLearnedCount() {
        binding.textTrainingLearnedPlus.visibility = View.VISIBLE

        val set = AnimationSet(true).apply {
            interpolator = AccelerateInterpolator()
            addAnimation(TranslateAnimation(0f, 0f, 0f, -180f))
            addAnimation(AlphaAnimation(1f, 0f))
            duration = 1200
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
            duration = 1200
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
