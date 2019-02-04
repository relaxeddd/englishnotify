package com.example.vchechin.testapp.dialogs

import android.app.Dialog
import android.os.Bundle
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.DialogFragment
import com.example.vchechin.testapp.R
import com.example.vchechin.testapp.common.*

class DialogCheckTags : DialogFragment() {

    private var itemsNames: Array<String> = arrayOf()
    private var checkedItemIxs: BooleanArray = BooleanArray(0)
    var listener: ListenerResult<List<String>>? = null

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        return activity?.let {
            val builder = AlertDialog.Builder(it)
            val checkedItems = arguments?.getStringArray(CHECKED_ITEMS) ?: arrayOf()
            itemsNames = arguments?.getStringArray(ITEMS) ?: arrayOf()
            checkedItemIxs = BooleanArray(itemsNames.size)

            for (checkedItem in checkedItems) {
                checkedItemIxs[itemsNames.indexOf(checkedItem)] = true
            }

            builder.setTitle(R.string.receive_notifications_time)
                .setMultiChoiceItems(itemsNames, checkedItemIxs) { _, which, isChecked ->
                    checkedItemIxs[which] = isChecked
                }.setPositiveButton(android.R.string.ok) { _, _ ->
                    val resultCheckedItems = ArrayList<String>()

                    for ((ix, isCheckedItemIx) in checkedItemIxs.withIndex()) {
                        if (isCheckedItemIx) {
                            resultCheckedItems.add(itemsNames[ix])
                        }
                    }

                    listener?.onResult(resultCheckedItems)
                }.setNegativeButton(android.R.string.cancel, null)
            builder.create()
        } ?: throw IllegalStateException("Activity cannot be null")
    }
}