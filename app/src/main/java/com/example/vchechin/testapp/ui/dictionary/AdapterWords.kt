package com.example.vchechin.testapp.ui.dictionary

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.vchechin.testapp.R
import com.example.vchechin.testapp.common.Word
import com.example.vchechin.testapp.common.showToast
import com.example.vchechin.testapp.databinding.ViewItemWordBinding

class AdapterWords : ListAdapter<Word, AdapterWords.ViewHolder>(WordDiffCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(
            DataBindingUtil.inflate(LayoutInflater.from(parent.context), R.layout.view_item_word, parent, false)
        )
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        getItem(position).let { word ->
            with(holder) {
                itemView.tag = word.eng
                bind(createOnClickListener(word), word)
            }
        }
    }

    private fun createOnClickListener(word: Word): View.OnClickListener {
        return View.OnClickListener {
            showToast(word.tags.toString())
        }
    }

    class ViewHolder(private val binding: ViewItemWordBinding) : RecyclerView.ViewHolder(binding.root) {

        fun bind(listener: View.OnClickListener, word: Word) {
            with(binding) {
                clickListener = listener
                this.word = word
                executePendingBindings()
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