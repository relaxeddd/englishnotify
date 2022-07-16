package relaxeddd.englishnotify.ui.training_setting

import android.os.Bundle
import android.view.LayoutInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import androidx.core.view.updatePaddingRelative
import androidx.fragment.app.viewModels
import relaxeddd.englishnotify.R
import relaxeddd.englishnotify.common.BaseFragment
import relaxeddd.englishnotify.common.CATEGORY
import relaxeddd.englishnotify.common.NAVIGATION_ACTIVITY_BACK
import relaxeddd.englishnotify.common.NAVIGATION_FRAGMENT_TRAINING
import relaxeddd.englishnotify.common.TRAINING_ENG_TO_RUS
import relaxeddd.englishnotify.common.TRAINING_MIXED
import relaxeddd.englishnotify.common.TRAINING_RUS_TO_ENG
import relaxeddd.englishnotify.common.TRAINING_TYPE
import relaxeddd.englishnotify.common.doOnApplyWindowInsets
import relaxeddd.englishnotify.databinding.FragmentTrainingSettingBinding
import relaxeddd.englishnotify.preferences.Preferences
import relaxeddd.englishnotify.preferences.utils.ALL_APP_WORDS
import relaxeddd.englishnotify.ui.categories.AdapterCategories

class FragmentTrainingSetting : BaseFragment<ViewModelTrainingSetting, FragmentTrainingSettingBinding>() {

    private val prefs = Preferences.getInstance()

    private lateinit var adapter: AdapterCategories

    override fun getToolbarTitleResId() = R.string.training_setting
    override fun getMenuResId() = R.menu.menu_accept
    override fun isHomeMenuButtonEnabled() = true
    override fun getHomeMenuButtonIconResId() = R.drawable.ic_back
    override fun getHomeMenuButtonListener(): () -> Unit = {
        onNavigationEvent(NAVIGATION_ACTIVITY_BACK)
    }
    override fun isTopLevelFragment() = true
    override fun getFabIconResId() = R.drawable.ic_accept
    override fun getFabListener() = View.OnClickListener { viewModel.onClickAccept() }

    override val viewModel: ViewModelTrainingSetting by viewModels()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        binding = FragmentTrainingSettingBinding.inflate(inflater)
        return binding?.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding?.apply {
            when (viewModel.trainingLanguage) {
                TRAINING_RUS_TO_ENG -> radioButtonTrainingSettingRuToEn.isChecked = true
                TRAINING_MIXED -> radioButtonTrainingSettingMixed.isChecked = true
                else -> radioButtonTrainingSettingEnToRu.isChecked = true
            }
            radioButtonTrainingSettingRuToEn.setOnCheckedChangeListener { _, isChecked ->
                if (isChecked) viewModel.trainingLanguage = TRAINING_RUS_TO_ENG
            }
            radioButtonTrainingSettingEnToRu.setOnCheckedChangeListener { _, isChecked ->
                if (isChecked) viewModel.trainingLanguage = TRAINING_ENG_TO_RUS
            }
            radioButtonTrainingSettingMixed.setOnCheckedChangeListener { _, isChecked ->
                if (isChecked) viewModel.trainingLanguage = TRAINING_MIXED
            }
            recyclerViewTrainingSettingCategories.doOnApplyWindowInsets { v, insets, padding ->
                v.updatePaddingRelative(bottom = padding.bottom + insets.systemWindowInsetBottom)
            }

            adapter = AdapterCategories(viewModel)

            recyclerViewTrainingSettingCategories.itemAnimator = null
            recyclerViewTrainingSettingCategories.adapter = adapter
            switchTrainingSettingListenTraining.isChecked = prefs.isListeningTraining()
            switchTrainingSettingListenTraining.setOnCheckedChangeListener { _, isChecked ->
                prefs.setListeningTraining(isChecked)
            }
            switchTrainingSettingHearAnswer.isChecked = prefs.isHearAnswer()
            switchTrainingSettingHearAnswer.setOnCheckedChangeListener { _, isChecked ->
                prefs.setHearAnswer(isChecked)
            }
            switchTrainingSettingCheckLearnedWords.isChecked = prefs.isCheckLearnedWords()
            switchTrainingSettingCheckLearnedWords.setOnCheckedChangeListener { _, isChecked ->
                prefs.setCheckLearnedWords(isChecked)
            }
        }
    }

    override fun onOptionsItemSelected(item: MenuItem) = when (item.itemId) {
        R.id.item_menu_accept -> {
            viewModel.onClickAccept()
            true
        }
        else -> super.onOptionsItemSelected(item)
    }

    override fun subscribeToViewModel() {
        super.subscribeToViewModel()

        viewModel.categories.observe(viewLifecycleOwner) { items ->
            if (items != null && items.isNotEmpty()) adapter.submitList(items)
        }
    }

    override fun onNavigationEvent(eventId: Int) {
        when (eventId) {
            NAVIGATION_FRAGMENT_TRAINING -> {
                val args = Bundle()
                val category = viewModel.checkedItem?.key ?: ALL_APP_WORDS
                args.putString(CATEGORY, category)
                args.putInt(TRAINING_TYPE, viewModel.trainingLanguage)
                navigate(R.id.action_fragmentTrainingSetting_to_fragmentTraining, args)
            }
            else -> super.onNavigationEvent(eventId)
        }
    }
}
