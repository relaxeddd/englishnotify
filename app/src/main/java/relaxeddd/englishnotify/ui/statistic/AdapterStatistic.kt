package relaxeddd.englishnotify.ui.statistic

import android.annotation.SuppressLint
import android.graphics.Paint
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ProgressBar
import android.widget.TextView
import androidx.appcompat.view.menu.MenuBuilder
import androidx.appcompat.view.menu.MenuPopupHelper
import androidx.appcompat.widget.PopupMenu
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import relaxeddd.englishnotify.R
import relaxeddd.englishnotify.model.preferences.SharedHelper
import relaxeddd.englishnotify.common.Word
import kotlin.math.min

class AdapterStatistic(val viewModel: ViewModelStatistic): ListAdapter<Word, AdapterStatistic.ViewHolder>(WordDiffCallback()) {

    private val learnStageMax = SharedHelper.getTrueAnswersToLearn()
    private val isEnabledSecondaryProgress = SharedHelper.isEnabledSecondaryProgress()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.view_item_statistic_word, parent, false))
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val word = getItem(position)
        val longListener = View.OnLongClickListener {
            showPopupWord(holder.itemView, word)
            true
        }
        holder.bind(getItem(position) ?: return, longListener)
    }

    @SuppressLint("RestrictedApi")
    private fun showPopupWord(view: View, word: Word) {
        val isEnabledSecondaryProgress = SharedHelper.isEnabledSecondaryProgress()
        val popupMenu = PopupMenu(view.context, view)

        popupMenu.inflate(R.menu.menu_popup_word)
        popupMenu.setOnMenuItemClickListener {
            when (it.itemId) {
                R.id.item_menu_delete -> viewModel.deleteWord(word)
                R.id.item_menu_reset_progress -> viewModel.resetProgress(word)
            }
            true
        }

        popupMenu.menu.findItem(R.id.item_menu_edit)?.isVisible = false
        popupMenu.menu.findItem(R.id.item_menu_reset_progress)?.isVisible = word.learnStage > 0 || (isEnabledSecondaryProgress && word.learnStageSecondary > 0)
        popupMenu.menu.findItem(R.id.item_menu_add_own)?.isVisible = false
        popupMenu.menu.findItem(R.id.item_menu_delete_own)?.isVisible = false
        val menuHelper = MenuPopupHelper(view.context, popupMenu.menu as MenuBuilder, view)
        menuHelper.setForceShowIcon(true)
        menuHelper.show()
    }

    inner class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {

        private val container = view.findViewById<ViewGroup>(R.id.container_item_statistic)
        private val textWord = view.findViewById<TextView>(R.id.text_statistic_word)
        private val textTranslation = view.findViewById<TextView>(R.id.text_statistic_word_translation)
        private val progress = view.findViewById<ProgressBar>(R.id.progress_bar_statistic_word)
        private val progressSecondary = view.findViewById<ProgressBar>(R.id.progress_bar_statistic_word_secondary)

        fun bind(word: Word, longListener: View.OnLongClickListener) {
            textWord.text = word.eng
            textTranslation.text = word.rus

            val totalProgress = if (isEnabledSecondaryProgress) min(word.learnStage, learnStageMax) + min(word.learnStageSecondary, learnStageMax) else word.learnStage
            val totalMaxProgress = if (isEnabledSecondaryProgress) learnStageMax * 2 else learnStageMax
            val textColorResId = when {
                totalProgress < (totalMaxProgress*0.3).toInt() -> R.color.need_learn
                totalProgress < (totalMaxProgress*0.55).toInt() -> R.color.start_learn
                totalProgress < (totalMaxProgress*0.99).toInt() -> R.color.almost_learned
                else -> R.color.green_success
            }

            if (isEnabledSecondaryProgress) {
                progress.visibility = if (word.isLearned(true, learnStageMax)) View.GONE else View.VISIBLE
                progress.progress = word.getLearnProgress(learnStageMax)
                progressSecondary.visibility = if (word.isLearned(true, learnStageMax)) View.GONE else View.VISIBLE
                progressSecondary.progress = word.getLearnProgressSecondary(learnStageMax)
            } else {
                progress.visibility = if (word.isLearned(false, learnStageMax)) View.GONE else View.VISIBLE
                progress.progress = word.getLearnProgress(learnStageMax)
                progressSecondary.visibility = View.GONE
            }

            textWord.setTextColor(ContextCompat.getColor(itemView.context, textColorResId))
            textTranslation.setTextColor(ContextCompat.getColor(itemView.context, textColorResId))
            if (word.isLearned(isEnabledSecondaryProgress, learnStageMax)) {
                textWord.paintFlags = textWord.paintFlags or Paint.STRIKE_THRU_TEXT_FLAG
                textTranslation.paintFlags = textTranslation.paintFlags or Paint.STRIKE_THRU_TEXT_FLAG
            } else {
                textWord.paintFlags = textWord.paintFlags and Paint.STRIKE_THRU_TEXT_FLAG.inv()
                textTranslation.paintFlags = textTranslation.paintFlags and Paint.STRIKE_THRU_TEXT_FLAG.inv()
            }
            container.setOnLongClickListener(longListener)
        }
    }

    private class WordDiffCallback : DiffUtil.ItemCallback<Word>() {

        override fun areItemsTheSame(oldItem: Word, newItem: Word) = oldItem.id == newItem.id
        override fun areContentsTheSame(oldItem: Word, newItem: Word) = oldItem == newItem
    }
}
