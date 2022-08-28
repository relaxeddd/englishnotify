package relaxeddd.englishnotify.view_base.dialog

import android.app.Dialog
import android.os.Bundle
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.DialogFragment
import relaxeddd.englishnotify.common.SELECTED_ITEM
import relaxeddd.englishnotify.view_base.R
import relaxeddd.englishnotify.view_base.interfaces.ListenerResult

abstract class DialogSingleChoice : DialogFragment() {

    abstract val arrayResId: Int
    abstract val titleResId: Int
    open val positiveButtonTextResId: Int = android.R.string.ok
    open val negativeButtonTextResId: Int = R.string.cancel

    private var selectedItemIx: Int = 0
    var listener: ListenerResult<Int>? = null

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val builder = AlertDialog.Builder(requireContext())
        selectedItemIx = arguments?.getInt(SELECTED_ITEM, 0) ?: 0

        builder.setTitle(titleResId)
            .setSingleChoiceItems(arrayResId, selectedItemIx) { _, which ->
                selectedItemIx = which
            }.setPositiveButton(positiveButtonTextResId) { _, _ ->
                listener?.onResult(selectedItemIx)
            }.setNegativeButton(negativeButtonTextResId, null)

        return builder.create()
    }
}
