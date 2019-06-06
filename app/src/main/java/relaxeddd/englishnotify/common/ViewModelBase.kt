package relaxeddd.englishnotify.common

import android.os.Build
import android.speech.tts.TextToSpeech
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import relaxeddd.englishnotify.App
import relaxeddd.englishnotify.R
import java.util.Locale

abstract class ViewModelBase : ViewModel() {

    companion object {
        private var tts: TextToSpeech? = null
        private var isTtsInit = false
        private var lastVoiceWordId: String? = null
        private var isFastLastSpeechSpeed = false
    }

    protected val navigateEvent = MutableLiveData<Event<Int>>()
    protected val uiScope = CoroutineScope(Dispatchers.Main)
    protected val ioScope = CoroutineScope(Dispatchers.IO)



    val navigation : LiveData<Event<Int>>
        get() = navigateEvent

    open fun onFragmentResume() {}

    init {
        if (tts == null) {
            tts = TextToSpeech(App.context, TextToSpeech.OnInitListener {
                if (it == TextToSpeech.SUCCESS) {
                    tts?.language = Locale.US
                    isTtsInit = true
                } else if (it == TextToSpeech.ERROR) {
                    showToast(R.string.error_word_voice)
                }
            })
        }
    }

    fun playWord(word: Word) {
        if (isTtsInit) {
            if (word.id == lastVoiceWordId && isFastLastSpeechSpeed) {
                tts?.setSpeechRate(0.5f)
                isFastLastSpeechSpeed = false
            } else if (!isFastLastSpeechSpeed) {
                tts?.setSpeechRate(1f)
                isFastLastSpeechSpeed = true
            }
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                tts?.speak(word.eng.replace("___", ""), TextToSpeech.QUEUE_FLUSH, null, word.eng)
            } else {
                tts?.speak(word.eng.replace("___", ""), TextToSpeech.QUEUE_FLUSH, HashMap())
            }
            lastVoiceWordId = word.id
        }
    }
}