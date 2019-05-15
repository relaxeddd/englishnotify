package relaxeddd.englishnotify.ui.dictionary_main

import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentPagerAdapter
import relaxeddd.englishnotify.R
import relaxeddd.englishnotify.common.getString
import relaxeddd.englishnotify.ui.dictionary_all.FragmentDictionaryAll
import relaxeddd.englishnotify.ui.dictionary_exercises.FragmentDictionaryExercises
import relaxeddd.englishnotify.ui.dictionary_own.FragmentDictionaryOwn

class AdapterDictionaryMain(fm: FragmentManager) : FragmentPagerAdapter(fm, BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT) {

    companion object {
        private const val TAB_ALL = 0
        private const val TAB_OWN = 1
        private const val TAB_EXERCISES = 2
    }

    private var mPosition = 0

    private var fragmentDictionaryAll: FragmentDictionaryAll? = null
    private var fragmentDictionaryOwn: FragmentDictionaryOwn? = null
    private var fragmentDictionaryExercises: FragmentDictionaryExercises? = null

    override fun getCount() = 3

    override fun getItem(position: Int) = when (position) {
        TAB_OWN -> {
            val fragment = FragmentDictionaryOwn()
            fragmentDictionaryOwn = fragment
            fragment
        }
        TAB_EXERCISES -> {
            val fragment = FragmentDictionaryExercises()
            fragmentDictionaryExercises = fragment
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
        TAB_EXERCISES -> getString(R.string.exercises)
        else -> getString(R.string.all_words)
    }

    fun onPageSelected(position: Int) {
        when (position) {
            TAB_OWN -> {
                if (mPosition == TAB_ALL) {
                    fragmentDictionaryAll?.onFragmentDeselected()
                } else if (mPosition == TAB_EXERCISES) {
                    fragmentDictionaryExercises?.onFragmentDeselected()
                }

                fragmentDictionaryOwn?.onFragmentSelected()
            }
            TAB_EXERCISES -> {
                if (mPosition == TAB_ALL) {
                    fragmentDictionaryAll?.onFragmentDeselected()
                } else if (mPosition == TAB_OWN) {
                    fragmentDictionaryOwn?.onFragmentDeselected()
                }

                fragmentDictionaryExercises?.onFragmentSelected()
            }
            else -> {
                if (mPosition == TAB_EXERCISES) {
                    fragmentDictionaryExercises?.onFragmentDeselected()
                } else if (mPosition == TAB_OWN) {
                    fragmentDictionaryOwn?.onFragmentDeselected()
                }

                fragmentDictionaryAll?.onFragmentSelected()
            }
        }
        mPosition = position
    }
}