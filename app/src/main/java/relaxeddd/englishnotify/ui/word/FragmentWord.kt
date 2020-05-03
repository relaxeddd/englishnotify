package relaxeddd.englishnotify.ui.word

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.MenuItem
import android.view.View
import kotlinx.android.synthetic.main.fragment_word.*
import relaxeddd.englishnotify.R
import relaxeddd.englishnotify.common.*
import relaxeddd.englishnotify.databinding.FragmentWordBinding
import android.view.inputmethod.EditorInfo

class FragmentWord : BaseFragment<ViewModelWord, FragmentWordBinding>() {

    override fun getLayoutResId() = R.layout.fragment_word
    override fun getToolbarTitleResId() = R.string.add_word
    override fun getViewModelFactory() = InjectorUtils.provideWordViewModelFactory()
    override fun getViewModelClass() = ViewModelWord::class.java
    override fun getMenuResId() = R.menu.menu_accept
    override fun isHomeMenuButtonEnabled() = true
    override fun getHomeMenuButtonIconResId() = R.drawable.ic_back
    override fun getHomeMenuButtonListener(): () -> Unit = {
        onNavigationEvent(NAVIGATION_ACTIVITY_BACK)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.item_menu_accept -> {
                text_input_translation.onEditorAction(EditorInfo.IME_ACTION_DONE)
                return true
            }
            else -> return super.onOptionsItemSelected(item)
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val id = arguments?.getString(ID) ?: ""
        val eng = arguments?.getString(ENG) ?: ""
        val transcription = arguments?.getString(TRANSCRIPTION) ?: ""
        val rus = arguments?.getString(RUS) ?: ""

        viewModel.wordId = id
        text_input_word.setText(eng)
        text_input_transcription.setText(transcription)
        text_input_translation.setText(rus)

        text_input_word.addTextChangedListener(object: TextWatcher {
            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {}
            override fun afterTextChanged(p0: Editable?) {}
            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
                text_input_word.error = null
            }
        })
        text_input_translation.addTextChangedListener(object: TextWatcher {
            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {}
            override fun afterTextChanged(p0: Editable?) {}
            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
                text_input_translation.error = null
            }
        })
        text_input_transcription.addTextChangedListener(object: TextWatcher {
            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {}
            override fun afterTextChanged(p0: Editable?) {}
            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
                text_input_transcription.error = null
            }
        })
        text_input_translation.setOnEditorActionListener { _, _, event ->
            if (event == null || !event.isShiftPressed) {
                handleClickedSave()
                true
            } else {
                false
            }
        }
    }

    override fun setupThemeColors() {
        container_text_word_input_word.boxStrokeColor = getPrimaryColorResId()
        container_text_word_input_transcription.boxStrokeColor = getPrimaryColorResId()
        container_text_word_input_translation.boxStrokeColor = getPrimaryColorResId()
    }

    private fun hideKeyboard() {
       when {
            text_input_word.hasFocus() -> hideKeyboard(text_input_word)
            text_input_translation.hasFocus() -> hideKeyboard(text_input_translation)
            text_input_transcription.hasFocus() -> hideKeyboard(text_input_transcription)
       }
    }

    private fun handleClickedSave() {
        val eng = text_input_word.text.toString()
        val rus = text_input_translation.text.toString()
        val transcription = text_input_transcription.text.toString()

        if (eng.isEmpty()) {
            text_input_word.error = getString(R.string.word_should_not_be_empty)
        } else if (rus.isEmpty()) {
            text_input_translation.error = getString(R.string.word_should_not_be_empty)
        } else if (eng.length > 200) {
            text_input_word.error = getString(R.string.word_length_limit)
        } else if (rus.length > 200) {
            text_input_translation.error = getString(R.string.word_length_limit)
        } else if (transcription.length > 200) {
            text_input_transcription.error = getString(R.string.word_length_limit)
        } else {
            hideKeyboard()
            viewModel.createOwnWord(eng, transcription, rus)
        }
    }
}