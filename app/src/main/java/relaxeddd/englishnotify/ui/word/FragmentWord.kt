package relaxeddd.englishnotify.ui.word

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.EditorInfo
import android.widget.AdapterView
import android.widget.ArrayAdapter
import androidx.core.view.doOnLayout
import androidx.fragment.app.viewModels
import androidx.lifecycle.ViewModelProvider
import com.google.android.play.core.review.ReviewManagerFactory
import relaxeddd.englishnotify.R
import relaxeddd.englishnotify.common.*
import relaxeddd.englishnotify.databinding.FragmentWordBinding
import relaxeddd.englishnotify.dialogs.DialogRestoreWord
import relaxeddd.englishnotify.preferences.Preferences
import relaxeddd.englishnotify.ui.categories.AdapterCategories
import relaxeddd.englishnotify.ui.main.MainActivity
import relaxeddd.englishnotify.view_base.BaseFragment
import relaxeddd.englishnotify.view_base.interfaces.ListenerResult
import javax.inject.Inject

class FragmentWord : BaseFragment<ViewModelWord, FragmentWordBinding>() {

    @Inject
    override lateinit var prefs: Preferences

    @Inject
    lateinit var viewModelFactory: ViewModelProvider.Factory

    override val viewModel by viewModels<ViewModelWord> { viewModelFactory }

    private var adapter: AdapterCategories? = null

    private val listenerRestoreWord: ListenerResult<Boolean> = object: ListenerResult<Boolean> {
        override fun onResult(result: Boolean) {
            if (result) {
                viewModel.forceUpdateFindWord()
            }
        }
    }

    override fun getToolbarTitleResId() = R.string.add_word
    override fun getMenuResId() = R.menu.menu_accept
    override fun isHomeMenuButtonEnabled() = true
    override fun getHomeMenuButtonIconResId() = R.drawable.ic_back
    override fun getHomeMenuButtonListener(): () -> Unit = { onNavigationEvent(NAVIGATION_ACTIVITY_BACK) }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        binding = FragmentWordBinding.inflate(inflater)
        return binding?.root
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

