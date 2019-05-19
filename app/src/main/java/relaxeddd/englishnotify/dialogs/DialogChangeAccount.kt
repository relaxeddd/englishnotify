package relaxeddd.englishnotify.dialogs

import android.app.Dialog
import android.content.DialogInterface
import android.os.Bundle
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.DialogFragment
import relaxeddd.englishnotify.R
import relaxeddd.englishnotify.common.ListenerResult

class DialogChangeAccount : DialogFragment() {

    var confirmListener: ListenerResult<Boolean>? = null

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        return activity?.let {
            val builder = AlertDialog.Builder(it)

            builder.setMessage(R.string.change_account_text)
                .setPositiveButton(R.string.confirm) { _, _ ->
                    confirmListener?.onResult(true)
                    confirmListener = null
                }
                .setNegativeButton(android.R.string.no) { _, _ ->
                    confirmListener?.onResult(false)
                    confirmListener = null
                }
            builder.create()
        } ?: throw IllegalStateException("Activity cannot be null")
    }

    override fun onDismiss(dialog: DialogInterface) {
        super.onDismiss(dialog)
        confirmListener?.onResult(false)
    }
}