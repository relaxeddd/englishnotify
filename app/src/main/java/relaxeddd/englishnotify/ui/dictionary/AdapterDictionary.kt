package relaxeddd.englishnotify.ui.dictionary

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CompoundButton
import android.widget.ImageView
import android.widget.ProgressBar
import android.widget.TextView
import com.google.android.material.card.MaterialCardView
import com.google.android.material.checkbox.MaterialCheckBox
import kotlinx.android.synthetic.main.view_item_word.view.*
import relaxeddd.englishnotify.R
import relaxeddd.englishnotify.common.TYPE_PUSH_ENGLISH
import relaxeddd.englishnotify.common.TYPE_PUSH_RUSSIAN
import relaxeddd.englishnotify.common.Word
import java.util.*

class AdapterDictionary(viewModel: ViewModelDictionary) : AdapterWords<AdapterDictionary.ViewHolder>(viewModel) {

    var languageType = 0
        set(value) {
            if (field != value) {
                field = value
                notifyDataSetChanged()
            }
        }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.view_item_word, parent, false))
    }

    override fun bind(holder: ViewHolder, item: Word, clickListener: View.OnClickListener,
                      longListener: View.OnLongClickListener, checkListener: CompoundButton.OnCheckedChangeListener) {
        holder.bind(item, languageType, isSelectState, checkList, clickListener, longListener, checkListener)
    }

    class ViewHolder(view: View) : AdapterWords.ViewHolder(view) {

        override fun getCardViewWord(): MaterialCardView = itemView.card_view_word
        override fun getTextTimestamp(): TextView = itemView.text_word_timestamp
        override fun getTextTags(): TextView = itemView.text_word_tags
        override fun getImageOwnWord(): ImageView = itemView.image_word_own
        override fun getCheckBoxSelect(): MaterialCheckBox = itemView.check_box_word_select
        override fun getProgressLearn(): ProgressBar = itemView.progress_bar_word_learn_stage

        fun bind(word: Word, languageType: Int, isSelectState: Boolean, checkList: HashSet<Word>,
                 clickListener: View.OnClickListener, longClickListener: View.OnLongClickListener,
                 checkedChangeListener: CompoundButton.OnCheckedChangeListener) {
            super.bind(word, isSelectState, checkList, clickListener, longClickListener, checkedChangeListener)

            with(itemView) {
                val transcription = if (word.transcription.isNotEmpty()) "[" + word.transcription + "]" else ""
                val textV2 = if (word.v2.isNotEmpty()) "v2: " + word.v2 else ""
                val textV3 = if (word.v3.isNotEmpty()) "v3: " + word.v3 else ""

                when (languageType) {
                    TYPE_PUSH_ENGLISH -> {
                        text_word.text = word.eng
                        text_word_transcription.text = transcription
                        text_word_translation.text = word.rus
                        text_word_transcription.visibility = View.VISIBLE
                        text_word_transcription_translation.visibility = View.GONE
                    }
                    TYPE_PUSH_RUSSIAN -> {
                        text_word.text = word.rus
                        text_word_transcription_translation.text = transcription
                        text_word_translation.text = word.eng
                        text_word_transcription.visibility = View.GONE
                        text_word_transcription_translation.visibility = View.VISIBLE
                    }
                }

                text_word_v2.text = textV2
                text_word_v3.text = textV3
                text_word_v2.visibility = if (word.v2.isEmpty()) View.GONE else View.VISIBLE
                text_word_v3.visibility = if (word.v3.isEmpty()) View.GONE else View.VISIBLE

                text_word_sample_eng.text = word.sampleEng
                text_word_sample_rus.text = word.sampleRus
                text_word_sample_eng.visibility = if (word.sampleEng.isEmpty()) View.GONE else View.VISIBLE
                text_word_sample_rus.visibility = if (word.sampleRus.isEmpty()) View.GONE else View.VISIBLE
            }
        }
    }
}