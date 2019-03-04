package relaxeddd.pushenglish.dialogs

import android.app.Dialog
import android.os.Bundle
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.DialogFragment
import relaxeddd.pushenglish.R
import relaxeddd.pushenglish.common.CHECKED_ITEMS
import relaxeddd.pushenglish.common.ITEMS
import relaxeddd.pushenglish.common.ListenerResult

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
                if (itemsNames.indexOf(checkedItem) != -1) {
                    checkedItemIxs[itemsNames.indexOf(checkedItem)] = true
                }
            }

            builder.setTitle(R.string.notification_categories)
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