package relaxeddd.englishnotify.ui.parsed_words

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.appcompat.view.menu.MenuBuilder
import androidx.appcompat.view.menu.MenuPopupHelper
import androidx.appcompat.widget.PopupMenu
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import relaxeddd.englishnotify.R
import relaxeddd.englishnotify.common.Word
import relaxeddd.englishnotify.model.preferences.SharedHelper

class AdapterParsedWords(val viewModel: ViewModelParsedWords): ListAdapter<Word, AdapterParsedWords.ViewHolder>(WordDiffCallback()) {

    //private val learnStageMax = SharedHelper.getTrueAnswersToLearn()
    //private val isEnabledSecondaryProgress = SharedHelper.isEnabledSecondaryProgress()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.view_item_parsed_word, parent, false))
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val word = getItem(position)
        val longListener = View.OnLongClickListener {
            showPopupWord(holder.itemView, word)
            true
        }
        holder.bind(getItem(position) ?: return, longListener)
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

        popupMenu.menu.findItem(R.id.item_menu_edit)?.isVisible = false
        popupMenu.menu.findItem(R.id.item_menu_reset_progress)?.isVisible = false
        popupMenu.menu.findItem(R.id.item_menu_add_own)?.isVisible = false
        popupMenu.menu.findItem(R.id.item_menu_delete_own)?.isVisible = false
        val menuHelper = MenuPopupHelper(view.context, popupMenu.menu as MenuBuilder, view)
        menuHelper.setForceShowIcon(true)
        menuHelper.show()
    }

    inner class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {

        private val container = view.findViewById<ViewGroup>(R.id.container_item_parsed_word)
        private val textWord = view.findViewById<TextView>(R.id.text_item_parsed_word)
        private val textTranscription = view.findViewById<TextView>(R.id.text_item_parsed_word_transcription)
        private val textTranslation = view.findViewById<TextView>(R.id.text_item_parsed_word_translation)

        fun bind(word: Word, longListener: View.OnLongClickListener) {
            textWord.text = word.eng
            textTranscription.text = word.transcription
            textTranslation.text = word.rus
            textTranscription.visibility = if (word.transcription.isEmpty()) View.GONE else View.VISIBLE

            container.setOnLongClickListener(longListener)
        }
    }

    private class WordDiffCallback : DiffUtil.ItemCallback<Word>() {

        override fun areItemsTheSame(oldItem: Word, newItem: Word) = oldItem.id == newItem.id
        override fun areContentsTheSame(oldItem: Word, newItem: Word) = oldItem == newItem
    }
}
