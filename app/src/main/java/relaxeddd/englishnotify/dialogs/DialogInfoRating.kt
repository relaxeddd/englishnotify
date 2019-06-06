package relaxeddd.englishnotify.dialogs

import android.app.Dialog
import android.os.Bundle
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.DialogFragment
import relaxeddd.englishnotify.R

class DialogInfoRating : DialogFragment() {

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        return activity?.let {
            val builder = AlertDialog.Builder(it)

            builder.setTitle(R.string.information)
                .setMessage(getString(R.string.text_rating_info))
                .setPositiveButton(android.R.string.ok) { _, _ -> }
            builder.create()
        } ?: throw IllegalStateException("Activity cannot be null")
    }
}