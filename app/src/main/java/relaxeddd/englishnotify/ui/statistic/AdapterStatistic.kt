package relaxeddd.englishnotify.ui.statistic

import android.graphics.Paint
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ProgressBar
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import relaxeddd.englishnotify.R
import relaxeddd.englishnotify.model.preferences.SharedHelper
import relaxeddd.englishnotify.common.Word
import kotlin.math.min

class AdapterStatistic: ListAdapter<Word, AdapterStatistic.ViewHolder>(TagInfoDiffCallback()) {

    private val learnStageMax = SharedHelper.getTrueAnswersToLearn()
    private val isEnabledSecondaryProgress = SharedHelper.isEnabledSecondaryProgress()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.view_item_statistic_word, parent, false))
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(getItem(position) ?: return)
    }

    inner class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {

        private val textWord = view.findViewById<TextView>(R.id.text_statistic_word)
        private val textTranslation = view.findViewById<TextView>(R.id.text_statistic_word_translation)
        private val progress = view.findViewById<ProgressBar>(R.id.progress_bar_statistic_word)
        private val progressSecondary = view.findViewById<ProgressBar>(R.id.progress_bar_statistic_word_secondary)

        fun bind(word: Word) {
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
        }
    }

    private class TagInfoDiffCallback : DiffUtil.ItemCallback<Word>() {

        override fun areItemsTheSame(oldItem: Word, newItem: Word) = oldItem.id == newItem.id
        override fun areContentsTheSame(oldItem: Word, newItem: Word) = oldItem == newItem
    }
}
