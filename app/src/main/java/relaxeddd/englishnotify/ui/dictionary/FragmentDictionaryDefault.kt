package relaxeddd.englishnotify.ui.dictionary

import android.os.Bundle
import android.view.View
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.card.MaterialCardView
import kotlinx.android.synthetic.main.fragment_dictionary.*
import relaxeddd.englishnotify.databinding.FragmentDictionaryBinding

abstract class FragmentDictionaryDefault<VM : ViewModelDictionary, A : AdapterWords<*>> : FragmentDictionary<VM, FragmentDictionaryBinding, A>() {

    override fun getRecyclerViewWords(): RecyclerView = recycler_view_dictionary
    override fun getCardViewFilter() : MaterialCardView = card_view_dictionary_filter

    override fun configureBinding() {
        super.configureBinding()
        binding.viewModel = viewModel
        binding.clickListenerCloseFilter = clickListenerCloseFilter
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        viewModel.wordsFiltered.observe(viewLifecycleOwner, Observer { words ->
            binding.hasWords = (words != null && words.isNotEmpty())
            updateAdapter(words)
        })
        viewModel.user.observe(viewLifecycleOwner, Observer { user ->
            adapter.languageType = user?.learnLanguageType ?: 0
        })
    }
}