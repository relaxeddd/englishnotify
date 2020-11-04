package relaxeddd.englishnotify.ui.word

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.MenuItem
import android.view.View
import relaxeddd.englishnotify.R
import relaxeddd.englishnotify.common.*
import relaxeddd.englishnotify.databinding.FragmentWordBinding
import android.view.inputmethod.EditorInfo
import android.widget.AdapterView
import android.widget.ArrayAdapter
import androidx.appcompat.widget.PopupMenu
import relaxeddd.englishnotify.dialogs.DialogRestoreWord
import relaxeddd.englishnotify.model.preferences.SharedHelper
import relaxeddd.englishnotify.ui.main.MainActivity

class FragmentWord : BaseFragment<ViewModelWord, FragmentWordBinding>() {

    private val listenerRestoreWord: ListenerResult<Boolean> = object: ListenerResult<Boolean> {
        override fun onResult(result: Boolean) {
            if (result) {
                viewModel.forceUpdateFindWord()
            }
        }
    }

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
                binding.textInputTranslation.onEditorAction(EditorInfo.IME_ACTION_DONE)
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    override fun configureBinding() {
        super.configureBinding()
        binding.imageWordMicrophone.setOnClickListener {
            val selectedLanguage = binding.spinnerWordLanguage.selectedItem as? String ?: ""
            (activity as? MainActivity)?.requestRecognizeSpeech(selectedLanguage) {
                if (it == null) {
                    SharedHelper.setShowVoiceInput(false)
                    updateVoiceInputVisibility(false)
                } else {
                    binding.textInputWord.setText(it)
                }
            }
        }
        ArrayAdapter.createFromResource(context ?: return, R.array.array_languages, android.R.layout.simple_spinner_item).also { adapter ->
            adapter.setDropDownViewResource(R.layout.view_item_spinner)
            binding.spinnerWordLanguage.adapter = adapter
        }
        binding.spinnerWordLanguage.setSelection(SharedHelper.getSelectedLocaleWord())
        binding.spinnerWordLanguage.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                SharedHelper.setSelectedLocaleWord(position)
            }
            override fun onNothingSelected(parent: AdapterView<*>?) {}
        }

        binding.imageWordMicrophoneTranslation.setOnClickListener {
            val selectedLanguage = binding.spinnerWordLanguageTranslation.selectedItem as? String ?: ""
            (activity as? MainActivity)?.requestRecognizeSpeech(selectedLanguage) {
                if (it == null) {
                    SharedHelper.setShowVoiceInput(false)
                    updateVoiceInputVisibility(false)
                } else {
                    binding.textInputTranslation.setText(it)
                }
            }
        }
        ArrayAdapter.createFromResource(context ?: return, R.array.array_languages, android.R.layout.simple_spinner_item).also { adapter ->
            adapter.setDropDownViewResource(R.layout.view_item_spinner)
            binding.spinnerWordLanguageTranslation.adapter = adapter
        }
        binding.spinnerWordLanguageTranslation.setSelection(SharedHelper.getSelectedLocaleTranslation())
        binding.spinnerWordLanguageTranslation.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                SharedHelper.setSelectedLocaleTranslation(position)
            }
            override fun onNothingSelected(parent: AdapterView<*>?) {}
        }
        binding.imageWordTranslate.setOnClickListener {
            val text = binding.textInputWord.text.toString()
            val fromLanguage = binding.spinnerWordLanguage.selectedItem as? String ?: ""
            val toLanguage = binding.spinnerWordLanguageTranslation.selectedItem as? String ?: ""

            if (text.isNotEmpty()) {
                binding.imageWordTranslate.visibility = View.INVISIBLE
                binding.progressBarWordTranslate.visibility = View.VISIBLE
                viewModel.onClickTranslate(text, fromLanguage, toLanguage) { translated ->
                    binding.imageWordTranslate.visibility = View.VISIBLE
                    binding.progressBarWordTranslate.visibility = View.INVISIBLE

                    if (translated != null) {
                        if (translated.isEmpty()) {
                            showToast(R.string.no_translation)
                        } else {
                            showPopupTranslation(binding.imageWordTranslate, translated) {
                                val existsTranslation = binding.textInputTranslation.text.toString()

                                if (existsTranslation.isEmpty()) {
                                    binding.textInputTranslation.setText(translated)
                                } else {
                                    val result = "$existsTranslation, $translated"
                                    binding.textInputTranslation.setText(result)
                                }
                            }
                        }
                    }
                }
            }
        }
        //TODO refactor
        binding.imageWordTranslateTranslation.setOnClickListener {
            val text = binding.textInputTranslation.text.toString()
            val fromLanguage = binding.spinnerWordLanguageTranslation.selectedItem as? String ?: ""
            val toLanguage = binding.spinnerWordLanguage.selectedItem as? String ?: ""

            if (text.isNotEmpty()) {
                binding.imageWordTranslateTranslation.visibility = View.INVISIBLE
                binding.progressBarWordTranslateTranslation.visibility = View.VISIBLE
                viewModel.onClickTranslate(text, fromLanguage, toLanguage) { translated ->
                    binding.imageWordTranslateTranslation.visibility = View.VISIBLE
                    binding.progressBarWordTranslateTranslation.visibility = View.INVISIBLE

                    if (translated != null) {
                        if (translated.isEmpty()) {
                            showToast(R.string.no_translation)
                        } else {
                            showPopupTranslation(binding.imageWordTranslateTranslation, translated) {
                                val existsTranslation = binding.textInputWord.text.toString()

                                if (existsTranslation.isEmpty()) {
                                    binding.textInputWord.setText(translated)
                                } else {
                                    val result = "$existsTranslation, $translated"
                                    binding.textInputWord.setText(result)
                                }
                            }
                        }
                    }
                }
            }
        }

        updateVoiceInputVisibility(SharedHelper.isShowVoiceInput())
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

        binding.textInputWord.setText(eng)
        binding.textInputTranscription.setText(transcription)
        binding.textInputTranslation.setText(rus)

        binding.textInputWord.addTextChangedListener(object: TextWatcher {
            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {}
            override fun afterTextChanged(p0: Editable?) {}
            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
                binding.textInputWord.error = null
            }
        })
        binding.textInputTranslation.addTextChangedListener(object: TextWatcher {
            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {}
            override fun afterTextChanged(p0: Editable?) {}
            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
                binding.textInputTranslation.error = null
            }
        })
        binding.textInputTranscription.addTextChangedListener(object: TextWatcher {
            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {}
            override fun afterTextChanged(p0: Editable?) {}
            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
                binding.textInputTranscription.error = null
            }
        })
        binding.textInputTranslation.setOnEditorActionListener { _, _, event ->
            if (event == null || !event.isShiftPressed) {
                handleClickedSave()
                true
            } else {
                false
            }
        }
    }

    override fun setupThemeColors() {
        binding.containerTextWordInputWord.boxStrokeColor = getPrimaryColorResId()
        binding.containerTextWordInputTranscription.boxStrokeColor = getPrimaryColorResId()
        binding.containerTextWordInputTranslation.boxStrokeColor = getPrimaryColorResId()
    }

    override fun onNavigationEvent(eventId: Int) {
        when (eventId) {
            NAVIGATION_WORD_EXISTS_ERROR -> {
                binding.textInputWord.error = getString(R.string.word_already_exists)
            }
            NAVIGATION_WORD_EXISTS_DIALOG -> {
                val dialog = DialogRestoreWord()
                dialog.confirmListener = listenerRestoreWord
                dialog.show(childFragmentManager, "Restore word Dialog")
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
           binding.textInputWord.hasFocus() -> hideKeyboard(binding.textInputWord)
           binding.textInputTranslation.hasFocus() -> hideKeyboard(binding.textInputTranslation)
           binding.textInputTranscription.hasFocus() -> hideKeyboard(binding.textInputTranscription)
       }
    }

    private fun handleClickedSave() {
        val eng = binding.textInputWord.text.toString()
        val rus = binding.textInputTranslation.text.toString()
        val transcription = binding.textInputTranscription.text.toString()

        when {
            eng.isEmpty() -> binding.textInputWord.error = getString(R.string.word_should_not_be_empty)
            rus.isEmpty() -> binding.textInputTranslation.error = getString(R.string.word_should_not_be_empty)
            eng.length > 200 -> binding.textInputWord.error = getString(R.string.word_length_limit)
            rus.length > 200 -> binding.textInputTranslation.error = getString(R.string.word_length_limit)
            transcription.length > 200 -> binding.textInputTranscription.error = getString(R.string.word_length_limit)
            else -> {
                hideKeyboard()
                viewModel.createOwnWord(eng, transcription, rus)
            }
        }
    }

    private fun updateVoiceInputVisibility(isShow: Boolean) {
        binding.imageWordMicrophone.visibility = if (isShow) View.VISIBLE else View.GONE
        binding.spinnerWordLanguage.visibility = if (isShow) View.VISIBLE else View.GONE
        binding.imageWordMicrophoneTranslation.visibility = if (isShow) View.VISIBLE else View.GONE
        binding.spinnerWordLanguageTranslation.visibility = if (isShow) View.VISIBLE else View.GONE
    }

    private fun showPopupTranslation(view: View, translationText: String, callback: () -> Unit) {
        val popupMenu = PopupMenu(view.context, view)

        popupMenu.inflate(R.menu.menu_popup_translation)
        popupMenu.menu.findItem(R.id.item_menu_translation)?.title = translationText
        popupMenu.setOnMenuItemClickListener {
            when (it.itemId) {
                R.id.item_menu_translation -> {
                    callback()
                }
            }
            true
        }

        popupMenu.show()
    }
}
