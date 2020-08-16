package relaxeddd.englishnotify.ui.dictionary

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CompoundButton
import android.widget.ImageView
import android.widget.ProgressBar
import android.widget.TextView
import com.google.android.material.checkbox.MaterialCheckBox
import kotlinx.android.synthetic.main.view_item_exercise.view.*
import relaxeddd.englishnotify.R
import relaxeddd.englishnotify.common.Word
import java.util.HashSet

class AdapterExercises(viewModel: ViewModelDictionary) : AdapterWords<AdapterExercises.ViewHolder>(viewModel) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.view_item_exercise, parent, false))
    }

    class ViewHolder(view: View) : AdapterWords.ViewHolder(view) {

        override fun getWordMainContainer(): ViewGroup = itemView.card_view_word
        override fun getTextTimestamp(): TextView = itemView.text_word_timestamp
        override fun getTextTags(): TextView = itemView.text_word_tags
        override fun getImageOwnWord(): ImageView = itemView.image_word_own
        override fun getImageOwnCreatedWord(): ImageView = itemView.image_word_own_created
        override fun getCheckBoxSelect(): MaterialCheckBox = itemView.check_box_word_select
        override fun getImagePlay(): ImageView = itemView.image_word_play
        override fun getProgressLearn(): ProgressBar = itemView.progress_bar_word_learn_stage

        override fun bind(word: Word, isSelectState: Boolean, checkList: HashSet<Word>,
                          clickListener: View.OnClickListener, longClickListener: View.OnLongClickListener,
                          clickListenerPlay: View.OnClickListener, checkedChangeListener: CompoundButton.OnCheckedChangeListener) {
            super.bind(word, isSelectState, checkList, clickListener, longClickListener, clickListenerPlay, checkedChangeListener)

            with(itemView) {
                val transcription = if (word.transcription.isNotEmpty()) word.transcription else ""

                text_word.text = word.eng
                text_word_transcription.text = transcription
                text_word_translation.text = word.rus
            }
        }
    }
}