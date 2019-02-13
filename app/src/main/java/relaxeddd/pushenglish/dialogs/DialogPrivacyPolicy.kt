package relaxeddd.pushenglish.dialogs

import android.app.Dialog
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.text.SpannableString
import android.text.Spanned
import android.text.method.LinkMovementMethod
import android.text.style.ClickableSpan
import android.text.style.UnderlineSpan
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.Window
import androidx.fragment.app.DialogFragment
import relaxeddd.pushenglish.R
import relaxeddd.pushenglish.common.ListenerResult
import relaxeddd.pushenglish.common.openWebPrivacyPolicy
import kotlinx.android.synthetic.main.dialog_privacy_policy.view.*

class DialogPrivacyPolicy : DialogFragment() {

    private var confirmListener: ListenerResult<Boolean>? = null

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val dialogView = inflater.inflate(R.layout.dialog_privacy_policy, container, false)

        dialogView.button_privacy_policy_confirm.setOnClickListener {
            confirmListener?.onResult(true)
            dismiss()
        }
        dialogView.button_privacy_policy_decline.setOnClickListener {
            confirmListener?.onResult(false)
            dismiss()
        }

        return dialogView
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val dialog = super.onCreateDialog(savedInstanceState)
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        dialog.window.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        isCancelable = false
        return dialog
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        configTextPrivacyPolicy(view)
    }

    override fun onStart() {
        super.onStart()
        val width = ViewGroup.LayoutParams.MATCH_PARENT
        val height = ViewGroup.LayoutParams.MATCH_PARENT
        dialog?.window?.setLayout(width, height)
    }

    fun setConfirmListener(confirmListener: ListenerResult<Boolean>?) {
        this.confirmListener = confirmListener
    }

    private fun configTextPrivacyPolicy(view: View) {
        val privacyPolicy = view.text_privacy_policy.text.toString()
        val spannablePrivacyPolicy = SpannableString(privacyPolicy)
        val clickablePrivacyPolicy = object : ClickableSpan() {
            override fun onClick(textView: View) {
                openWebPrivacyPolicy(activity)
            }
        }

        setClickableSubstring(privacyPolicy, spannablePrivacyPolicy, getString(R.string.privacy_policy), clickablePrivacyPolicy)

        view.text_privacy_policy.text = spannablePrivacyPolicy
        view.text_privacy_policy.movementMethod = LinkMovementMethod.getInstance()
    }

    private fun setClickableSubstring(string: String, spannableString: SpannableString, substring: String, clickableSpan: ClickableSpan) {
        val firstIndex = string.indexOf(substring)
        val lastIndex = firstIndex + substring.length

        spannableString.setSpan(clickableSpan, firstIndex, lastIndex, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE)
        spannableString.setSpan(UnderlineSpan(), firstIndex, lastIndex, 0)
    }
}