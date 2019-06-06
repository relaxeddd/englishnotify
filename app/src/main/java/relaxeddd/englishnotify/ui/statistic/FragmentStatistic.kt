package relaxeddd.englishnotify.ui.statistic

import android.os.Bundle
import android.os.Handler
import android.view.View
import androidx.recyclerview.widget.LinearLayoutManager
import kotlinx.android.synthetic.main.fragment_statistic.*
import relaxeddd.englishnotify.R
import relaxeddd.englishnotify.common.BaseFragment
import relaxeddd.englishnotify.common.InjectorUtils
import relaxeddd.englishnotify.common.NAVIGATION_ACTIVITY_BACK
import relaxeddd.englishnotify.databinding.FragmentStatisticBinding

class FragmentStatistic : BaseFragment<ViewModelStatistic, FragmentStatisticBinding>() {

    private lateinit var adapter: AdapterStatistic

    override fun getLayoutResId() = R.layout.fragment_statistic
    override fun getToolbarTitleResId() = R.string.statistic
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
        adapter = AdapterStatistic()
        recycler_view_statistic.layoutManager = LinearLayoutManager(context)
        recycler_view_statistic.isNestedScrollingEnabled = false
        recycler_view_statistic.adapter = adapter
        Handler().postDelayed({ adapter.submitList(viewModel.tagsInfo) }, 400)
    }
}