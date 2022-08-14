package relaxeddd.englishnotify.ui.dictionary

import android.annotation.SuppressLint
import android.content.res.Configuration
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.LayoutInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.core.view.updatePaddingRelative
import androidx.lifecycle.lifecycleScope
import androidx.navigation.Navigation
import androidx.recyclerview.widget.LinearLayoutManager
import relaxeddd.englishnotify.R
import relaxeddd.englishnotify.common.*
import relaxeddd.englishnotify.databinding.FragmentDictionaryBinding
import relaxeddd.englishnotify.dialogs.DialogCheckTags
import relaxeddd.englishnotify.dialogs.DialogDeleteWords
import relaxeddd.englishnotify.dialogs.DialogSortBy
import relaxeddd.englishnotify.domain_words.entity.Word
import relaxeddd.englishnotify.ui.main.MainActivity
import relaxeddd.englishnotify.view_base.BaseFragment
import relaxeddd.englishnotify.view_base.interfaces.ListenerResult

abstract class FragmentDictionary<VM : ViewModelDictionary, A : AdapterWords<*>> : BaseFragment<VM, FragmentDictionaryBinding>() {

    protected var adapter: A? = null
    private var animBlock: AnimBlock = AnimBlock(false)
    private val handler = Handler(Looper.getMainLooper())

    private val listenerCheckTags: ListenerResult<List<String>> = object: ListenerResult<List<String>> {
        override fun onResult(result: List<String>) {
            viewModel.setFilterTags(result)
            binding?.containerDictionaryFilter?.let { animateDropdown(it, false, animBlock) }
        }
    }

    private val listenerSortBy: ListenerResult<Int> = object: ListenerResult<Int> {
        override fun onResult(result: Int) {
            viewModel.onDialogSortByType(result)
        }
    }
    private val clickListenerCloseFilter = View.OnClickListener {
        binding?.containerDictionaryFilter?.let { animateDropdown(it, false, animBlock) }
    }

