package relaxeddd.englishnotify.dialogs

import android.app.Dialog
import android.os.Bundle
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.DialogFragment
import relaxeddd.englishnotify.R
import relaxeddd.englishnotify.common.ListenerResult

class DialogVoiceInput : DialogFragment() {

    var confirmListener: ListenerResult<Boolean>? = null

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        return activity?.let {
            val builder = AlertDialog.Builder(it)

            builder.setMessage(R.string.voice_input_error)
                .setPositiveButton(R.string.hide) { _, _ -> confirmListener?.onResult(true) }
                .setNegativeButton(R.string.no) { _, _ -> confirmListener?.onResult(false) }
            builder.create()
        } ?: throw IllegalStateException("Activity cannot be null")
    }
}