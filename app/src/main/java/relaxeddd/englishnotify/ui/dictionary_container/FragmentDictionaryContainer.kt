package relaxeddd.englishnotify.ui.dictionary_container

import android.view.MenuItem
import androidx.fragment.app.Fragment
import androidx.viewpager2.adapter.FragmentStateAdapter
import androidx.viewpager2.widget.ViewPager2
import com.google.android.material.tabs.TabLayoutMediator
import relaxeddd.englishnotify.R
import relaxeddd.englishnotify.common.BaseFragment
import relaxeddd.englishnotify.common.InjectorUtils
import relaxeddd.englishnotify.databinding.FragmentDictionaryContainerBinding
import relaxeddd.englishnotify.ui.dictionary.FragmentDictionary
import relaxeddd.englishnotify.ui.dictionary_all.FragmentDictionaryAll
import relaxeddd.englishnotify.ui.dictionary_exercises.FragmentDictionaryExercises
import relaxeddd.englishnotify.ui.dictionary_know.FragmentDictionaryKnow
import relaxeddd.englishnotify.ui.dictionary_own.FragmentDictionaryOwn

class FragmentDictionaryContainer : BaseFragment<ViewModelDictionaryContainer, FragmentDictionaryContainerBinding>() {

    private var adapterFragments = HashMap<Int, FragmentDictionary<*, *>>()
    private val currentFragment: BaseFragment<*, *>?
        get() = adapterFragments[binding.viewPagerDictionaryContainer.currentItem]

    override fun getLayoutResId() = R.layout.fragment_dictionary_container
    override fun getToolbarTitleResId() = R.string.dictionary
    override fun getMenuResId() = R.menu.menu_fragment_dictionary
    override fun getSearchMenuItemId() = R.id.item_menu_search_dictionary
    override fun getViewModelFactory() = InjectorUtils.provideDictionaryContainerViewModelFactory()
    override fun getViewModelClass() = ViewModelDictionaryContainer::class.java

    override fun configureBinding() {
        super.configureBinding()
        binding.viewPagerDictionaryContainer.adapter = DictionaryFragmentsAdapter(this)
        TabLayoutMediator(binding.tabLayoutDictionaryContainer, binding.viewPagerDictionaryContainer) { tab, position ->
            tab.text = getString(when(position) {
                DictionaryTab.OWN.ordinal -> R.string.own_words
                DictionaryTab.EXERCISES.ordinal -> R.string.exercises
                DictionaryTab.KNOW.ordinal -> R.string.already_know
                else -> R.string.all_words
            })
        }.attach()
        binding.viewPagerDictionaryContainer.registerOnPageChangeCallback(object: ViewPager2.OnPageChangeCallback() {
            override fun onPageSelected(position: Int) {
                activity?.invalidateOptionsMenu()
            }
        })
    }

    override fun onDestroy() {
        adapterFragments.clear()
        super.onDestroy()
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return adapterFragments[binding.viewPagerDictionaryContainer.currentItem]?.onOptionsItemSelected(item) ?: super.onOptionsItemSelected(item)
    }

    inner class DictionaryFragmentsAdapter(fragment: Fragment) : FragmentStateAdapter(fragment) {

        override fun getItemCount() = 4

        override fun createFragment(position: Int) : Fragment {
            val fragment = when (position) {
                DictionaryTab.OWN.ordinal -> FragmentDictionaryOwn()
                DictionaryTab.EXERCISES.ordinal -> FragmentDictionaryExercises()
                DictionaryTab.KNOW.ordinal -> FragmentDictionaryKnow()
                else -> FragmentDictionaryAll()
            }
            adapterFragments[position] = fragment

            return fragment
        }
    }

    private enum class DictionaryTab {
        ALL, OWN, EXERCISES, KNOW
    }
}
