package relaxeddd.englishnotify.dialogs

import android.app.Dialog
import android.os.Bundle
import android.text.SpannableString
import android.text.method.LinkMovementMethod
import android.text.util.Linkify
import android.widget.TextView
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.DialogFragment
import relaxeddd.englishnotify.R
import relaxeddd.englishnotify.view_base.interfaces.ListenerResult

class DialogNotificationsNotShow : DialogFragment() {

    var confirmListener: ListenerResult<Boolean>? = null

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        return activity?.let {
            val builder = AlertDialog.Builder(it)
            val text = SpannableString(getString(R.string.text_notifications_not_show))
            Linkify.addLinks(text, Linkify.ALL)

            builder.setMessage(text)
                .setPositiveButton(R.string.settings) { _, _ -> confirmListener?.onResult(true) }
            builder.create()
        } ?: throw IllegalStateException("Activity cannot be null")
    }

    override fun onResume() {
        super.onResume()
        dialog?.findViewById<TextView>(android.R.id.message)?.movementMethod = LinkMovementMethod.getInstance()
    }
}
