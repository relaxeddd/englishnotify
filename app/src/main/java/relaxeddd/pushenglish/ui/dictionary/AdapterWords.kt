package relaxeddd.pushenglish.ui.dictionary

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import relaxeddd.pushenglish.R
import relaxeddd.pushenglish.common.Word
import relaxeddd.pushenglish.common.animateDropdown
import relaxeddd.pushenglish.databinding.ViewItemWordBinding

class AdapterWords : ListAdapter<Word, AdapterWords.ViewHolder>(WordDiffCallback()) {

    var languageType = 0
        set(value) {
            field = value
            notifyDataSetChanged()
        }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(DataBindingUtil.inflate(LayoutInflater.from(parent.context), R.layout.view_item_word, parent,
            false))
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        getItem(position).let { word ->
            with(holder) {
                itemView.tag = word.eng
                bind(createOnClickListener(this), word, languageType)
            }
        }
    }

    private fun createOnClickListener(holder: ViewHolder): View.OnClickListener {
        return View.OnClickListener {
            animateDropdown(it.findViewById(R.id.constraint_word_drop_dawn), !holder.isOpen, paddingDp = 16f)
            holder.isOpen = !holder.isOpen
        }
    }

    class ViewHolder(private val binding: ViewItemWordBinding) : RecyclerView.ViewHolder(binding.root) {

        var isOpen = false

        fun bind(listener: View.OnClickListener, word: Word, languageType: Int) {
            with(binding) {
                clickListener = listener
                this.word = word
                executePendingBindings()
                when (languageType) {
                    0 -> {
                        textWord.text = word.eng
                        textWordTranscription.text = word.rus
                    }
                    1 -> {
                        textWord.text = word.rus
                        textWordTranscription.text = word.eng
                    }
                }
            }
        }
    }
}

private class WordDiffCallback : DiffUtil.ItemCallback<Word>() {

    override fun areItemsTheSame(oldItem: Word, newItem: Word): Boolean {
        return oldItem.eng == newItem.eng
    }

    override fun areContentsTheSame(oldItem: Word, newItem: Word): Boolean {
        return oldItem.eng == newItem.eng
    }
}