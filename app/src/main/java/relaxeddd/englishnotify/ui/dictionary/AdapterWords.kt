package relaxeddd.englishnotify.ui.dictionary

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import relaxeddd.englishnotify.R
import relaxeddd.englishnotify.common.Word
import relaxeddd.englishnotify.common.animateDropdown
import relaxeddd.englishnotify.databinding.ViewItemWordBinding
import androidx.appcompat.widget.PopupMenu
import androidx.appcompat.view.menu.MenuBuilder
import androidx.appcompat.view.menu.MenuPopupHelper
import java.text.SimpleDateFormat
import java.util.*

class AdapterWords(val viewModel: ViewModelDictionary) : ListAdapter<Word, AdapterWords.ViewHolder>(
    WordDiffCallback()) {

    var languageType = 0
        set(value) {
            field = value
            notifyDataSetChanged()
        }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(
            DataBindingUtil.inflate(
                LayoutInflater.from(parent.context), R.layout.view_item_word, parent,
                false
            )
        )
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        getItem(position).let { word ->
            with(holder) {
                itemView.tag = word.eng
                bind(createOnClickListener(this), createOnLongClickListener(this, word), word, languageType)
            }
        }
    }

    private fun createOnLongClickListener(holder: ViewHolder, word: Word): View.OnLongClickListener {
        return View.OnLongClickListener {
            showPopupWord(holder.itemView, word)
            true
        }
    }

    private fun createOnClickListener(holder: ViewHolder): View.OnClickListener {
        return View.OnClickListener {
            animateDropdown(
                it.findViewById(R.id.constraint_word_drop_dawn),
                !holder.isOpen,
                paddingDp = 16f
            )
            holder.isOpen = !holder.isOpen
        }
    }

    class ViewHolder(private val binding: ViewItemWordBinding) : RecyclerView.ViewHolder(binding.root) {

        var isOpen = false

        fun bind(listener: View.OnClickListener, longListener: View.OnLongClickListener, word: Word, languageType: Int) {
            with(binding) {
                clickListener = listener
                longClickListener = longListener
                this.word = word
                executePendingBindings()
                val transcription = "[" + word.transcription + "]"

                when (languageType) {
                    0 -> {
                        textWord.text = word.eng
                        textWordTranscription.text = transcription
                        textWordTranslation.text = word.rus
                    }
                    1 -> {
                        textWord.text = word.rus
                        textWordTranscription.text = transcription
                        textWordTranslation.text = word.eng
                    }
                }
                val dateFormat = "hh:mm dd.MM"
                textWordTimestamp.text = SimpleDateFormat(dateFormat, Locale.getDefault()).format(word.timestamp) ?: ""
            }
        }
    }

    @SuppressLint("RestrictedApi")
    private fun showPopupWord(view: View, word: Word) {
        val popupMenu = PopupMenu(view.context, view)

        popupMenu.inflate(R.menu.menu_popup_word)
        popupMenu.setOnMenuItemClickListener {
            when (it.itemId) {
                R.id.item_menu_delete -> viewModel.deleteWord(word)
            }
            true
        }

        val menuHelper = MenuPopupHelper(view.context, popupMenu.menu as MenuBuilder, view)
        menuHelper.setForceShowIcon(true)
        menuHelper.show()
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