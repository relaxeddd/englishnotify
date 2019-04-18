package relaxeddd.englishnotify.dialogs

import android.app.Dialog
import android.os.Bundle
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.DialogFragment
import relaxeddd.englishnotify.R

class DialogPatchNotes : DialogFragment() {

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        return activity?.let {
            val builder = AlertDialog.Builder(it)

            builder.setTitle(getString(R.string.new_version, "1.0.0"))
                .setMessage(getString(R.string.patch_notes_1_0_0))
                .setPositiveButton(android.R.string.ok) { _, _ -> }
            builder.create()
        } ?: throw IllegalStateException("Activity cannot be null")
    }
}