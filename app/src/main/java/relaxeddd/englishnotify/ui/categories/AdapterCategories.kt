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
import relaxeddd.englishnotify.common.ISelectCategory
import relaxeddd.englishnotify.common.OWN_KEY_SYMBOL
import relaxeddd.englishnotify.common.getStringByResName

class AdapterCategories(val viewModel: ISelectCategory) : ListAdapter<CategoryItem, AdapterCategories.ViewHolder>(CategoryDiffCallback()) {

    private var checkedRadioButton: MaterialRadioButton? = null
    var isClickableItems = true
    var additionalItemClickListener: (() -> Unit)? = null

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.view_item_category, parent, false))
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(getItem(position), viewModel.getSelectedCategory(), { radioButton, isChecked -> run {
            if (!isChecked) {
                return@run
            }
            additionalItemClickListener?.invoke()
            if (!isClickableItems) {
                radioButton.isChecked = false
                return@run
            }

            if (radioButton != checkedRadioButton) {
                val checkedItem = radioButton.tag as CategoryItem
                checkedRadioButton?.isChecked = false
                checkedRadioButton = radioButton as MaterialRadioButton
                viewModel.setSelectedCategory(checkedItem)
            }
        }}, viewModel)
    }

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

        fun bind(item: CategoryItem, selectedCategory: String?, listener: CompoundButton.OnCheckedChangeListener, iSelectCategory: ISelectCategory) {
            with(itemView) {
                radio_button_category.tag = item
                radio_button_category.text = getStringByResName(item.key).replaceFirst(OWN_KEY_SYMBOL, "")
                radio_button_category.setOnCheckedChangeListener(listener)
                radio_button_category.isChecked = item.key == selectedCategory
                iSelectCategory.onRadioButtonInit(item.key, radio_button_category)
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
