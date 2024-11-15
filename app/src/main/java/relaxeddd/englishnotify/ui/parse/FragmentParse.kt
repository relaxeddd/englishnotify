package relaxeddd.englishnotify.ui.parse

import android.os.Bundle
import android.view.LayoutInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.Navigation
import relaxeddd.englishnotify.R
import relaxeddd.englishnotify.common.NAVIGATION_ACTIVITY_BACK
import relaxeddd.englishnotify.common.NAVIGATION_FRAGMENT_PARSED_WORDS
import relaxeddd.englishnotify.common.getPrimaryColorResId
import relaxeddd.englishnotify.common.hideKeyboard
import relaxeddd.englishnotify.common.myNavigate
import relaxeddd.englishnotify.databinding.FragmentParseBinding
import relaxeddd.englishnotify.preferences.Preferences
import relaxeddd.englishnotify.ui.categories.AdapterCategories
import relaxeddd.englishnotify.view_base.BaseFragment
import javax.inject.Inject

class FragmentParse: BaseFragment<ViewModelParse, FragmentParseBinding>() {

    @Inject
    override lateinit var prefs: Preferences

    @Inject
    lateinit var viewModelFactory: ViewModelProvider.Factory

    override val viewModel by viewModels<ViewModelParse> { viewModelFactory }

    private var adapter: AdapterCategories? = null

    override fun getToolbarTitleResId() = R.string.add_multiple_words
    override fun getMenuResId() = R.menu.menu_accept
    override fun isHomeMenuButtonEnabled() = true
    override fun getHomeMenuButtonIconResId() = R.drawable.ic_back
    override fun getHomeMenuButtonListener(): () -> Unit = { onNavigationEvent(NAVIGATION_ACTIVITY_BACK) }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        binding = FragmentParseBinding.inflate(inflater)
        return binding?.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding?.apply {
            adapter = AdapterCategories(viewModel)
            recyclerViewParseOwnCategories.itemAnimator = null
            recyclerViewParseOwnCategories.adapter = adapter
        }
    }

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
        binding.containerTextParseDelimiterInner.boxStrokeColor = getPrimaryColorResId(prefs.getAppThemeType())
        binding.containerTextParseDelimiterWords.boxStrokeColor = getPrimaryColorResId(prefs.getAppThemeType())
        binding.containerTextParse.boxStrokeColor = getPrimaryColorResId(prefs.getAppThemeType())
        binding.containerTextParseOwnTag.boxStrokeColor = getPrimaryColorResId(prefs.getAppThemeType())
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
