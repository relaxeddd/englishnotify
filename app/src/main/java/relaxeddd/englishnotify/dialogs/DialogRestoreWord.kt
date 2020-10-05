package relaxeddd.englishnotify.dialogs

import android.app.Dialog
import android.os.Bundle
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.DialogFragment
import relaxeddd.englishnotify.R
import relaxeddd.englishnotify.common.ListenerResult

class DialogRestoreWord : DialogFragment() {

    var confirmListener: ListenerResult<Boolean>? = null

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        return activity?.let {
            val builder = AlertDialog.Builder(it)

            builder.setTitle(R.string.word_already_exists)
                .setPositiveButton(R.string.reset_progress) { _, _ ->
                    confirmListener?.onResult(true)
                }
                .setNegativeButton(R.string.cancel) { _, _ ->
                    confirmListener?.onResult(false)
                }
            builder.create()
        } ?: throw IllegalStateException("Activity cannot be null")
    }
}
