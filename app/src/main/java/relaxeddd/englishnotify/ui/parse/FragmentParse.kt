package relaxeddd.englishnotify.ui.parse

import android.view.MenuItem
import androidx.fragment.app.viewModels
import androidx.navigation.Navigation
import relaxeddd.englishnotify.R
import relaxeddd.englishnotify.common.BaseFragment
import relaxeddd.englishnotify.common.NAVIGATION_ACTIVITY_BACK
import relaxeddd.englishnotify.common.NAVIGATION_FRAGMENT_PARSED_WORDS
import relaxeddd.englishnotify.common.getPrimaryColorResId
import relaxeddd.englishnotify.common.hideKeyboard
import relaxeddd.englishnotify.common.myNavigate
import relaxeddd.englishnotify.databinding.FragmentParseBinding
import relaxeddd.englishnotify.ui.categories.AdapterCategories

class FragmentParse: BaseFragment<ViewModelParse, FragmentParseBinding>() {

    private var adapter: AdapterCategories? = null

    override fun getLayoutResId() = R.layout.fragment_parse
    override fun getToolbarTitleResId() = R.string.add_multiple_words
    override fun getMenuResId() = R.menu.menu_accept
    override fun isHomeMenuButtonEnabled() = true
    override fun getHomeMenuButtonIconResId() = R.drawable.ic_back
    override fun getHomeMenuButtonListener(): () -> Unit = { onNavigationEvent(NAVIGATION_ACTIVITY_BACK) }

    override val viewModel: ViewModelParse by viewModels()

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when(item.itemId) {
            R.id.item_menu_accept -> {
                handleClickedAccept()
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    override fun subscribeToViewModel() {
        super.subscribeToViewModel()

        val binding = binding ?: return
        binding.viewModel = viewModel

        adapter = AdapterCategories(viewModel)
        binding.recyclerViewParseOwnCategories.itemAnimator = null
        binding.recyclerViewParseOwnCategories.adapter = adapter
        viewModel.categories.observe(viewLifecycleOwner) { items ->
            if (items != null && items.isNotEmpty()) adapter?.submitList(items)
        }
    }

    override fun onNavigationEvent(eventId: Int) {
        when(eventId) {
            NAVIGATION_FRAGMENT_PARSED_WORDS -> {
                Navigation.findNavController(view ?: return)
                    .myNavigate(R.id.action_fragmentParse_to_fragmentParsedWords)
            }
            else -> super.onNavigationEvent(eventId)
        }
    }

    override fun setupThemeColors() {
        val binding = binding ?: return
        binding.containerTextParseDelimiterInner.boxStrokeColor = getPrimaryColorResId()
        binding.containerTextParseDelimiterWords.boxStrokeColor = getPrimaryColorResId()
        binding.containerTextParse.boxStrokeColor = getPrimaryColorResId()
        binding.containerTextParseOwnTag.boxStrokeColor = getPrimaryColorResId()
    }

    private fun handleClickedAccept() {
        val binding = binding ?: return
        val delimiterInner = binding.textParseDelimiterInner.text.toString()
        val delimiterWords = binding.textParseDelimiterWords.text.toString()
        val textWords = binding.textParseWords.text.toString()
        val ownCategory = binding.textInputOwnTag.text.toString()
        var isValid = true

        hideKeyboard()

        if (delimiterInner.isEmpty()) {
            binding.textParseDelimiterInner.error = getString(R.string.delimiter_should_not_be_empty)
            isValid = false
        }
        if (delimiterWords.isEmpty()) {
            binding.textParseDelimiterWords.error = getString(R.string.delimiter_should_not_be_empty)
            isValid = false
        }
        if (textWords.isEmpty()) {
            binding.textParseWords.error = getString(R.string.words_should_not_be_empty)
            isValid = false
        }
        if (isValid && delimiterInner == delimiterWords) {
            binding.textParseDelimiterWords.error = getString(R.string.delimiters_should_be_different)
            isValid = false
        }

        if (isValid) {
            viewModel.onClickedParseWords(textWords, delimiterInner, delimiterWords, ownCategory)
        }
    }

    private fun hideKeyboard() {
        val binding = binding ?: return
        when {
            binding.textParseDelimiterInner.hasFocus() -> hideKeyboard(binding.textParseDelimiterInner)
            binding.textParseDelimiterWords.hasFocus() -> hideKeyboard(binding.textParseDelimiterWords)
            binding.textParseWords.hasFocus() -> hideKeyboard(binding.textParseWords)
            binding.textInputOwnTag.hasFocus() -> hideKeyboard(binding.textInputOwnTag)
        }
    }
}
