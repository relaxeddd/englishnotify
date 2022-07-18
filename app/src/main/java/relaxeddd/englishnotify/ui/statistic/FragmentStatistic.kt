package relaxeddd.englishnotify.ui.statistic

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import relaxeddd.englishnotify.R
import relaxeddd.englishnotify.common.NAVIGATION_ACTIVITY_BACK
import relaxeddd.englishnotify.databinding.FragmentStatisticBinding
import relaxeddd.englishnotify.view_base.BaseFragment

class FragmentStatistic : BaseFragment<ViewModelStatistic, FragmentStatisticBinding>() {

    private lateinit var adapter: AdapterStatistic

    override fun getToolbarTitleResId() = R.string.own_words_statistic
    override fun isHomeMenuButtonEnabled() = true
    override fun getHomeMenuButtonIconResId() = R.drawable.ic_back
    override fun getHomeMenuButtonListener(): () -> Unit = { onNavigationEvent(NAVIGATION_ACTIVITY_BACK) }

    override val viewModel: ViewModelStatistic by viewModels()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        binding = FragmentStatisticBinding.inflate(inflater)
        return binding?.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding?.apply {
            adapter = AdapterStatistic(viewModel)
            recyclerViewStatistic.layoutManager = LinearLayoutManager(context)
            recyclerViewStatistic.adapter = adapter
        }
    }

    override fun subscribeToViewModel() {
        super.subscribeToViewModel()

        viewModel.ownWords.observe(viewLifecycleOwner) { words ->
            updateTagsInfo()
            adapter.submitList(words)
        }
    }

    private fun updateTagsInfo() {
        val tagInfo = viewModel.ownTagInfo
        val textOwnWordsTag = "" + tagInfo.learned + " / " + tagInfo.total
        val textOwnWordsPercentage = "" + (if (tagInfo.total != 0) (tagInfo.learned.toFloat() / tagInfo.total.toFloat() * 100).toInt() else 0) + "%"
        val binding = binding ?: return

        binding.textStatisticOwnWords.text = textOwnWordsTag
        binding.textStatisticOwnWordsPercentage.text = textOwnWordsPercentage
        binding.progressBarStatisticOwnWords.progress = if (tagInfo.total != 0) (tagInfo.learned.toFloat() / tagInfo.total.toFloat() * 100).toInt() else 0
    }
}
