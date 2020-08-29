package relaxeddd.englishnotify.ui.dictionary

import android.annotation.SuppressLint
import android.content.res.ColorStateList
import android.os.Bundle
import android.os.Handler
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.databinding.ViewDataBinding
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import kotlinx.android.synthetic.main.fragment_dictionary.*
import relaxeddd.englishnotify.R
import relaxeddd.englishnotify.dialogs.DialogCheckTags
import relaxeddd.englishnotify.dialogs.DialogSortBy
import relaxeddd.englishnotify.common.*
import relaxeddd.englishnotify.dialogs.DialogDeleteWords
import relaxeddd.englishnotify.ui.main.MainActivity
import java.lang.IllegalStateException

abstract class FragmentDictionary<VM : ViewModelDictionary, B : ViewDataBinding, A : AdapterWords<*>> : BaseFragment<VM, B>() {

    protected lateinit var adapter: A
    private var animBlock: AnimBlock = AnimBlock(false)
    private val handler = Handler()

    private val listenerCheckTags: ListenerResult<List<String>> = object: ListenerResult<List<String>> {
        override fun onResult(result: List<String>) {
            viewModel.setFilterTags(result)
            animateDropdown(getCardViewFilter(), false, animBlock)
        }
    }

    private val listenerSortBy: ListenerResult<Int> = object: ListenerResult<Int> {
        override fun onResult(result: Int) {
            viewModel.onDialogSortByType(result)
        }
    }
    protected val clickListenerCloseFilter = View.OnClickListener {
        animateDropdown(getCardViewFilter(), false, animBlock)
    }

    //------------------------------------------------------------------------------------------------------------------
    abstract fun createWordsAdapter() : A
    abstract fun getRecyclerViewWords() : RecyclerView
    abstract fun getCardViewFilter() : ViewGroup
    override fun getLayoutResId() = R.layout.fragment_dictionary

    @SuppressLint("ClickableViewAccessibility")
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        adapter = createWordsAdapter()

        if (check_box_dictionary_show_own_words != null) {
            check_box_dictionary_show_own_words.setOnCheckedChangeListener(viewModel.checkedChangeListenerShowOwnWords)
        }
        getRecyclerViewWords().adapter = adapter
        getRecyclerViewWords().setOnTouchListener { _, _ ->
            animateDropdown(getCardViewFilter(), false, animBlock)
            false
        }
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.item_menu_filter -> {
                animateDropdown(getCardViewFilter(), getCardViewFilter().visibility == View.GONE, animBlock)
                return true
            }
            R.id.item_menu_check -> {
                setCheckMode(true)
                adapter.isSelectState = true
                return true
            }
            R.id.item_menu_cancel_check -> {
                setCheckMode(false)
                adapter.isSelectState = false
                return true
            }
            R.id.item_menu_check_all -> {
                adapter.checkAll()
                return true
            }
            R.id.item_menu_delete -> {
                val selectedWords = HashSet(adapter.checkList)

                if (selectedWords.isNotEmpty()) {
                    val dialog = DialogDeleteWords()
                    dialog.confirmListener = object : ListenerResult<Boolean> {
                        override fun onResult(result: Boolean) {
                            if (result) {
                                viewModel.deleteWords(HashSet(adapter.checkList))
                                setCheckMode(false)
                                adapter.isSelectState = false
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
                animateDropdown(getCardViewFilter(), false, animBlock)
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

    override fun onSearchTextChanged(searchText: String) {
        super.onSearchTextChanged(searchText)
        viewModel.applySearch(searchText)
    }

    override fun setupThemeColors() {
        val context = context ?: return
        button_dictionary_add_word?.backgroundTintList = ColorStateList.valueOf(ContextCompat.getColor(context, getPrimaryColorResId()))
        container_dictionary_filter.setBackgroundResource(getPrimaryColorResId())
    }

    open fun onFragmentSelected() {}

    fun onFragmentDeselected() {
        if (adapter.isSelectState) {
            adapter.isSelectState = false
        }
        animateDropdown(getCardViewFilter(), false, animBlock)
    }

    protected fun updateAdapter(words: List<Word>?) {
        if (words != null && words.isNotEmpty()) {
            val isScroll = adapter.currentList.size != words.size
            adapter.submitList(words)
            if (isScroll) {
                handler.postDelayed({
                    try {
                        (getRecyclerViewWords().layoutManager as LinearLayoutManager?)?.scrollToPositionWithOffset(0, 0)
                    } catch (e: IllegalStateException) {}
                }, 50)
            }
        }
    }

    private fun setCheckMode(isCheckMode: Boolean) {
        menu?.findItem(R.id.item_menu_check)?.isVisible = !isCheckMode
        menu?.findItem(getSearchMenuItemId())?.isVisible = !isCheckMode
        menu?.findItem(R.id.item_menu_filter)?.isVisible = !isCheckMode
        menu?.findItem(R.id.item_menu_dialog_own)?.isVisible = !isCheckMode
        menu?.findItem(R.id.item_menu_check_all)?.isVisible = isCheckMode
        menu?.findItem(R.id.item_menu_cancel_check)?.isVisible = isCheckMode
        menu?.findItem(R.id.item_menu_delete)?.isVisible = isCheckMode
    }
}