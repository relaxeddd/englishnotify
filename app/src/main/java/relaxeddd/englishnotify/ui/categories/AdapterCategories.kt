package relaxeddd.englishnotify.ui.categories

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.RadioButton
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.radiobutton.MaterialRadioButton
import relaxeddd.englishnotify.R
import relaxeddd.englishnotify.common.CategoryItem
import relaxeddd.englishnotify.common.ISelectCategory
import relaxeddd.englishnotify.common.OWN_KEY_SYMBOL
import relaxeddd.englishnotify.common.getStringByResName

class AdapterCategories(val viewModel: ISelectCategory) : ListAdapter<CategoryItem, AdapterCategories.ViewHolder>(CategoryDiffCallback()) {

    private var checkedRadioButton: RadioButton? = null
    var isClickableItems = true
    var additionalItemClickListener: (() -> Unit)? = null

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.view_item_category, parent, false))
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(getItem(position), viewModel.getSelectedCategory(), { view -> run {
            val radioButton = view as? RadioButton
            additionalItemClickListener?.invoke()
            if (!isClickableItems) {
                radioButton?.isChecked = false
                return@run
            }

            if (radioButton != checkedRadioButton) {
                val checkedItem = radioButton?.tag as? CategoryItem
                checkedRadioButton?.isChecked = false
                checkedRadioButton = radioButton
                viewModel.setSelectedCategory(checkedItem)
            } else {
                clearSelection(false)
            }
        }}, viewModel)
    }

    fun clearSelection(isNotify: Boolean = true) {
        viewModel.setSelectedCategory(null)
        checkedRadioButton?.isChecked = false
        checkedRadioButton = null
        if (isNotify) {
            notifyDataSetChanged()
        }
    }

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

        private val radioButtonCategory: MaterialRadioButton? = itemView.findViewById(R.id.radio_button_category)

        fun bind(item: CategoryItem, selectedCategory: String?, listener: View.OnClickListener, iSelectCategory: ISelectCategory) {
            radioButtonCategory?.let { radioButtonCategory ->
                val isChecked = item.key == selectedCategory
                radioButtonCategory.tag = item
                radioButtonCategory.text = getStringByResName(itemView.context, item.key).replaceFirst(OWN_KEY_SYMBOL, "")
                radioButtonCategory.setOnClickListener(listener)
                radioButtonCategory.isChecked = isChecked
                iSelectCategory.onRadioButtonInit(item.key, radioButtonCategory)
                if (isChecked) {
                    checkedRadioButton = radioButtonCategory
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
