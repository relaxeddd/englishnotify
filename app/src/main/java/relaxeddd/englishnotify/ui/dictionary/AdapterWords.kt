package relaxeddd.englishnotify.ui.dictionary

import android.annotation.SuppressLint
import android.view.View
import android.widget.CompoundButton
import android.widget.ImageView
import android.widget.ProgressBar
import android.widget.TextView
import androidx.annotation.CallSuper
import androidx.appcompat.view.menu.MenuBuilder
import androidx.appcompat.view.menu.MenuPopupHelper
import androidx.appcompat.widget.PopupMenu
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.card.MaterialCardView
import com.google.android.material.checkbox.MaterialCheckBox
import relaxeddd.englishnotify.R
import relaxeddd.englishnotify.common.*
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.HashSet

abstract class AdapterWords<VH : AdapterWords.ViewHolder>(val viewModel: ViewModelDictionary) : ListAdapter<Word, VH>(WordDiffCallback()) {

    companion object {
        var isHideLearnStage = SharedHelper.isHideLearnStage()
    }

    var languageType = 0
        set(value) {
            if (field != value) {
                field = value
                notifyDataSetChanged()
            }
        }

    var isSelectState = false
        set(value) {
            field = value
            if (!value) checkList.clear()
            notifyDataSetChanged()
        }
    var checkList = HashSet<Word>()

    //------------------------------------------------------------------------------------------------------------------
    override fun onBindViewHolder(holder: VH, position: Int) {
        val word = getItem(position)
        val clickListener = View.OnClickListener {
            animateDropdown(it.findViewById(R.id.constraint_word_drop_dawn), !holder.isOpen, paddingDp = 16f)
            holder.isOpen = !holder.isOpen
        }
        val longListener = View.OnLongClickListener {
            showPopupWord(holder.itemView, word)
            true
        }
        val checkListener = CompoundButton.OnCheckedChangeListener { _, isChecked ->
            if (isChecked) checkList.add(word) else checkList.remove(word)
        }

        bind(holder, word, clickListener, longListener, checkListener)
    }

    open fun bind(holder: VH, item: Word, clickListener: View.OnClickListener, longListener: View.OnLongClickListener,
                  checkListener: CompoundButton.OnCheckedChangeListener) {
        holder.bind(item, isSelectState, checkList, clickListener, longListener, checkListener)
    }

    fun checkAll() {
        if (checkList.size != currentList.size) {
            checkList.addAll(currentList)
        } else {
            checkList.clear()
        }
        notifyDataSetChanged()
    }

    @SuppressLint("RestrictedApi")
    protected fun showPopupWord(view: View, word: Word) {
        val popupMenu = PopupMenu(view.context, view)

        popupMenu.inflate(R.menu.menu_popup_word)
        popupMenu.setOnMenuItemClickListener {
            when (it.itemId) {
                R.id.item_menu_delete -> viewModel.deleteWord(word)
                R.id.item_menu_add_own -> viewModel.addToOwn(word)
                R.id.item_menu_delete_own -> viewModel.removeFromOwnDict(word)
                R.id.item_menu_reset_progress -> viewModel.resetProgress(word)
                R.id.item_menu_know -> viewModel.setMaxProgress(word)
            }
            true
        }

        popupMenu.menu.findItem(R.id.item_menu_reset_progress)?.isVisible = word.learnStage > 0 && !isHideLearnStage
        popupMenu.menu.findItem(R.id.item_menu_know)?.isVisible = word.learnStage != LEARN_STAGE_MAX && !isHideLearnStage
        popupMenu.menu.findItem(R.id.item_menu_add_own)?.isVisible = word.saveType == Word.DICTIONARY
        popupMenu.menu.findItem(R.id.item_menu_delete_own)?.isVisible = word.saveType != Word.DICTIONARY
        val menuHelper = MenuPopupHelper(view.context, popupMenu.menu as MenuBuilder, view)
        menuHelper.setForceShowIcon(true)
        menuHelper.show()
    }

    private class WordDiffCallback : DiffUtil.ItemCallback<Word>() {

        override fun areItemsTheSame(oldItem: Word, newItem: Word): Boolean {
            return oldItem.eng == newItem.eng
        }

        override fun areContentsTheSame(oldItem: Word, newItem: Word): Boolean {
            return oldItem.learnStage == newItem.learnStage && oldItem.saveType == newItem.saveType
                    && oldItem.rus == newItem.rus && oldItem.type == newItem.type
                    && newItem.tags.containsAll(oldItem.tags) && oldItem.tags.containsAll(newItem.tags)

        }
    }

    abstract class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {

        var isOpen = false

        abstract fun getCardViewWord() : MaterialCardView
        abstract fun getTextTimestamp() : TextView
        abstract fun getTextTags() : TextView
        abstract fun getImageOwnWord() : ImageView
        abstract fun getCheckBoxSelect() : MaterialCheckBox
        abstract fun getProgressLearn() : ProgressBar

        @CallSuper
        open fun bind(word: Word, isSelectState: Boolean, checkList: java.util.HashSet<Word>,
                      clickListener: View.OnClickListener, longClickListener: View.OnLongClickListener,
                      checkedChangeListener: CompoundButton.OnCheckedChangeListener) {
            itemView.tag = word.eng
            getCardViewWord().setOnClickListener(clickListener)
            getCardViewWord().setOnLongClickListener(longClickListener)

            getTextTimestamp().text = SimpleDateFormat("hh:mm dd.MM", Locale.getDefault()).format(word.timestamp) ?: ""
            getTextTags().text = word.tags.toString()
            getImageOwnWord().visibility = if (word.saveType == Word.DICTIONARY) View.GONE else View.VISIBLE

            getCheckBoxSelect().visibility = if (isSelectState) View.VISIBLE else View.GONE
            getCheckBoxSelect().setOnCheckedChangeListener(null)
            if (isSelectState) {
                getCheckBoxSelect().isChecked = checkList.contains(word)
                getCheckBoxSelect().setOnCheckedChangeListener(checkedChangeListener)
            } else {
                getCheckBoxSelect().isChecked = false
            }

            getProgressLearn().visibility = if (isHideLearnStage) View.GONE else View.VISIBLE
            if (!isHideLearnStage) {
                getProgressLearn().progress = when (word.learnStage) {
                    0 -> 4
                    1 -> 32
                    2 -> 68
                    else -> 100
                }
            }
        }
    }
}