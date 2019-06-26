package relaxeddd.englishnotify.dialogs

import android.app.Dialog
import android.os.Bundle
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.DialogFragment
import relaxeddd.englishnotify.R
import relaxeddd.englishnotify.common.ListenerResult
import relaxeddd.englishnotify.common.SELECTED_ITEM

class DialogVote : DialogFragment() {

    companion object {
        const val VOTE_NONE = 0
        const val VOTE_NO = 1
        const val VOTE_YES = 2
        const val VOTE_NO_OPTION = 3
        const val VOTE_YES_OPTION = 4
    }

    private var selectedItemIx: Int = 0
    var listener: ListenerResult<Int>? = null

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        return activity?.let {
            val builder = AlertDialog.Builder(it)
            selectedItemIx = arguments?.getInt(SELECTED_ITEM, 0) ?: 0

            builder.setTitle(R.string.you_receive_notifications)
                .setSingleChoiceItems(R.array.array_dialog_receive_notifications, selectedItemIx) { _, which ->
                    selectedItemIx = which
                }.setPositiveButton(android.R.string.ok) { _, _ ->
                    listener?.onResult(selectedItemIx)
                }.setNegativeButton(android.R.string.cancel, null)
            builder.create()
        } ?: throw IllegalStateException("Activity cannot be null")
    }
}