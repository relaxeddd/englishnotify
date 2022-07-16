package relaxeddd.englishnotify.ui.parsed_words

import android.os.Bundle
import android.view.LayoutInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import relaxeddd.englishnotify.R
import relaxeddd.englishnotify.common.BaseFragment
import relaxeddd.englishnotify.common.NAVIGATION_ACTIVITY_BACK
import relaxeddd.englishnotify.databinding.FragmentParsedWordsBinding

class FragmentParsedWords : BaseFragment<ViewModelParsedWords, FragmentParsedWordsBinding>() {

    private var adapter: AdapterParsedWords? = null

    override fun getToolbarTitleResId() = R.string.parsed_words
    override fun getMenuResId() = R.menu.menu_accept
    override fun isHomeMenuButtonEnabled() = true
    override fun getHomeMenuButtonIconResId() = R.drawable.ic_back
    override fun getHomeMenuButtonListener(): () -> Unit = { onNavigationEvent(NAVIGATION_ACTIVITY_BACK) }

    override val viewModel: ViewModelParsedWords by viewModels()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        binding = FragmentParsedWordsBinding.inflate(inflater)
        return binding?.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding?.apply {
            adapter = AdapterParsedWords(viewModel)
            recyclerViewParsedWords.layoutManager = LinearLayoutManager(context)
            recyclerViewParsedWords.adapter = adapter
        }
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.item_menu_accept -> {
                viewModel.onClickedAccept()
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    override fun subscribeToViewModel() {
        super.subscribeToViewModel()

        viewModel.parsedWords.observe(viewLifecycleOwner) { words ->
            adapter?.submitList(words)
        }
    }
}
