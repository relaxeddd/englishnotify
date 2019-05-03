package relaxeddd.englishnotify.ui.dictionary

import android.os.Bundle
import android.view.MenuItem
import android.view.View
import androidx.databinding.ViewDataBinding
import relaxeddd.englishnotify.R
import relaxeddd.englishnotify.dialogs.DialogCheckTags
import relaxeddd.englishnotify.dialogs.DialogSortBy
import kotlinx.android.synthetic.main.fragment_dictionary_all.*
import relaxeddd.englishnotify.common.*
import relaxeddd.englishnotify.dialogs.DialogDeleteWords

abstract class FragmentDictionary<VM : ViewModelDictionary, B : ViewDataBinding> : BaseFragment<VM, B>() {

    protected lateinit var adapter: AdapterWords
    private var animBlock: AnimBlock = AnimBlock(false)

    private val listenerCheckTags: ListenerResult<List<String>> = object:
        ListenerResult<List<String>> {
        override fun onResult(result: List<String>) {
            viewModel.setFilterTags(result)
        }
    }

    private val listenerSortBy: ListenerResult<Int> = object:
        ListenerResult<Int> {
        override fun onResult(result: Int) {
            viewModel.onDialogSortByType(result)
        }
    }
    protected val clickListenerCloseFilter = View.OnClickListener {
        animateDropdown(card_view_dictionary_filter, false, animBlock)
    }

    override fun getToolbarTitleResId() = R.string.dictionary
    override fun getMenuResId() = R.menu.menu_fragment_dictionary
    override fun getToolbarElevation() = 0f

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.item_menu_filter -> {
                animateDropdown(card_view_dictionary_filter, card_view_dictionary_filter.visibility == View.GONE, animBlock)
                return true
            }
            R.id.item_menu_check -> {
                setCkeckMode(true)
                adapter.isSelectState = true
                return true
            }
            R.id.item_menu_cancel_check -> {
                setCkeckMode(false)
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
                                setCkeckMode(false)
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

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        recycler_view_dictionary.setOnTouchListener { _, _ ->
            animateDropdown(card_view_dictionary_filter, false, animBlock)
            false
        }
    }

    override fun onNavigationEvent(eventId: Int) {
        when (eventId) {
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

    open fun onFragmentSelected() {}

    fun onFragmentDeselected() {
        if (adapter.isSelectState) {
            adapter.isSelectState = false
        }
        animateDropdown(card_view_dictionary_filter, false, animBlock)
    }

    private fun setCkeckMode(isCheckMode: Boolean) {
        menu?.findItem(R.id.item_menu_check)?.isVisible = !isCheckMode
        menu?.findItem(R.id.item_menu_search)?.isVisible = !isCheckMode
        menu?.findItem(R.id.item_menu_filter)?.isVisible = !isCheckMode
        menu?.findItem(R.id.item_menu_dialog_own)?.isVisible = !isCheckMode
        menu?.findItem(R.id.item_menu_check_all)?.isVisible = isCheckMode
        menu?.findItem(R.id.item_menu_cancel_check)?.isVisible = isCheckMode
        menu?.findItem(R.id.item_menu_delete)?.isVisible = isCheckMode
    }
}