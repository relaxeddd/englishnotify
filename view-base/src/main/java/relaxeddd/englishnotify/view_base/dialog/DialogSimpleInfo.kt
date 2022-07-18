package relaxeddd.englishnotify.view_base.dialog

import android.app.Dialog
import android.os.Bundle
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.DialogFragment
import relaxeddd.englishnotify.common.EMPTY_RES

abstract class DialogSimpleInfo : DialogFragment() {

    abstract val titleResId: Int
    open val textResId: Int = EMPTY_RES
    open val positiveButtonTextResId: Int = android.R.string.ok
    open val arg: String = ""

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val builder = AlertDialog.Builder(requireContext())

        builder.setTitle(titleResId)
            .setPositiveButton(positiveButtonTextResId) { _, _ -> }
        if (textResId != EMPTY_RES) {
            if (arg.isEmpty()) {
                builder.setMessage(textResId)
            } else {
                builder.setMessage(getString(textResId, arg))
            }
        }

        return builder.create()
    }
}
