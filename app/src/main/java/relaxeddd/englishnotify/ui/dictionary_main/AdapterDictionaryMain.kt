package relaxeddd.englishnotify.ui.dictionary_main

import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentPagerAdapter
import relaxeddd.englishnotify.R
import relaxeddd.englishnotify.common.getString
import relaxeddd.englishnotify.ui.dictionary_all.FragmentDictionaryAll
import relaxeddd.englishnotify.ui.dictionary_own.FragmentDictionaryOwn

class AdapterDictionaryMain(fm: FragmentManager) : FragmentPagerAdapter(fm) {

    val TAB_ALL = 0
    val TAB_OWN = 1

    var fragmentDictionaryAll: FragmentDictionaryAll? = null
    var fragmentDictionaryOwn: FragmentDictionaryOwn? = null

    override fun getCount() = 2

    override fun getItem(position: Int) = when (position) {
        TAB_OWN -> {
            val fragment = FragmentDictionaryOwn()
            fragmentDictionaryOwn = fragment
            fragment
        }
        else -> {
            val fragment = FragmentDictionaryAll()
            fragmentDictionaryAll = fragment
            fragment
        }
    }

    override fun getPageTitle(position: Int) = when(position) {
        TAB_OWN -> getString(R.string.own_category)
        else -> getString(R.string.all_words)
    }

    fun onPageSelected(position: Int) {
        when (position) {
            TAB_OWN -> {
                fragmentDictionaryAll?.onFragmentDeselected()
                fragmentDictionaryOwn?.onFragmentSelected()
            }
            else -> {
                fragmentDictionaryOwn?.onFragmentDeselected()
                fragmentDictionaryAll?.onFragmentSelected()
            }
        }
    }
}