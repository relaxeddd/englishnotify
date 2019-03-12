package relaxeddd.englishnotify.dialogs

import android.app.Dialog
import android.os.Bundle
import android.text.SpannableString
import android.text.Spanned
import android.text.method.LinkMovementMethod
import android.text.style.ClickableSpan
import android.text.style.UnderlineSpan
import android.view.View
import android.widget.TextView
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.DialogFragment
import relaxeddd.englishnotify.R
import relaxeddd.englishnotify.common.ListenerResult
import relaxeddd.englishnotify.common.openWebPrivacyPolicy

class DialogPrivacyPolicy : DialogFragment() {

    private var confirmListener: ListenerResult<Boolean>? = null

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        return activity?.let {
            val builder = AlertDialog.Builder(it)

            builder.setTitle(R.string.privacy_policy)
                .setMessage(R.string.privacy_policy_text)
                .setPositiveButton(R.string.confirm_and_continue) { _, _ -> confirmListener?.onResult(true) }
                .setNegativeButton(R.string.close) { _, _ -> confirmListener?.onResult(false) }
            builder.create()
        } ?: throw IllegalStateException("Activity cannot be null")
    }

    override fun onStart() {
        super.onStart()
        val messageView = dialog?.findViewById(android.R.id.message) as TextView
        configTextPrivacyPolicy(messageView)
    }

    fun setConfirmListener(confirmListener: ListenerResult<Boolean>?) {
        this.confirmListener = confirmListener
    }

    private fun configTextPrivacyPolicy(textView: TextView) {
        val privacyPolicy = textView.text.toString()
        val spannablePrivacyPolicy = SpannableString(privacyPolicy)
        val clickablePrivacyPolicy = object : ClickableSpan() {
            override fun onClick(textView: View) {
                openWebPrivacyPolicy(activity)
            }
        }

        setClickableSubstring(privacyPolicy, spannablePrivacyPolicy, getString(R.string.privacy_policy_in_sentence), clickablePrivacyPolicy)

        textView.text = spannablePrivacyPolicy
        textView.movementMethod = LinkMovementMethod.getInstance()
    }

    private fun setClickableSubstring(string: String, spannableString: SpannableString, substring: String, clickableSpan: ClickableSpan) {
        val firstIndex = string.indexOf(substring)
        val lastIndex = firstIndex + substring.length

        spannableString.setSpan(clickableSpan, firstIndex, lastIndex, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE)
        spannableString.setSpan(UnderlineSpan(), firstIndex, lastIndex, 0)
    }
}