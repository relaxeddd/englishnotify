package relaxeddd.englishnotify.ui.training_setting

import android.content.res.ColorStateList
import android.os.Bundle
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.lifecycle.Observer
import relaxeddd.englishnotify.R
import relaxeddd.englishnotify.common.*
import relaxeddd.englishnotify.databinding.FragmentTrainingSettingBinding
import relaxeddd.englishnotify.ui.categories.AdapterCategories
import relaxeddd.englishnotify.ui.main.MainActivity
import kotlin.math.roundToInt

class FragmentTrainingSetting : BaseFragment<ViewModelTrainingSetting, FragmentTrainingSettingBinding>() {

    private lateinit var adapter: AdapterCategories

    override fun getLayoutResId() = R.layout.fragment_training_setting
    override fun getToolbarTitleResId() = R.string.training_setting
    override fun getViewModelFactory() = InjectorUtils.provideTrainingSettingViewModelFactory()
    override fun getViewModelClass() = ViewModelTrainingSetting::class.java
    override fun getMenuResId() = R.menu.menu_accept
    override fun isHomeMenuButtonEnabled() = true
    override fun getHomeMenuButtonIconResId() = R.drawable.ic_back
    override fun getHomeMenuButtonListener(): () -> Unit = {
        onNavigationEvent(NAVIGATION_ACTIVITY_BACK)
    }

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
        binding.buttonTrainingSettingStart.setOnClickListener { viewModel.onClickAccept() }
        binding.switchTrainingSettingListenTraining.isChecked = SharedHelper.isListeningTraining()
        binding.switchTrainingSettingListenTraining.setOnCheckedChangeListener { _, isChecked ->
            SharedHelper.setListeningTraining(isChecked)
        }
        viewModel.categories.observe(viewLifecycleOwner, { items ->
            if (items != null && items.isNotEmpty()) adapter.submitList(items)
        })
        (activity as? MainActivity)?.warningContainerSize?.observe(viewLifecycleOwner, {
            val bottomMargin = (if (it == 0) resources.getDimension(R.dimen.size_32) else resources.getDimension(R.dimen.size_8)) + it
            (binding.buttonTrainingSettingStart.layoutParams as? ViewGroup.MarginLayoutParams)?.bottomMargin = bottomMargin.roundToInt()
            binding.buttonTrainingSettingStart.requestLayout()
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

    override fun setupThemeColors() {
        val context = context ?: return
        binding.buttonTrainingSettingStart.backgroundTintList = ColorStateList.valueOf(ContextCompat.getColor(context, getPrimaryColorResId()))
    }
}
