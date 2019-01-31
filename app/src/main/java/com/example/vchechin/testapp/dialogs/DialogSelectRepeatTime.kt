package com.example.vchechin.testapp.dialogs

import android.app.Dialog
import android.content.DialogInterface
import android.os.Bundle
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.DialogFragment
import com.example.vchechin.testapp.R

class DialogSelectRepeatTime : DialogFragment() {

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        return activity?.let {
            val builder = AlertDialog.Builder(it)

            builder.setTitle(R.string.receive_notifications_time)
                .setSingleChoiceItems(R.array.array_time_repeat, 2, { dialog, which ->

                }).setPositiveButton(android.R.string.ok, { dialog, id ->

                }).setNegativeButton(android.R.string.cancel, { dialog, id ->

                })
            builder.create()
        } ?: throw IllegalStateException("Activity cannot be null")
    }
}