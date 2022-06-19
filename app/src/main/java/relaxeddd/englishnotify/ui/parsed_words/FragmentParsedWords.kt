package relaxeddd.englishnotify.ui.parsed_words

import android.os.Bundle
import android.view.MenuItem
import android.view.View
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import relaxeddd.englishnotify.R
import relaxeddd.englishnotify.common.BaseFragment
import relaxeddd.englishnotify.common.NAVIGATION_ACTIVITY_BACK
import relaxeddd.englishnotify.databinding.FragmentParsedWordsBinding

class FragmentParsedWords : BaseFragment<ViewModelParsedWords, FragmentParsedWordsBinding>() {

    override fun getLayoutResId() = R.layout.fragment_parsed_words
    override fun getToolbarTitleResId() = R.string.parsed_words
    override fun getMenuResId() = R.menu.menu_accept
    override fun isHomeMenuButtonEnabled() = true
    override fun getHomeMenuButtonIconResId() = R.drawable.ic_back
    override fun getHomeMenuButtonListener(): () -> Unit = { onNavigationEvent(NAVIGATION_ACTIVITY_BACK) }

    override val viewModel: ViewModelParsedWords by viewModels()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val adapter = AdapterParsedWords(viewModel)
        val binding = binding ?: return
        binding.recyclerViewParsedWords.layoutManager = LinearLayoutManager(context)
        binding.recyclerViewParsedWords.adapter = adapter

        viewModel.parsedWords.observe(viewLifecycleOwner) { words ->
            adapter.submitList(words)
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
}
