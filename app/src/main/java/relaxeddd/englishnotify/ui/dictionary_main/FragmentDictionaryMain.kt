package relaxeddd.englishnotify.ui.dictionary_main

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.viewpager.widget.ViewPager
import kotlinx.android.synthetic.main.fragment_dictionary_main.*
import relaxeddd.englishnotify.R

class FragmentDictionaryMain : Fragment() {

    private lateinit var pagerAdapter: AdapterDictionaryMain

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_dictionary_main, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        pagerAdapter = AdapterDictionaryMain(childFragmentManager)
        tab_layout_dictionary.setupWithViewPager(view_pager_dictionary)
        view_pager_dictionary.adapter = pagerAdapter

        view_pager_dictionary.addOnPageChangeListener(object: ViewPager.OnPageChangeListener {
            override fun onPageScrollStateChanged(state: Int) {}
            override fun onPageScrolled(position: Int, positionOffset: Float, positionOffsetPixels: Int) {}
            override fun onPageSelected(position: Int) {
                pagerAdapter.onPageSelected(position)
            }
        })
    }
}