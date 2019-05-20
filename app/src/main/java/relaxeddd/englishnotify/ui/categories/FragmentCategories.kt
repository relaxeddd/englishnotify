package relaxeddd.englishnotify.ui.categories

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentPagerAdapter
import kotlinx.android.synthetic.main.fragment_categories.*
import relaxeddd.englishnotify.R
import relaxeddd.englishnotify.common.getString
import relaxeddd.englishnotify.ui.categories.section.FragmentCategorySection

class FragmentCategories : Fragment() {

    private lateinit var pagerAdapter: PagerAdapterCategories

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_categories, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        pagerAdapter = PagerAdapterCategories(childFragmentManager)
        tab_layout_categories.setupWithViewPager(view_pager_categories)
        view_pager_categories.adapter = pagerAdapter
    }

    class PagerAdapterCategories(fm: FragmentManager) : FragmentPagerAdapter(fm, BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT) {

        override fun getCount() = CategorySection.values().size
        override fun getPageTitle(position: Int) : CharSequence = getString(CategorySection.values()[position].titleResId)
        override fun getItem(position: Int) = FragmentCategorySection(CategorySection.values()[position])
    }
}