package relaxeddd.englishnotify.dialogs

import android.app.Dialog
import android.os.Bundle
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.DialogFragment
import relaxeddd.englishnotify.R
import relaxeddd.englishnotify.common.*

class DialogSelectTag : DialogFragment() {

    private var items: Array<String> = arrayOf()
    private var itemsTitles: Array<String> = arrayOf()
    private var checkedItemIx: Int = 0
    var listener: ListenerResult<String>? = null

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        return activity?.let {
            val builder = AlertDialog.Builder(it)
            val checkedItem = arguments?.getString(CHECKED_ITEM) ?: ""
            items = arguments?.getStringArray(ITEMS) ?: arrayOf()
            itemsTitles = Array(items.size) {""}

            if (items.indexOf(checkedItem) != -1) {
                checkedItemIx = items.indexOf(checkedItem)
            }
            for ((ix, item) in items.withIndex()) {
                itemsTitles[ix] = getStringByResName(item)
            }

            builder.setTitle(R.string.notification_categories)
                .setSingleChoiceItems(itemsTitles, checkedItemIx) { _, which ->
                    checkedItemIx = which
                }.setPositiveButton(android.R.string.ok) { _, _ ->
                    listener?.onResult(items[checkedItemIx])
                }.setNegativeButton(android.R.string.cancel, null)
            builder.create()
        } ?: throw IllegalStateException("Activity cannot be null")
    }
}