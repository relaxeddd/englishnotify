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
import relaxeddd.englishnotify.common.LEARN_STAGE_MAX
import relaxeddd.englishnotify.model.preferences.SharedHelper
import relaxeddd.englishnotify.common.Word
import kotlin.math.min

class AdapterStatistic: ListAdapter<Word, AdapterStatistic.ViewHolder>(TagInfoDiffCallback()) {

    val isEnabledSecondaryProgress = SharedHelper.isEnabledSecondaryProgress()

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

            val textColorResId = if (isEnabledSecondaryProgress) {
                when(min(word.learnStage, 3) + min(word.learnStageSecondary, 3)) {
                    0, 1, 2 -> R.color.need_learn
                    3 -> R.color.start_learn
                    4, 5 -> R.color.almost_learned
                    else -> R.color.green_success
                }
            } else {
                when(word.learnStage) {
                    0 -> R.color.need_learn
                    1 -> R.color.start_learn
                    2 -> R.color.almost_learned
                    else -> R.color.green_success
                }
            }

            if (isEnabledSecondaryProgress) {
                progress.visibility = if (word.learnStage >= LEARN_STAGE_MAX && word.learnStageSecondary >= LEARN_STAGE_MAX) View.GONE else View.VISIBLE
                progress.progress = min((word.learnStage.toFloat() / LEARN_STAGE_MAX * 100).toInt(), 100)
                progressSecondary.visibility = if (word.learnStageSecondary >= LEARN_STAGE_MAX && word.learnStage >= LEARN_STAGE_MAX) View.GONE else View.VISIBLE
                progressSecondary.progress = min((word.learnStageSecondary.toFloat() / LEARN_STAGE_MAX * 100).toInt(), 100)
            } else {
                progress.visibility = if (word.learnStage >= LEARN_STAGE_MAX) View.GONE else View.VISIBLE
                progress.progress = min((word.learnStage.toFloat() / LEARN_STAGE_MAX * 100).toInt(), 100)
                progressSecondary.visibility = View.GONE
            }

            textWord.setTextColor(ContextCompat.getColor(itemView.context, textColorResId))
            textTranslation.setTextColor(ContextCompat.getColor(itemView.context, textColorResId))
            if (word.isLearned(isEnabledSecondaryProgress)) {
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
