package relaxeddd.englishnotify.ui.statistic

import android.os.Bundle
import android.view.View
import androidx.recyclerview.widget.LinearLayoutManager
import relaxeddd.englishnotify.R
import relaxeddd.englishnotify.common.BaseFragment
import relaxeddd.englishnotify.common.InjectorUtils
import relaxeddd.englishnotify.common.NAVIGATION_ACTIVITY_BACK
import relaxeddd.englishnotify.databinding.FragmentStatisticBinding

class FragmentStatistic : BaseFragment<ViewModelStatistic, FragmentStatisticBinding>() {

    private lateinit var adapter: AdapterStatistic

    override fun getLayoutResId() = R.layout.fragment_statistic
    override fun getToolbarTitleResId() = R.string.own_words_statistic
    override fun getViewModelFactory() = InjectorUtils.provideStatisticViewModelFactory(requireContext())
    override fun getViewModelClass() = ViewModelStatistic::class.java
    override fun isHomeMenuButtonEnabled() = true
    override fun getHomeMenuButtonIconResId() = R.drawable.ic_back
    override fun getHomeMenuButtonListener(): () -> Unit = { onNavigationEvent(NAVIGATION_ACTIVITY_BACK) }

    override fun configureBinding() {
        super.configureBinding()
        binding.viewModel = viewModel
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        adapter = AdapterStatistic(viewModel)
        binding.recyclerViewStatistic.layoutManager = LinearLayoutManager(context)
        binding.recyclerViewStatistic.adapter = adapter

        viewModel.ownWords.observe(viewLifecycleOwner, { words ->
            updateTagsInfo()
            adapter.submitList(words)
        })
    }

    private fun updateTagsInfo() {
        val tagInfo = viewModel.ownTagInfo
        val textOwnWordsTag = "" + tagInfo.learned + " / " + tagInfo.total
        val textOwnWordsPercentage = "" + (if (tagInfo.total != 0) (tagInfo.learned.toFloat() / tagInfo.total.toFloat() * 100).toInt() else 0) + "%"

        binding.textStatisticOwnWords.text = textOwnWordsTag
        binding.textStatisticOwnWordsPercentage.text = textOwnWordsPercentage
        binding.progressBarStatisticOwnWords.progress = if (tagInfo.total != 0) (tagInfo.learned.toFloat() / tagInfo.total.toFloat() * 100).toInt() else 0
    }
}
