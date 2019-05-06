package relaxeddd.englishnotify.ui.categories

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CompoundButton
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.radiobutton.MaterialRadioButton
import kotlinx.android.synthetic.main.view_item_category.view.*
import relaxeddd.englishnotify.R
import relaxeddd.englishnotify.common.CategoryItem
import relaxeddd.englishnotify.common.getStringByResName

class AdapterCategories(val viewModel: ViewModelCategories) : ListAdapter<CategoryItem, AdapterCategories.ViewHolder>(CategoryDiffCallback()) {

    private var checkedRadioButton: MaterialRadioButton? = null

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.view_item_category, parent, false))
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(getItem(position), viewModel.checkedItem, CompoundButton.OnCheckedChangeListener { radioButton, isChecked -> run {
            if (!isChecked) {
                return@run
            }

            val checkedItem = radioButton.tag as CategoryItem
            viewModel.checkedItem = checkedItem
            if (radioButton != checkedRadioButton) {
                checkedRadioButton?.isChecked = false
                checkedRadioButton = radioButton as MaterialRadioButton
            }
        }})
    }

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

        fun bind(item: CategoryItem, checkedItem: CategoryItem?, listener: CompoundButton.OnCheckedChangeListener) {
            with(itemView) {
                radio_button_category.tag = item
                radio_button_category.text = getStringByResName(item.key)

                radio_button_category.setOnCheckedChangeListener(listener)
                if (item == checkedItem && !radio_button_category.isChecked) {
                    radio_button_category.isChecked = true
                } else if (item != checkedItem && radio_button_category.isChecked) {
                    radio_button_category.isChecked = false
                }
            }
        }
    }

    private class CategoryDiffCallback : DiffUtil.ItemCallback<CategoryItem>() {

        override fun areItemsTheSame(oldItem: CategoryItem, newItem: CategoryItem): Boolean {
            return oldItem.key == newItem.key
        }

        override fun areContentsTheSame(oldItem: CategoryItem, newItem: CategoryItem): Boolean {
            return oldItem.key == newItem.key
        }
    }
}