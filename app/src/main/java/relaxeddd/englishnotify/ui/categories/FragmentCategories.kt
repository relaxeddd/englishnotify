package relaxeddd.englishnotify.ui.categories

import android.view.MenuItem
import androidx.fragment.app.Fragment
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.google.android.material.tabs.TabLayoutMediator
import relaxeddd.englishnotify.R
import relaxeddd.englishnotify.common.BaseFragment
import relaxeddd.englishnotify.common.InjectorUtils
import relaxeddd.englishnotify.common.NAVIGATION_ACTIVITY_BACK
import relaxeddd.englishnotify.common.getAppString
import relaxeddd.englishnotify.databinding.FragmentCategoriesBinding
import relaxeddd.englishnotify.ui.categories.section.FragmentCategorySection

class FragmentCategories : BaseFragment<ViewModelCategories, FragmentCategoriesBinding>() {

    private var fragmentsMap = HashMap<Int, FragmentCategorySection?>()
    private val currentFragment: FragmentCategorySection?
        get() = fragmentsMap[binding?.viewPagerCategories?.currentItem ?: 0]

    override fun getLayoutResId() = R.layout.fragment_categories
    override fun getToolbarTitleResId() = R.string.dictionary
    override fun getViewModelFactory() = InjectorUtils.provideDictionaryContainerViewModelFactory()
    override fun getViewModelClass() = ViewModelCategories::class.java
    override fun getMenuResId() = R.menu.menu_accept
    override fun getToolbarElevation() = 0f
    override fun isHomeMenuButtonEnabled() = true
    override fun getHomeMenuButtonIconResId() = R.drawable.ic_back
    override fun getHomeMenuButtonListener(): () -> Unit = { onNavigationEvent(NAVIGATION_ACTIVITY_BACK) }

    override fun configureBinding() {
        super.configureBinding()
        val adapter = CategoryFragmentsAdapter(this)
        val binding = binding ?: return

        binding.viewPagerCategories.adapter = adapter
        TabLayoutMediator(binding.tabLayoutCategories, binding.viewPagerCategories) { tab, position ->
            tab.text = getAppString(CategorySection.values()[position].titleResId)
        }.attach()
    }

    override fun onOptionsItemSelected(item: MenuItem) = when (item.itemId) {
        R.id.item_menu_accept -> {
            currentFragment?.onAcceptClicked()
            true
        }
        else -> super.onOptionsItemSelected(item)
    }

    inner class CategoryFragmentsAdapter(fragment: Fragment) : FragmentStateAdapter(fragment) {

        override fun getItemCount() = CategorySection.values().size

        override fun createFragment(position: Int) : Fragment {
            val fragment = FragmentCategorySection(CategorySection.values()[position])
            fragmentsMap[position] = fragment
            return fragment
        }
    }
}
