package relaxeddd.englishnotify.dialogs

import android.app.Dialog
import android.os.Bundle
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.DialogFragment
import relaxeddd.englishnotify.R
import relaxeddd.englishnotify.common.ListenerResult

class DialogConfirmLogout : DialogFragment() {

    private var confirmListener: ListenerResult<Boolean>? = null

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        return activity?.let {
            val builder = AlertDialog.Builder(it)

            builder.setMessage(R.string.do_you_really_want_to_logout)
                .setPositiveButton(android.R.string.yes) { _, _ -> confirmListener?.onResult(true) }
                .setNegativeButton(android.R.string.no) { _, _ -> confirmListener?.onResult(false) }
            builder.create()
        } ?: throw IllegalStateException("Activity cannot be null")
    }

    fun setConfirmListener(confirmListener: ListenerResult<Boolean>?) {
        this.confirmListener = confirmListener
    }
}