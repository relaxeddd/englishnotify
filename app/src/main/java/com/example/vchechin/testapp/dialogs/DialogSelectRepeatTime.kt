package com.example.vchechin.testapp.dialogs

import android.app.Dialog
import android.content.DialogInterface
import android.os.Bundle
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.DialogFragment
import com.example.vchechin.testapp.R
import com.example.vchechin.testapp.common.ListenerResult
import com.example.vchechin.testapp.common.SELECTED_ITEM

class DialogSelectRepeatTime : DialogFragment() {

    private var selectedItemIx: Int = 0
    var listener: ListenerResult<Int>? = null

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        return activity?.let {
            val builder = AlertDialog.Builder(it)
            selectedItemIx = arguments?.getInt(SELECTED_ITEM, 0) ?: 0

            builder.setTitle(R.string.receive_notifications_time)
                .setSingleChoiceItems(R.array.array_time_repeat, selectedItemIx) { _, which ->
                    selectedItemIx = which
                }.setPositiveButton(android.R.string.ok) { _, _ ->
                    listener?.onResult(selectedItemIx)
                }.setNegativeButton(android.R.string.cancel, null)
            builder.create()
        } ?: throw IllegalStateException("Activity cannot be null")
    }
}