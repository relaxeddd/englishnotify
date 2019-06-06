package relaxeddd.englishnotify.ui.rating

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import kotlinx.android.synthetic.main.view_item_rating.view.*
import relaxeddd.englishnotify.R
import relaxeddd.englishnotify.common.RatingItem

class AdapterRating: ListAdapter<RatingItem, AdapterRating.ViewHolder>(DiffCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.view_item_rating, parent, false))
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(position, getItem(position) ?: return)
    }

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {

        fun bind(position: Int, item: RatingItem) {
            val positionStr = (position + 1).toString() + "."

            itemView.text_rating_position.text = positionStr
            itemView.text_rating_name.text = item.name
            itemView.text_rating_value.text = item.value.toString()
        }
    }

    private class DiffCallback : DiffUtil.ItemCallback<RatingItem>() {

        override fun areItemsTheSame(oldItem: RatingItem, newItem: RatingItem) = oldItem.name == newItem.name
        override fun areContentsTheSame(oldItem: RatingItem, newItem: RatingItem) = oldItem.value == newItem.value
    }
}