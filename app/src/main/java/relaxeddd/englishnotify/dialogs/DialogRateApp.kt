package relaxeddd.englishnotify.dialogs

import android.app.Dialog
import android.os.Bundle
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.DialogFragment
import relaxeddd.englishnotify.R
import relaxeddd.englishnotify.common.SharedHelper
import relaxeddd.englishnotify.common.openWebApplication

class DialogRateApp : DialogFragment() {

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        return activity?.let {
            val builder = AlertDialog.Builder(it)

            builder.setTitle(R.string.rate_app_pls)
                .setMessage(R.string.text_rate_app)
                .setPositiveButton(R.string.rate) { _, _ ->
                    SharedHelper.setCancelledRateDialog(true)
                    openWebApplication(activity)
                }
                .setNeutralButton(R.string.no_thanks) { _, _ -> SharedHelper.setCancelledRateDialog(true) }
                .setNegativeButton(R.string.later) { _, _ -> }
            builder.create()
        } ?: throw IllegalStateException("Activity cannot be null")
    }
}