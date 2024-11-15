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
import relaxeddd.englishnotify.common.OWN_KEY_SYMBOL
import relaxeddd.englishnotify.common.animateDropdown
import relaxeddd.englishnotify.domain_words.entity.Word
import relaxeddd.englishnotify.preferences.Preferences
import java.text.SimpleDateFormat
import java.util.*

abstract class AdapterWords<VH : AdapterWords.ViewHolder>(
    private val prefs: Preferences, // TODO: refactor without using prefs, viewModel
    private val viewModel: ViewModelDictionary,
) : ListAdapter<Word, VH>(WordDiffCallback) {

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
            val padding = it.resources.getDimension(R.dimen.size_2)
            animateDropdown(it.findViewById(R.id.constraint_word_drop_dawn), !holder.isOpen, paddingDp = padding)
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
        holder.bind(prefs, item, isSelectState, checkList, clickListener, longListener, clickListenerPlay, checkListener)
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
        val isEnabledSecondaryProgress = prefs.isEnabledSecondaryProgress()
        val popupMenu = PopupMenu(view.context, view)

        popupMenu.inflate(R.menu.menu_popup_word)
        popupMenu.setOnMenuItemClickListener {
            when (it.itemId) {
                R.id.item_menu_edit -> viewModel.edit(word)
                R.id.item_menu_delete -> viewModel.deleteWord(word)
                R.id.item_menu_reset_progress -> viewModel.resetProgress(word)
            }
            true
        }

        popupMenu.menu.findItem(R.id.item_menu_edit)?.isVisible = word.isCreatedByUser
        popupMenu.menu.findItem(R.id.item_menu_reset_progress)?.isVisible = word.learnStage > 0 || (isEnabledSecondaryProgress && word.learnStageSecondary > 0)
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
        abstract fun getWordContainerDropDawn() : ViewGroup?
        abstract fun getTextTimestamp() : TextView
        abstract fun getTextTags() : TextView
        abstract fun getCheckBoxSelect() : MaterialCheckBox
        abstract fun getImagePlay() : ImageView
        abstract fun getProgressLearn() : ProgressBar
        abstract fun getProgressLearnSecondary() : ProgressBar?

        @CallSuper
        open fun bind(prefs: Preferences, word: Word, isSelectState: Boolean, checkList: HashSet<Word>,
                      clickListener: View.OnClickListener, longClickListener: View.OnLongClickListener,
                      clickListenerPlay: View.OnClickListener, checkedChangeListener: CompoundButton.OnCheckedChangeListener) {
            hideDropDawnContainer()
            isOpen = false

            itemView.tag = word.id
            getWordMainContainer().setOnClickListener(clickListener)
            getWordMainContainer().setOnLongClickListener(longClickListener)
            getImagePlay().setOnClickListener(clickListenerPlay)

            val tagsString = if (word.tags.isNotEmpty()) ArrayList<String>().apply {
                word.tags.forEach { add(it.replaceFirst(OWN_KEY_SYMBOL, "")) }
            }.toString() else ""

            getTextTimestamp().text = SimpleDateFormat("hh:mm dd.MM", Locale.getDefault()).format(word.timestamp) ?: ""
            getTextTags().text = tagsString
            getTextTags().visibility = if (word.tags.isNotEmpty()) View.VISIBLE else View.GONE

            getImagePlay().visibility = if (!isSelectState) View.VISIBLE else View.GONE
            getCheckBoxSelect().visibility = if (isSelectState) View.VISIBLE else View.GONE
            getCheckBoxSelect().setOnCheckedChangeListener(null)
            if (isSelectState) {
                getCheckBoxSelect().isChecked = checkList.contains(word)
                getCheckBoxSelect().setOnCheckedChangeListener(checkedChangeListener)
            } else {
                getCheckBoxSelect().isChecked = false
            }

            getProgressLearn().progress = word.getLearnProgress(prefs.getTrueAnswersToLearn())
            if (prefs.isEnabledSecondaryProgress()) {
                getProgressLearnSecondary()?.visibility = View.VISIBLE
                getProgressLearnSecondary()?.progress = word.getLearnProgressSecondary(prefs.getTrueAnswersToLearn())
            } else {
                getProgressLearnSecondary()?.visibility = View.GONE
            }
        }

        private fun hideDropDawnContainer() {
            getWordContainerDropDawn()?.visibility = View.GONE
            val params = getWordContainerDropDawn()?.layoutParams
            params?.height = 0
            getWordContainerDropDawn()?.layoutParams = params
        }
    }
}
