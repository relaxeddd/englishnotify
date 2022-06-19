package relaxeddd.englishnotify.ui.categories

import android.view.MenuItem
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.google.android.material.tabs.TabLayoutMediator
import relaxeddd.englishnotify.R
import relaxeddd.englishnotify.common.BaseFragment
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
    override fun getMenuResId() = R.menu.menu_accept
    override fun getToolbarElevation() = 0f
    override fun isHomeMenuButtonEnabled() = true
    override fun getHomeMenuButtonIconResId() = R.drawable.ic_back
    override fun getHomeMenuButtonListener(): () -> Unit = { onNavigationEvent(NAVIGATION_ACTIVITY_BACK) }

    override val viewModel: ViewModelCategories by viewModels()

    override fun subscribeToViewModel() {
        super.subscribeToViewModel()
        val adapter = CategoryFragmentsAdapter(this)
        val binding = binding ?: return

        binding.viewPagerCategories.adapter = adapter
        TabLayoutMediator(binding.tabLayoutCategories, binding.viewPagerCategories) { tab, position ->
            tab.text = getAppString(CategorySection.OWN_CATEGORIES.titleResId)
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

        override fun getItemCount() = 1

        override fun createFragment(position: Int) : Fragment {
            val fragment = FragmentCategorySection()
            fragmentsMap[position] = fragment
            return fragment
        }
    }
}
