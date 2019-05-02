package relaxeddd.englishnotify.ui.word

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.MenuItem
import android.view.View
import kotlinx.android.synthetic.main.fragment_word.*
import relaxeddd.englishnotify.R
import relaxeddd.englishnotify.common.BaseFragment
import relaxeddd.englishnotify.common.InjectorUtils
import relaxeddd.englishnotify.common.NAVIGATION_ACTIVITY_BACK
import relaxeddd.englishnotify.databinding.FragmentWordBinding

class FragmentWord : BaseFragment<ViewModelWord, FragmentWordBinding>() {

    override fun getLayoutResId() = R.layout.fragment_word
    override fun getToolbarTitleResId() = R.string.word
    override fun getViewModelFactory() = InjectorUtils.provideWordViewModelFactory(requireContext())
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
                val eng = text_input_word.text.toString()
                val rus = text_input_translation.text.toString()
                val transcription = text_input_transcription.text.toString()

                if (eng.isEmpty()) {
                    text_input_word.error = getString(R.string.word_should_not_be_empty)
                    return true
                }
                if (rus.isEmpty()) {
                    text_input_translation.error = getString(R.string.word_should_not_be_empty)
                    return true
                }
                if (eng.length > 200) {
                    text_input_word.error = getString(R.string.word_length_limit)
                    return true
                }
                if (rus.length > 200) {
                    text_input_translation.error = getString(R.string.word_length_limit)
                    return true
                }
                if (transcription.length > 200) {
                    text_input_transcription.error = getString(R.string.word_length_limit)
                    return true
                }

                viewModel.createOwnWord(eng, transcription, rus)
                return true
            }
            else -> return super.onOptionsItemSelected(item)
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
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
    }
}