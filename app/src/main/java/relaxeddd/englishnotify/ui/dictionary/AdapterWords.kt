package relaxeddd.englishnotify.ui.dictionary

import android.annotation.SuppressLint
import android.view.View
import android.view.ViewGroup
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
import com.google.android.material.checkbox.MaterialCheckBox
import relaxeddd.englishnotify.R
import relaxeddd.englishnotify.model.preferences.SharedHelper
import relaxeddd.englishnotify.common.Word
import relaxeddd.englishnotify.common.animateDropdown
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.HashSet

abstract class AdapterWords<VH : AdapterWords.ViewHolder>(val viewModel: ViewModelDictionary) : ListAdapter<Word, VH>(WordDiffCallback) {

    companion object {
        var learnStageMax = SharedHelper.getTrueAnswersToLearn()
        var isEnabledSecondaryProgress = SharedHelper.isEnabledSecondaryProgress()
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
            if (field != value) {
                field = value
                if (!value) checkList.clear()
                notifyDataSetChanged()
            }
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
        val clickListenerPlay = View.OnClickListener {
            viewModel.playWord(word)
        }

        bind(holder, word, clickListener, longListener, clickListenerPlay, checkListener)
    }

    open fun bind(holder: VH, item: Word, clickListener: View.OnClickListener, longListener: View.OnLongClickListener,
                  clickListenerPlay: View.OnClickListener, checkListener: CompoundButton.OnCheckedChangeListener) {
        holder.bind(item, isSelectState, checkList, clickListener, longListener, clickListenerPlay, checkListener)
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
        val isEnabledSecondaryProgress = SharedHelper.isEnabledSecondaryProgress()
        val popupMenu = PopupMenu(view.context, view)

        popupMenu.inflate(R.menu.menu_popup_word)
        popupMenu.setOnMenuItemClickListener {
            when (it.itemId) {
                R.id.item_menu_edit -> viewModel.edit(word)
                R.id.item_menu_delete -> viewModel.deleteWord(word)
                R.id.item_menu_add_own -> viewModel.addToOwn(word)
                R.id.item_menu_delete_own -> viewModel.removeFromOwnDict(word)
                R.id.item_menu_reset_progress -> viewModel.resetProgress(word)
            }
            true
        }

        popupMenu.menu.findItem(R.id.item_menu_edit)?.isVisible = word.isCreatedByUser
        popupMenu.menu.findItem(R.id.item_menu_reset_progress)?.isVisible = word.learnStage > 0 || (isEnabledSecondaryProgress && word.learnStageSecondary > 0)
        popupMenu.menu.findItem(R.id.item_menu_add_own)?.isVisible = !word.isOwnCategory
        popupMenu.menu.findItem(R.id.item_menu_delete_own)?.isVisible = word.isOwnCategory
        val menuHelper = MenuPopupHelper(view.context, popupMenu.menu as MenuBuilder, view)
        menuHelper.setForceShowIcon(true)
        menuHelper.show()
    }

    private object WordDiffCallback : DiffUtil.ItemCallback<Word>() {

        override fun areItemsTheSame(oldItem: Word, newItem: Word): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(oldItem: Word, newItem: Word): Boolean {
            return oldItem.eng == newItem.eng && oldItem.learnStage == newItem.learnStage && oldItem.rus == newItem.rus
                    && oldItem.transcription == newItem.transcription && oldItem.type == newItem.type
                    && newItem.tags.containsAll(oldItem.tags) && oldItem.tags.containsAll(newItem.tags)
                    && oldItem.isCreatedByUser == newItem.isCreatedByUser
                    && oldItem.isOwnCategory == newItem.isOwnCategory && oldItem.isDeleted == newItem.isDeleted
                    && oldItem.learnStageSecondary == newItem.learnStageSecondary
        }
    }

    abstract class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {

        var isOpen = false

        abstract fun getWordMainContainer() : ViewGroup
        abstract fun getTextTimestamp() : TextView
        abstract fun getTextTags() : TextView
        abstract fun getImageOwnWord() : ImageView
        abstract fun getImageOwnCreatedWord() : ImageView
        abstract fun getCheckBoxSelect() : MaterialCheckBox
        abstract fun getImagePlay() : ImageView
        abstract fun getProgressLearn() : ProgressBar
        abstract fun getProgressLearnSecondary() : ProgressBar?

        @CallSuper
        open fun bind(word: Word, isSelectState: Boolean, checkList: java.util.HashSet<Word>,
                      clickListener: View.OnClickListener, longClickListener: View.OnLongClickListener,
                      clickListenerPlay: View.OnClickListener, checkedChangeListener: CompoundButton.OnCheckedChangeListener) {
            itemView.tag = word.id
            getWordMainContainer().setOnClickListener(clickListener)
            getWordMainContainer().setOnLongClickListener(longClickListener)
            getImagePlay().setOnClickListener(clickListenerPlay)

            getTextTimestamp().text = SimpleDateFormat("hh:mm dd.MM", Locale.getDefault()).format(word.timestamp) ?: ""
            getTextTags().text = if (word.tags.isNotEmpty()) word.tags.toString() else ""
            getTextTags().visibility = if (word.tags.isNotEmpty()) View.VISIBLE else View.GONE
            getImageOwnWord().visibility = if (word.isOwnCategory) View.VISIBLE else View.GONE
            getImageOwnCreatedWord().visibility = if (word.isCreatedByUser) View.VISIBLE else View.GONE

            getImagePlay().visibility = if (!isSelectState) View.VISIBLE else View.GONE
            getCheckBoxSelect().visibility = if (isSelectState) View.VISIBLE else View.GONE
            getCheckBoxSelect().setOnCheckedChangeListener(null)
            if (isSelectState) {
                getCheckBoxSelect().isChecked = checkList.contains(word)
                getCheckBoxSelect().setOnCheckedChangeListener(checkedChangeListener)
            } else {
                getCheckBoxSelect().isChecked = false
            }

            getProgressLearn().progress = word.getLearnProgress(learnStageMax)
            if (isEnabledSecondaryProgress) {
                getProgressLearnSecondary()?.visibility = View.VISIBLE
                getProgressLearnSecondary()?.progress = word.getLearnProgressSecondary(learnStageMax)
            } else {
                getProgressLearnSecondary()?.visibility = View.GONE
            }
        }
    }
}
