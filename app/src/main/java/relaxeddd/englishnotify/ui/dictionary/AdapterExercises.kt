package relaxeddd.englishnotify.ui.dictionary

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CompoundButton
import android.widget.ImageView
import android.widget.ProgressBar
import android.widget.TextView
import com.google.android.material.checkbox.MaterialCheckBox
import relaxeddd.englishnotify.R
import relaxeddd.englishnotify.common.Word
import java.util.HashSet

class AdapterExercises(viewModel: ViewModelDictionary) : AdapterWords<AdapterExercises.ViewHolder>(viewModel) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.view_item_exercise, parent, false))
    }

    class ViewHolder(view: View) : AdapterWords.ViewHolder(view) {

        override fun getWordMainContainer(): ViewGroup = itemView.findViewById(R.id.card_view_word)
        override fun getWordContainerDropDawn(): ViewGroup? = itemView.findViewById(R.id.constraint_word_drop_dawn)
        override fun getTextTimestamp(): TextView = itemView.findViewById(R.id.text_word_timestamp)
        override fun getTextTags(): TextView = itemView.findViewById(R.id.text_word_tags)
        override fun getImageOwnWord(): ImageView = itemView.findViewById(R.id.image_word_own)
        override fun getImageOwnCreatedWord(): ImageView = itemView.findViewById(R.id.image_word_own_created)
        override fun getCheckBoxSelect(): MaterialCheckBox = itemView.findViewById(R.id.check_box_word_select)
        override fun getImagePlay(): ImageView = itemView.findViewById(R.id.image_word_play)
        override fun getProgressLearn(): ProgressBar = itemView.findViewById(R.id.progress_bar_word_learn_stage)
        override fun getProgressLearnSecondary(): ProgressBar? = null

        private val textWord: TextView? = itemView.findViewById(R.id.text_word)
        private val textWordTranslation: TextView? = itemView.findViewById(R.id.text_word_translation)
        private val textWordTranscription: TextView? = itemView.findViewById(R.id.text_word_transcription)

        override fun bind(word: Word, isSelectState: Boolean, checkList: HashSet<Word>,
                          clickListener: View.OnClickListener, longClickListener: View.OnLongClickListener,
                          clickListenerPlay: View.OnClickListener, checkedChangeListener: CompoundButton.OnCheckedChangeListener) {
            super.bind(word, isSelectState, checkList, clickListener, longClickListener, clickListenerPlay, checkedChangeListener)

            with(itemView) {
                val transcription = if (word.transcription.isNotEmpty()) word.transcription else ""

                textWord?.text = word.eng
                textWordTranscription?.text = transcription
                textWordTranslation?.text = word.rus
            }
        }
    }
}
