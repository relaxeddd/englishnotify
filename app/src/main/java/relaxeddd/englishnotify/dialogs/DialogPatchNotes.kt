package relaxeddd.englishnotify.dialogs

import android.app.Dialog
import android.os.Bundle
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.DialogFragment
import relaxeddd.englishnotify.R

class DialogPatchNotes : DialogFragment() {

    companion object {
        const val VERSION = "1.3.2"
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        return activity?.let {
            val builder = AlertDialog.Builder(it)

            builder.setTitle(getString(R.string.new_version))
                .setMessage(getString(R.string.patch_notes_1_3_2))
                .setPositiveButton(android.R.string.ok) { _, _ -> }
            builder.create()
        } ?: throw IllegalStateException("Activity cannot be null")
    }
}