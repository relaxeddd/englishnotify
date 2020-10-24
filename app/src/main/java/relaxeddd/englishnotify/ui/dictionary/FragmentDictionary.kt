package relaxeddd.englishnotify.ui.dictionary

import android.annotation.SuppressLint
import android.content.res.Configuration
import android.os.Bundle
import android.os.Handler
import android.view.MenuItem
import android.view.View
import androidx.core.view.updatePaddingRelative
import androidx.navigation.Navigation
import androidx.recyclerview.widget.LinearLayoutManager
import relaxeddd.englishnotify.R
import relaxeddd.englishnotify.dialogs.DialogCheckTags
import relaxeddd.englishnotify.dialogs.DialogSortBy
import relaxeddd.englishnotify.common.*
import relaxeddd.englishnotify.databinding.FragmentDictionaryBinding
import relaxeddd.englishnotify.dialogs.DialogDeleteWords
import relaxeddd.englishnotify.ui.main.MainActivity
import java.lang.IllegalStateException

abstract class FragmentDictionary<VM : ViewModelDictionary, A : AdapterWords<*>> : BaseFragment<VM, FragmentDictionaryBinding>() {

    protected var adapter: A? = null
    private var animBlock: AnimBlock = AnimBlock(false)
    private val handler = Handler()

    private val listenerCheckTags: ListenerResult<List<String>> = object: ListenerResult<List<String>> {
        override fun onResult(result: List<String>) {
            viewModel.setFilterTags(result)
            animateDropdown(binding.containerDictionaryFilter, false, animBlock)
        }
    }

    private val listenerSortBy: ListenerResult<Int> = object: ListenerResult<Int> {
        override fun onResult(result: Int) {
            viewModel.onDialogSortByType(result)
        }
    }
    private val clickListenerCloseFilter = View.OnClickListener {
        animateDropdown(binding.containerDictionaryFilter, false, animBlock)
    }

    //------------------------------------------------------------------------------------------------------------------
    abstract fun createWordsAdapter() : A
    override fun getLayoutResId() = R.layout.fragment_dictionary
    override fun hasToolbar() = false
    override fun getFabIconResId() = R.drawable.ic_plus
    override fun getFabListener() = Navigation.createNavigateOnClickListener(R.id.action_fragmentDictionaryContainer_to_fragmentWord)

    override fun configureBinding() {
        super.configureBinding()
        binding.viewModel = viewModel
        binding.clickListenerCloseFilter = clickListenerCloseFilter
    }

    @SuppressLint("ClickableViewAccessibility")
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        adapter = createWordsAdapter()

        binding.checkBoxDictionaryShowOwnWords.setOnCheckedChangeListener(viewModel.checkedChangeListenerShowOwnWords)
        binding.recyclerViewDictionary.adapter = adapter
        binding.recyclerViewDictionary.setOnTouchListener { _, _ ->
            animateDropdown(binding.containerDictionaryFilter, false, animBlock)
            false
        }
        binding.recyclerViewDictionary.doOnApplyWindowInsets { v, insets, padding ->
            v.updatePaddingRelative(bottom = padding.bottom + insets.systemWindowInsetBottom)
        }
        viewModel.applySearch(textSearch)
        viewModel.wordsFiltered.observe(viewLifecycleOwner, { words ->
            binding.hasWords = (words != null && words.isNotEmpty())
            updateAdapter(words)
        })
        viewModel.user.observe(viewLifecycleOwner, { user ->
            adapter?.languageType = user?.learnLanguageType ?: 0
        })
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.item_menu_filter -> {
                animateDropdown(binding.containerDictionaryFilter, binding.containerDictionaryFilter.visibility == View.GONE, animBlock)
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
                    showToast(R.string.words_not_selected)
                }
                return true
            }
            else -> return super.onOptionsItemSelected(item)
        }
    }

    override fun onNavigationEvent(eventId: Int) {
        when (eventId) {
            NAVIGATION_ACTION_HIDE_FILTER -> {
                animateDropdown(binding.containerDictionaryFilter, false, animBlock)
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
            NAVIGATION_DIALOG_SORT_BY -> {
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
        binding.containerDictionaryFilter.setBackgroundResource(if (isNightMode) R.color.filter_bg_color else getPrimaryColorResId())
    }

    open fun onFragmentSelected() {}

    fun onFragmentDeselected() {
        adapter?.isSelectState = false
        textSearch = ""
        animateDropdown(binding.containerDictionaryFilter, false, animBlock)
    }

    fun onParentSearchTextChanged(textSearch: String) {
        this.textSearch = textSearch
    }

    override fun onSearchTextChanged(searchText: String) {
        if (isViewModelInitialized()) {
            viewModel.applySearch(searchText)
        }
    }

    fun onMenuFilterClicked() {
        animateDropdown(binding.containerDictionaryFilter, binding.containerDictionaryFilter.visibility == View.GONE, animBlock)
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
            showToast(R.string.words_not_selected)
        }
    }

    fun setCheckMode(isCheckMode: Boolean) {
        adapter?.isSelectState = isCheckMode
    }

    private fun updateAdapter(words: List<Word>?) {
        if (words != null && words.isNotEmpty()) {
            val isScroll = adapter?.currentList?.size != words.size
            AdapterWords.isEnabledSecondaryProgress = SharedHelper.isEnabledSecondaryProgress()
            adapter?.submitList(words)
            if (isScroll) {
                handler.postDelayed({
                    try {
                        (binding.recyclerViewDictionary.layoutManager as LinearLayoutManager?)?.scrollToPositionWithOffset(0, 0)
                    } catch (e: IllegalStateException) {}
                }, 50)
            }
        }
    }
}
