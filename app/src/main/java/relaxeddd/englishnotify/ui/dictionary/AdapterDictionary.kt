package relaxeddd.englishnotify.ui.dictionary

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CompoundButton
import android.widget.ImageView
import android.widget.ProgressBar
import android.widget.TextView
import androidx.annotation.VisibleForTesting
import com.google.android.material.checkbox.MaterialCheckBox
import relaxeddd.englishnotify.R
import relaxeddd.englishnotify.domain_words.entity.Word
import relaxeddd.englishnotify.preferences.Preferences
import relaxeddd.englishnotify.preferences.utils.TYPE_PUSH_ENGLISH
import relaxeddd.englishnotify.preferences.utils.TYPE_PUSH_RUSSIAN

class AdapterDictionary(
    private val prefs: Preferences,
    viewModel: ViewModelDictionary,
) : AdapterWords<AdapterDictionary.ViewHolder>(prefs, viewModel) {

    companion object {
        const val MAX_TRANSLATIONS_COUNT = 4
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.view_item_word, parent, false))
    }

    override fun bind(holder: ViewHolder, item: Word, clickListener: View.OnClickListener,
                      longListener: View.OnLongClickListener, clickListenerPlay: View.OnClickListener,
                      checkListener: CompoundButton.OnCheckedChangeListener) {
        holder.bind(prefs, item, languageType, isSelectState, checkList, clickListener, longListener, clickListenerPlay, checkListener)
    }

    class ViewHolder(view: View) : AdapterWords.ViewHolder(view) {

        override fun getWordMainContainer(): ViewGroup = itemView.findViewById(R.id.card_view_word)
        override fun getWordContainerDropDawn(): ViewGroup? = itemView.findViewById(R.id.constraint_word_drop_dawn)
        override fun getTextTimestamp(): TextView = itemView.findViewById(R.id.text_word_timestamp)
        override fun getTextTags(): TextView = itemView.findViewById(R.id.text_word_tags)
        override fun getCheckBoxSelect(): MaterialCheckBox = itemView.findViewById(R.id.check_box_word_select)
        override fun getImagePlay(): ImageView = itemView.findViewById(R.id.image_word_play)
        override fun getProgressLearn(): ProgressBar = itemView.findViewById(R.id.progress_bar_word_learn_stage)
        override fun getProgressLearnSecondary(): ProgressBar = itemView.findViewById(R.id.progress_bar_word_learn_stage_secondary)

        @VisibleForTesting
        val textWord: TextView? = itemView.findViewById(R.id.text_word)
        private val textWordTranslation: TextView? = itemView.findViewById(R.id.text_word_translation)
        private val textWordTranscription: TextView? = itemView.findViewById(R.id.text_word_transcription)
        private val textWordTranscriptionTranslation: TextView? = itemView.findViewById(R.id.text_word_transcription_translation)
        private val textWordTranslation2: TextView? = itemView.findViewById(R.id.text_word_translation_2)
        private val textWordV2: TextView? = itemView.findViewById(R.id.text_word_v2)
        private val textWordV3: TextView? = itemView.findViewById(R.id.text_word_v3)
        private val textWordSampleEng: TextView? = itemView.findViewById(R.id.text_word_sample_eng)
        private val textWordSampleRus: TextView? = itemView.findViewById(R.id.text_word_sample_rus)

        fun bind(prefs: Preferences, word: Word, languageType: Int, isSelectState: Boolean, checkList: HashSet<Word>,
                 clickListener: View.OnClickListener, longClickListener: View.OnLongClickListener,
                 clickListenerPlay: View.OnClickListener, checkedChangeListener: CompoundButton.OnCheckedChangeListener) {
            super.bind(prefs, word, isSelectState, checkList, clickListener, longClickListener, clickListenerPlay, checkedChangeListener)

            val transcription = if (word.transcription.isNotEmpty()) "[" + word.transcription + "]" else ""
            val textV2 = if (word.v2.isNotEmpty()) "v2: " + word.v2 else ""
            val textV3 = if (word.v3.isNotEmpty()) "v3: " + word.v3 else ""

            val translations = word.rus.split(',')
            var textTranslation1 = ""
            var textTranslation2 = ""

            if (translations.size > MAX_TRANSLATIONS_COUNT) {
                for ((ix, translation) in translations.withIndex()) {
                    if (ix < MAX_TRANSLATIONS_COUNT) {
                        textTranslation1 += "$translation, "
                    } else {
                        textTranslation2 += "$translation, "
                    }
                }
                textTranslation1 = textTranslation1.substring(0, textTranslation1.length - 2) + " ..."
                textTranslation2 = textTranslation2.substring(0, textTranslation2.length - 2).trim()
            }

            textWordTranslation2?.visibility = View.GONE
            textWordTranslation2?.text = ""
            when (languageType) {
                TYPE_PUSH_ENGLISH -> {
                    textWord?.text = word.eng
                    textWordTranscription?.text = transcription
                    textWordTranscription?.visibility = if (transcription.isNotEmpty()) View.VISIBLE else View.GONE
                    textWordTranscriptionTranslation?.visibility = View.GONE

                    if (translations.size <= MAX_TRANSLATIONS_COUNT) {
                        textWordTranslation?.text = word.rus
                    } else {
                        textWordTranslation?.text = textTranslation1
                        textWordTranslation2?.text = textTranslation2
                        textWordTranslation2?.visibility = View.VISIBLE
                    }
                }
                TYPE_PUSH_RUSSIAN -> {
                    textWordTranscriptionTranslation?.text = transcription
                    textWordTranslation?.text = word.eng
                    textWordTranscription?.visibility = View.GONE
                    textWordTranscriptionTranslation?.visibility = if (transcription.isNotEmpty()) View.VISIBLE else View.GONE

                    if (translations.size <= MAX_TRANSLATIONS_COUNT) {
                        textWord?.text = word.rus
                    } else {
                        textWord?.text = textTranslation1
                        textWordTranslation2?.text = textTranslation2
                        textWordTranslation2?.visibility = View.VISIBLE
                    }
                }
            }

            textWordV2?.text = textV2
            textWordV3?.text = textV3
            textWordV2?.visibility = if (word.v2.isEmpty()) View.GONE else View.VISIBLE
            textWordV3?.visibility = if (word.v3.isEmpty()) View.GONE else View.VISIBLE

            textWordSampleEng?.text = word.sampleEng
            textWordSampleRus?.text = word.sampleRus
            textWordSampleEng?.visibility = if (word.sampleEng.isEmpty()) View.GONE else View.VISIBLE
            textWordSampleRus?.visibility = if (word.sampleRus.isEmpty()) View.GONE else View.VISIBLE
        }
    }
}
