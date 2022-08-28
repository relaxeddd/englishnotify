package relaxeddd.englishnotify.view_base.dialog

import android.app.Dialog
import android.os.Bundle
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.DialogFragment
import relaxeddd.englishnotify.common.EMPTY_RES
import relaxeddd.englishnotify.view_base.R
import relaxeddd.englishnotify.view_base.interfaces.ListenerResult

abstract class DialogSimpleChoice : DialogFragment() {

    open val titleResId: Int = EMPTY_RES
    open val textResId: Int = EMPTY_RES
    open val positiveButtonTextResId: Int = android.R.string.ok
    open val negativeButtonTextResId: Int = R.string.cancel
    open val isCanBeCancelled: Boolean = true

    var confirmListener: ListenerResult<Boolean>? = null

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        isCancelable = isCanBeCancelled

        val builder = AlertDialog.Builder(requireContext())

        builder.setPositiveButton(positiveButtonTextResId) { _, _ ->
            confirmListener?.onResult(true)
        }
        if (negativeButtonTextResId != EMPTY_RES) {
            builder.setNegativeButton(negativeButtonTextResId) { _, _ ->
                confirmListener?.onResult(false)
            }
        }
        if (titleResId != EMPTY_RES) {
            builder.setTitle(titleResId)
        }
        if (textResId != EMPTY_RES) {
            builder.setMessage(getString(textResId))
        }
        if (titleResId == EMPTY_RES && textResId == EMPTY_RES) {
            builder.setTitle("")
        }

        return builder.create()
    }
}
