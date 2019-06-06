package relaxeddd.englishnotify.ui.statistic

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import kotlinx.android.synthetic.main.view_item_tag_statistic.view.*
import relaxeddd.englishnotify.R
import relaxeddd.englishnotify.common.TagInfo
import relaxeddd.englishnotify.common.getStringByResName

class AdapterStatistic: ListAdapter<TagInfo, AdapterStatistic.ViewHolder>(TagInfoDiffCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.view_item_tag_statistic, parent, false))
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(getItem(position) ?: return)
    }

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {

        fun bind(tagInfo: TagInfo) {
            val textValue = "" + tagInfo.learned + " / " + tagInfo.received + " / " + tagInfo.total

            itemView.text_tag_statistic_title.text = getStringByResName(tagInfo.key)
            itemView.text_tag_statistic_value.text = textValue
            itemView.progress_bar_tag_statistic.progress = (tagInfo.learned.toFloat() / tagInfo.total.toFloat() * 100).toInt()
            itemView.progress_bar_tag_statistic.secondaryProgress = (tagInfo.received.toFloat() / tagInfo.total.toFloat() * 100).toInt()
        }
    }

    private class TagInfoDiffCallback : DiffUtil.ItemCallback<TagInfo>() {

        override fun areItemsTheSame(oldItem: TagInfo, newItem: TagInfo) = oldItem.key == newItem.key
        override fun areContentsTheSame(oldItem: TagInfo, newItem: TagInfo) = oldItem.total == newItem.total
                && oldItem.learned == newItem.learned && oldItem.received == newItem.received
    }
}