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
    override fun getHomeMenuButtonListener(): () -> Unit = { onNavigationEvent(NAVIGATION_ACTIVITY_BACK) }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.item_menu_accept -> {
                text_input_translation.onEditorAction(EditorInfo.IME_ACTION_DONE)
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val id = arguments?.getString(ID) ?: ""
        val eng = arguments?.getString(ENG) ?: ""
        val transcription = arguments?.getString(TRANSCRIPTION) ?: ""
        val rus = arguments?.getString(RUS) ?: ""

        viewModel.existsWordId = id

        if (id.isNotEmpty()) {
            updateToolbarTitle(getString(R.string.edit))
        }

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

    override fun onNavigationEvent(eventId: Int) {
        when (eventId) {
            NAVIGATION_WORD_EXISTS_ERROR -> {
                text_input_word.error = getString(R.string.word_already_exists)
            }
            NAVIGATION_ACTIVITY_BACK -> {
                hideKeyboard(view)
                super.onNavigationEvent(eventId)
            }
            else -> super.onNavigationEvent(eventId)
        }
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

        when {
            eng.isEmpty() -> text_input_word.error = getString(R.string.word_should_not_be_empty)
            rus.isEmpty() -> text_input_translation.error = getString(R.string.word_should_not_be_empty)
            eng.length > 200 -> text_input_word.error = getString(R.string.word_length_limit)
            rus.length > 200 -> text_input_translation.error = getString(R.string.word_length_limit)
            transcription.length > 200 -> text_input_transcription.error = getString(R.string.word_length_limit)
            else -> {
                hideKeyboard()
                viewModel.createOwnWord(eng, transcription, rus)
            }
        }
    }
}
