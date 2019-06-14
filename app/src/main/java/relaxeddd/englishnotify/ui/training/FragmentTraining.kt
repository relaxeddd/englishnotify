package relaxeddd.englishnotify.ui.training

import android.view.View
import kotlinx.android.synthetic.main.fragment_training.*
import relaxeddd.englishnotify.R
import relaxeddd.englishnotify.common.*
import relaxeddd.englishnotify.databinding.FragmentTrainingBinding
import relaxeddd.englishnotify.ui.main.MainActivity

class FragmentTraining : BaseFragment<ViewModelTraining, FragmentTrainingBinding>() {

    private var toolbarTitleTraining: String = "Training"

    private val clickListenerOk = View.OnClickListener {
        val answer = edit_text_training_answer.text.toString()
        edit_text_training_answer.setText("")
        hideKeyboard(edit_text_training_answer)
        viewModel.onClickOk(answer)
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
        binding.viewModel = viewModel
        viewModel.onBind()
    }

    override fun onNavigationEvent(eventId: Int) {
        when (eventId) {
            NAVIGATION_PLAY_WORD -> {
                val ac = activity
                if (ac is MainActivity) {
                    ac.playWord(viewModel.getCurrentWord())
                }
            }
            else -> super.onNavigationEvent(eventId)
        }
    }
}