        val binding = binding ?: return
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
        binding.textInputOwnTag.addTextChangedListener(object: TextWatcher {
            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {}
            override fun afterTextChanged(p0: Editable?) {}
            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
                adapter?.clearSelection()
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

        binding.textInputWord.doOnLayout {
            showKeyboard(it)
        }

        binding.imageWordMicrophone.setOnClickListener {
            val selectedLanguage = binding.spinnerWordLanguage.selectedItem as? String ?: ""
            (activity as? MainActivity)?.requestRecognizeSpeech(selectedLanguage) {
                if (it == null) {
                    prefs.setShowVoiceInput(false)
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
        binding.spinnerWordLanguage.setSelection(prefs.getSelectedLocaleWord())
        binding.spinnerWordLanguage.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                prefs.setSelectedLocaleWord(position)
            }
            override fun onNothingSelected(parent: AdapterView<*>?) {}
        }

        binding.imageWordMicrophoneTranslation.setOnClickListener {
            val selectedLanguage = binding.spinnerWordLanguageTranslation.selectedItem as? String ?: ""
            (activity as? MainActivity)?.requestRecognizeSpeech(selectedLanguage) {
                if (it == null) {
                    prefs.setShowVoiceInput(false)
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
        binding.spinnerWordLanguageTranslation.setSelection(prefs.getSelectedLocaleTranslation())
        binding.spinnerWordLanguageTranslation.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                prefs.setSelectedLocaleTranslation(position)
            }
            override fun onNothingSelected(parent: AdapterView<*>?) {}
        }

        adapter = AdapterCategories(viewModel)
        binding.recyclerViewWordOwnCategories.itemAnimator = null
        binding.recyclerViewWordOwnCategories.adapter = adapter

        updateVoiceInputVisibility(prefs.isShowVoiceInput())
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.item_menu_accept -> {
                binding?.textInputTranslation?.onEditorAction(EditorInfo.IME_ACTION_DONE)
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    override fun subscribeToViewModel() {
        super.subscribeToViewModel()

        viewModel.categories.observe(viewLifecycleOwner) { items ->
            if (items != null && items.isNotEmpty()) adapter?.submitList(items)
        }
    }

    override fun setupThemeColors() {
        val binding = binding ?: return
        binding.containerTextWordInputWord.boxStrokeColor = getPrimaryColorResId(prefs.getAppThemeType())
        binding.containerTextWordInputTranscription.boxStrokeColor = getPrimaryColorResId(prefs.getAppThemeType())
        binding.containerTextWordInputTranslation.boxStrokeColor = getPrimaryColorResId(prefs.getAppThemeType())
        binding.containerTextWordOwnTag.boxStrokeColor = getPrimaryColorResId(prefs.getAppThemeType())
    }

    override fun onNavigationEvent(eventId: Int) {
        when (eventId) {
            NAVIGATION_WORD_EXISTS_ERROR -> {
                binding?.textInputWord?.error = getString(R.string.word_already_exists)
            }
            NAVIGATION_WORD_EXISTS_DIALOG -> {
                val dialog = DialogRestoreWord()
                dialog.confirmListener = listenerRestoreWord
                dialog.show(childFragmentManager, "Restore word Dialog")
            }
            NAVIGATION_ACTIVITY_BACK -> {
                hideKeyboard()
                super.onNavigationEvent(eventId)
            }
            else -> super.onNavigationEvent(eventId)
        }
    }

    private fun hideKeyboard() {
        val binding = binding ?: return
       when {
           binding.textInputWord.hasFocus() -> hideKeyboard(binding.textInputWord)
           binding.textInputTranslation.hasFocus() -> hideKeyboard(binding.textInputTranslation)
           binding.textInputTranscription.hasFocus() -> hideKeyboard(binding.textInputTranscription)
           binding.textInputOwnTag.hasFocus() -> hideKeyboard(binding.textInputOwnTag)
       }
    }

    private fun handleClickedSave() {
        val binding = binding ?: return
        val eng = binding.textInputWord.text.toString()
        val rus = binding.textInputTranslation.text.toString()
        val transcription = binding.textInputTranscription.text.toString()
        val ownCategory = binding.textInputOwnTag.text.toString()

        when {
            eng.isEmpty() -> binding.textInputWord.error = getString(R.string.word_should_not_be_empty)
            rus.isEmpty() -> binding.textInputTranslation.error = getString(R.string.word_should_not_be_empty)
            eng.length > 200 -> binding.textInputWord.error = getString(R.string.word_length_limit)
            rus.length > 200 -> binding.textInputTranslation.error = getString(R.string.word_length_limit)
            transcription.length > 200 -> binding.textInputTranscription.error = getString(R.string.word_length_limit)
            else -> {
                hideKeyboard()

                // TODO: rate app dialog is disabled
                if (false) {
                    val manager = ReviewManagerFactory.create(requireContext())
                    val request = manager.requestReviewFlow()

                    request.addOnCompleteListener { ableToRateAnswer ->
                        if (ableToRateAnswer.isSuccessful) {
                            val reviewInfo = ableToRateAnswer.result
                            val flow = manager.launchReviewFlow(requireActivity(), reviewInfo)
                            flow.addOnCompleteListener {
                                viewModel.createOwnWord(eng, transcription, rus, ownCategory)
                            }
                        } else {
                            viewModel.createOwnWord(eng, transcription, rus, ownCategory)
                        }
                    }
                } else {
                    viewModel.createOwnWord(eng, transcription, rus, ownCategory)
                }
            }
        }
    }

    private fun updateVoiceInputVisibility(isShow: Boolean) {
        val binding = binding ?: return
        binding.imageWordMicrophone.visibility = if (isShow) View.VISIBLE else View.GONE
        binding.spinnerWordLanguage.visibility = if (isShow) View.VISIBLE else View.GONE
        binding.imageWordMicrophoneTranslation.visibility = if (isShow) View.VISIBLE else View.GONE
        binding.spinnerWordLanguageTranslation.visibility = if (isShow) View.VISIBLE else View.GONE
    }
}
