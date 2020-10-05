package relaxeddd.englishnotify.ui.training_setting

import android.os.Bundle
import android.view.MenuItem
import android.view.View
import androidx.core.view.updatePaddingRelative
import relaxeddd.englishnotify.R
import relaxeddd.englishnotify.common.*
import relaxeddd.englishnotify.databinding.FragmentTrainingSettingBinding
import relaxeddd.englishnotify.ui.categories.AdapterCategories

class FragmentTrainingSetting : BaseFragment<ViewModelTrainingSetting, FragmentTrainingSettingBinding>() {

    private lateinit var adapter: AdapterCategories

    override fun getLayoutResId() = R.layout.fragment_training_setting
    override fun getToolbarTitleResId() = R.string.training_setting
    override fun getViewModelFactory() = InjectorUtils.provideTrainingSettingViewModelFactory(requireContext())
    override fun getViewModelClass() = ViewModelTrainingSetting::class.java
    override fun getMenuResId() = R.menu.menu_accept
    override fun isHomeMenuButtonEnabled() = true
    override fun getHomeMenuButtonIconResId() = R.drawable.ic_back
    override fun getHomeMenuButtonListener(): () -> Unit = {
        onNavigationEvent(NAVIGATION_ACTIVITY_BACK)
    }
    override fun isTopLevelFragment() = true
    override fun getFabIconResId() = R.drawable.ic_accept
    override fun getFabListener() = View.OnClickListener { viewModel.onClickAccept() }

    override fun onOptionsItemSelected(item: MenuItem) = when (item.itemId) {
        R.id.item_menu_accept -> {
            viewModel.onClickAccept()
            true
        }
        else -> super.onOptionsItemSelected(item)
    }

    override fun configureBinding() {
        super.configureBinding()
        adapter = AdapterCategories(viewModel)
        binding.recyclerViewTrainingSettingCategories.itemAnimator = null
        binding.recyclerViewTrainingSettingCategories.adapter = adapter
        binding.switchTrainingSettingListenTraining.isChecked = SharedHelper.isListeningTraining()
        binding.switchTrainingSettingListenTraining.setOnCheckedChangeListener { _, isChecked ->
            SharedHelper.setListeningTraining(isChecked)
        }
        binding.switchTrainingSettingHearAnswer.isChecked = SharedHelper.isHearAnswer()
        binding.switchTrainingSettingHearAnswer.setOnCheckedChangeListener { _, isChecked ->
            SharedHelper.setHearAnswer(isChecked)
        }
        viewModel.categories.observe(viewLifecycleOwner, { items ->
            if (items != null && items.isNotEmpty()) adapter.submitList(items)
        })
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        when (viewModel.trainingLanguage) {
            TRAINING_RUS_TO_ENG -> binding.radioButtonTrainingSettingRuToEn.isChecked = true
            TRAINING_MIXED -> binding.radioButtonTrainingSettingMixed.isChecked = true
            else -> binding.radioButtonTrainingSettingEnToRu.isChecked = true
        }
        binding.radioButtonTrainingSettingRuToEn.setOnCheckedChangeListener { _, isChecked ->
            if (isChecked) viewModel.trainingLanguage = TRAINING_RUS_TO_ENG
        }
        binding.radioButtonTrainingSettingEnToRu.setOnCheckedChangeListener { _, isChecked ->
            if (isChecked) viewModel.trainingLanguage = TRAINING_ENG_TO_RUS
        }
        binding.radioButtonTrainingSettingMixed.setOnCheckedChangeListener { _, isChecked ->
            if (isChecked) viewModel.trainingLanguage = TRAINING_MIXED
        }
        binding.recyclerViewTrainingSettingCategories.doOnApplyWindowInsets { v, insets, padding ->
            v.updatePaddingRelative(bottom = padding.bottom + insets.systemWindowInsetBottom)
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
