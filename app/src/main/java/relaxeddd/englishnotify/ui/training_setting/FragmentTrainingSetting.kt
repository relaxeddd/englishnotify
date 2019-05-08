package relaxeddd.englishnotify.ui.training_setting

import android.os.Bundle
import android.view.MenuItem
import android.view.View
import androidx.lifecycle.Observer
import kotlinx.android.synthetic.main.fragment_training_setting.*
import relaxeddd.englishnotify.R
import relaxeddd.englishnotify.common.*
import relaxeddd.englishnotify.databinding.FragmentTrainingSettingBinding
import relaxeddd.englishnotify.ui.categories.AdapterCategories

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

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.item_menu_accept -> {
                viewModel.onClickAccept()
                return true
            }
            else -> return super.onOptionsItemSelected(item)
        }
    }

    override fun configureBinding() {
        super.configureBinding()
        adapter = AdapterCategories(viewModel)
        binding.recyclerViewTrainingSettingCategories.adapter = adapter
        viewModel.categories.observe(viewLifecycleOwner, Observer { items ->
            if (items != null && items.isNotEmpty()) adapter.submitList(items)
        })
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        when (viewModel.trainingLanguage) {
            TRAINING_RUS_TO_ENG -> radio_button_training_setting_ru_to_en.isChecked = true
            else -> radio_button_training_setting_en_to_ru.isChecked = true
        }
        radio_button_training_setting_ru_to_en.setOnCheckedChangeListener { _, isChecked ->
            if (isChecked) viewModel.trainingLanguage = TRAINING_RUS_TO_ENG
        }
        radio_button_training_setting_en_to_ru.setOnCheckedChangeListener { _, isChecked ->
            if (isChecked) viewModel.trainingLanguage = TRAINING_ENG_TO_RUS
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