    //------------------------------------------------------------------------------------------------------------------
    abstract fun createWordsAdapter() : A
    override fun hasToolbar() = false
    override fun getFabIconResId() = R.drawable.ic_plus
    override fun getFabListener() = Navigation.createNavigateOnClickListener(R.id.action_fragmentDictionaryContainer_to_fragmentWord)

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        binding = FragmentDictionaryBinding.inflate(inflater)
        return binding?.root
    }

    @SuppressLint("ClickableViewAccessibility")
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding?.apply {
            adapter = createWordsAdapter()

            recyclerViewDictionary.adapter = adapter
            recyclerViewDictionary.setOnTouchListener { _, _ ->
                animateDropdown(containerDictionaryFilter, false, animBlock)
                false
            }
            recyclerViewDictionary.doOnApplyWindowInsets { v, insets, padding ->
                v.updatePaddingRelative(bottom = padding.bottom + insets.systemWindowInsetBottom)
            }
            containerDictionaryFilterTags.setOnClickListener {
                viewModel.onClickedFilterTags()
            }
            containerSortedBy.setOnClickListener {
                viewModel.onClickedSortedBy()
            }
        }
    }

    override fun subscribeToViewModel() {
        super.subscribeToViewModel()

        viewModel.applySearch(textSearch)
        viewModel.wordsFiltered.observe(viewLifecycleOwner) { words ->
            val hasWords = words.isNotEmpty()

            binding?.recyclerViewDictionary?.isVisible = hasWords
            binding?.textDictionaryNoWords?.isVisible = !hasWords
            updateAdapter(words)
        }
        viewModel.filterTags.observe(viewLifecycleOwner) {
            binding?.textDictionaryFilterTagsValues?.isVisible = it.isNotEmpty()
            binding?.textDictionaryFilterTagsValues?.text = if (it.isEmpty()) "" else it.toString()
        }
        viewModel.sortByType.observe(viewLifecycleOwner) {
            binding?.textDictionarySortByValue?.text = it.getTitle(requireContext())
        }
        lifecycleScope.launchWhenCreated {
            prefs.learnLanguageTypeFlow.collect {
                adapter?.languageType = it
            }
        }
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.item_menu_filter -> {
                binding?.containerDictionaryFilter?.let { animateDropdown(it, it.visibility == View.GONE, animBlock) }
                return true
            }
            R.id.item_menu_check -> {
                setCheckMode(true)
                adapter?.isSelectState = true
                return true
            }
            R.id.item_menu_cancel_check -> {
                setCheckMode(false)
                adapter?.isSelectState = false
                return true
            }
            R.id.item_menu_check_all -> {
                adapter?.checkAll()
                return true
            }
            R.id.item_menu_delete -> {
                val selectedWords = HashSet(adapter?.checkList ?: emptyList())

                if (selectedWords.isNotEmpty()) {
                    val dialog = DialogDeleteWords()
                    dialog.confirmListener = object : ListenerResult<Boolean> {
                        override fun onResult(result: Boolean) {
                            if (result) {
                                viewModel.deleteWords(HashSet(adapter?.checkList ?: emptyList()))
                                setCheckMode(false)
                                adapter?.isSelectState = false
                            }
                        }
                    }
                    dialog.show(this@FragmentDictionary.childFragmentManager, "Confirm delete Dialog")
                } else {
                    showToast(requireContext(), R.string.words_not_selected)
                }
                return true
            }
            else -> return super.onOptionsItemSelected(item)
        }
    }

    override fun onNavigationEvent(eventId: Int) {
        when (eventId) {
            NAVIGATION_ACTION_HIDE_FILTER -> {
                binding?.containerDictionaryFilter?.let { animateDropdown(it, false, animBlock) }
            }
            NAVIGATION_PLAY_WORD -> {
                val ac = activity
                if (ac is MainActivity) {
                    ac.playWord(viewModel.playWord)
                }
            }
            NAVIGATION_FRAGMENT_WORD -> {
                val editWord = viewModel.editWord ?: return
                val args = Bundle()

                viewModel.editWord = null
                args.putString(ID, editWord.id)
                args.putString(ENG, editWord.eng)
                args.putString(TRANSCRIPTION, editWord.transcription)
                args.putString(RUS, editWord.rus)
                navigate(R.id.action_global_fragmentEditWord, args)
            }
            NAVIGATION_DIALOG_CHECK_TAGS -> {
                val dialog = DialogCheckTags()
                val args = Bundle()
                args.putStringArray(ITEMS, viewModel.tags.toTypedArray())
                args.putStringArray(CHECKED_ITEMS, viewModel.filterTags.value?.toTypedArray())
                dialog.arguments = args
                dialog.listener = listenerCheckTags
                dialog.show(this@FragmentDictionary.childFragmentManager, "Check tags Dialog")
            }
            NAVIGATION_DIALOG_SORTED_BY -> {
                val dialog = DialogSortBy()
                val args = Bundle()
                args.putInt(SELECTED_ITEM, viewModel.sortByType.value?.ordinal ?: 0)
                dialog.arguments = args
                dialog.listener = listenerSortBy
                dialog.show(this@FragmentDictionary.childFragmentManager, "Repeat Dialog")
            }
            else -> super.onNavigationEvent(eventId)
        }
    }

    override fun setupThemeColors() {
        super.setupThemeColors()
        val isNightMode = resources.configuration.uiMode and Configuration.UI_MODE_NIGHT_MASK == Configuration.UI_MODE_NIGHT_YES
        binding?.containerDictionaryFilter?.setBackgroundResource(if (isNightMode) R.color.filter_bg_color else getPrimaryColorResId(prefs.getAppThemeType()))
    }

    open fun onFragmentSelected() {}

    fun onFragmentDeselected() {
        adapter?.isSelectState = false
        textSearch = ""
        binding?.containerDictionaryFilter?.let { animateDropdown(it, false, animBlock) }
    }

    fun onParentSearchTextChanged(textSearch: String) {
        this.textSearch = textSearch
    }

    override fun onSearchTextChanged(searchText: String) {
        viewModel.applySearch(searchText)
    }

    fun onMenuFilterClicked() {
        binding?.containerDictionaryFilter?.let { animateDropdown(it, it.visibility == View.GONE, animBlock) }
    }

    fun onMenuCheckAllClicked() {
        adapter?.checkAll()
    }

    fun onMenuDeleteClicked() {
        val selectedWords = HashSet(adapter?.checkList ?: emptyList())

        if (selectedWords.isNotEmpty()) {
            val dialog = DialogDeleteWords()
            dialog.confirmListener = object : ListenerResult<Boolean> {
                override fun onResult(result: Boolean) {
                    if (result) {
                        viewModel.deleteWords(HashSet(adapter?.checkList ?: emptyList()))
                        setCheckMode(false)
                        adapter?.isSelectState = false
                    }
                }
            }
            dialog.show(this@FragmentDictionary.childFragmentManager, "Confirm delete Dialog")
        } else {
            showToast(requireContext(), R.string.words_not_selected)
        }
    }

    fun setCheckMode(isCheckMode: Boolean) {
        adapter?.isSelectState = isCheckMode
    }

    private fun updateAdapter(words: List<Word>?) {
        if (words != null && words.isNotEmpty()) {
            val isScroll = adapter?.currentList?.size != words.size
            adapter?.submitList(words)
            if (isScroll) {
                handler.postDelayed({
                    try {
                        (binding?.recyclerViewDictionary?.layoutManager as? LinearLayoutManager)?.scrollToPositionWithOffset(0, 0)
                    } catch (e: IllegalStateException) {}
                }, 50)
            }
        }
    }
}
