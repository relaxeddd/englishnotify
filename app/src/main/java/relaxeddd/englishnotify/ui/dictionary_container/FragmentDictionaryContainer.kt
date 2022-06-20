package relaxeddd.englishnotify.ui.dictionary_container

import android.os.Bundle
import android.view.LayoutInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.viewpager2.adapter.FragmentStateAdapter
import androidx.viewpager2.widget.ViewPager2
import com.google.android.material.tabs.TabLayoutMediator
import relaxeddd.englishnotify.R
import relaxeddd.englishnotify.common.BaseFragment
import relaxeddd.englishnotify.databinding.FragmentDictionaryContainerBinding
import relaxeddd.englishnotify.model.preferences.SharedHelper
import relaxeddd.englishnotify.ui.dictionary.FragmentDictionary
import relaxeddd.englishnotify.ui.dictionary_all.FragmentDictionaryAll
import relaxeddd.englishnotify.ui.dictionary_know.FragmentDictionaryKnow

class FragmentDictionaryContainer : BaseFragment<ViewModelDictionaryContainer, FragmentDictionaryContainerBinding>() {

    private var adapterFragmentsMap = HashMap<Int, FragmentDictionary<*, *>?>()
    private var currentPosition: Int = SharedHelper.getDictionaryTabPosition()
    private val currentFragment: FragmentDictionary<*, *>?
        get() = adapterFragmentsMap[currentPosition]

    private val onPageChangeCallback = object: ViewPager2.OnPageChangeCallback() {
        override fun onPageSelected(position: Int) {
            if (currentPosition != position && adapterFragmentsMap.size > currentPosition) {
                val previousFragment = adapterFragmentsMap[currentPosition]
                previousFragment?.onFragmentDeselected()

                if (adapterFragmentsMap.size > position) {
                    val nextFragment = adapterFragmentsMap[position]
                    nextFragment?.onParentSearchTextChanged(textSearch)
                }
            }
            updateMenuIcons(false)
            currentPosition = position
            SharedHelper.setDictionaryTabPosition(position)
        }
    }

    override fun getToolbarTitleResId() = R.string.dictionary
    override fun getMenuResId() = R.menu.menu_fragment_dictionary
    override fun getSearchMenuItemId() = R.id.item_menu_search_dictionary
    override fun isTopLevelFragment() = true

    override val viewModel: ViewModelDictionaryContainer by viewModels()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        binding = FragmentDictionaryContainerBinding.inflate(inflater)
        return binding?.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val adapter = DictionaryFragmentsAdapter(this)

        binding?.apply {
            viewPagerDictionaryContainer.adapter = adapter
            TabLayoutMediator(tabLayoutDictionaryContainer, viewPagerDictionaryContainer) { tab, position ->
                tab.text = getString(when(position) {
                    DictionaryTab.KNOW.ordinal -> R.string.already_know
                    else -> R.string.all_words
                })
            }.attach()
            viewPagerDictionaryContainer.registerOnPageChangeCallback(onPageChangeCallback)
        }
    }

    override fun onStart() {
        super.onStart()
        if (binding?.viewPagerDictionaryContainer?.currentItem != SharedHelper.getDictionaryTabPosition()) {
            binding?.viewPagerDictionaryContainer?.setCurrentItem(currentPosition, false)
        }
    }

    override fun onDestroyView() {
        binding?.viewPagerDictionaryContainer?.unregisterOnPageChangeCallback(onPageChangeCallback)
        super.onDestroyView()
    }

    override fun onDetach() {
        super.onDetach()
        adapterFragmentsMap.clear()
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.item_menu_filter -> {
                currentFragment?.onMenuFilterClicked()
                return true
            }
            R.id.item_menu_check -> {
                updateMenuIcons(true)
                return true
            }
            R.id.item_menu_cancel_check -> {
                updateMenuIcons(false)
                return true
            }
            R.id.item_menu_check_all -> {
                currentFragment?.onMenuCheckAllClicked()
                return true
            }
            R.id.item_menu_delete -> {
                currentFragment?.onMenuDeleteClicked()
                return true
            }
            else -> return super.onOptionsItemSelected(item)
        }
    }

    override fun onSearchTextChanged(searchText: String) {
        currentFragment?.onParentSearchTextChanged(searchText)
    }

    override fun onSearchViewStateChanged(isCollapsed: Boolean) {
        super.onSearchViewStateChanged(isCollapsed)
        updateMenuIcons(false, !isCollapsed)
    }

    private fun updateMenuIcons(isCheckMode: Boolean, isSearchMode: Boolean = searchView?.isIconified == false) {
        currentFragment?.setCheckMode(isCheckMode && !isSearchMode)

        menu?.findItem(R.id.item_menu_check)?.apply {
            val isVisibleMenuItem = !isCheckMode && !isSearchMode
            if (isVisible != isVisibleMenuItem) isVisible = isVisibleMenuItem
        }
        menu?.findItem(getSearchMenuItemId())?.apply {
            val isVisibleMenuItem = !isCheckMode && !isSearchMode
            if (isVisible != isVisibleMenuItem) isVisible = isVisibleMenuItem
        }
        menu?.findItem(R.id.item_menu_filter)?.apply {
            val isVisibleMenuItem = !isCheckMode && !isSearchMode
            if (isVisible != isVisibleMenuItem) isVisible = isVisibleMenuItem
        }
        menu?.findItem(R.id.item_menu_check_all)?.apply {
            val isVisibleMenuItem = isCheckMode && !isSearchMode
            if (isVisible != isVisibleMenuItem) isVisible = isVisibleMenuItem
        }
        menu?.findItem(R.id.item_menu_cancel_check)?.apply {
            val isVisibleMenuItem = isCheckMode && !isSearchMode
            if (isVisible != isVisibleMenuItem) isVisible = isVisibleMenuItem
        }
        menu?.findItem(R.id.item_menu_delete)?.apply {
            val isVisibleMenuItem = isCheckMode && !isSearchMode
            if (isVisible != isVisibleMenuItem) isVisible = isVisibleMenuItem
        }
    }

    inner class DictionaryFragmentsAdapter(fragment: Fragment) : FragmentStateAdapter(fragment) {

        override fun getItemCount() = DictionaryTab.values().size

        override fun createFragment(position: Int) : Fragment {
            val fragment = when (position) {
                DictionaryTab.KNOW.ordinal -> FragmentDictionaryKnow()
                else -> FragmentDictionaryAll()
            }
            adapterFragmentsMap[position] = fragment
            fragment.onParentSearchTextChanged(textSearch)

            return fragment
        }
    }

    private enum class DictionaryTab {
        ALL, KNOW
    }
}
