package relaxeddd.englishnotify.dialogs

import android.app.Dialog
import android.os.Bundle
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.DialogFragment
import relaxeddd.englishnotify.R
import relaxeddd.englishnotify.common.CHECKED_ITEMS
import relaxeddd.englishnotify.common.ITEMS
import relaxeddd.englishnotify.common.ListenerResult
import relaxeddd.englishnotify.common.getStringByResName

class DialogCheckTags : DialogFragment() {

    private var items: Array<String> = arrayOf()
    private var itemsTitles: Array<String> = arrayOf()
    private var checkedItemIxs: BooleanArray = BooleanArray(0)
    var listener: ListenerResult<List<String>>? = null

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        return activity?.let {
            val builder = AlertDialog.Builder(it)
            val checkedItems = arguments?.getStringArray(CHECKED_ITEMS) ?: arrayOf()
            items = arguments?.getStringArray(ITEMS) ?: arrayOf()
            checkedItemIxs = BooleanArray(items.size)
            itemsTitles = Array(items.size) {""}

            for (checkedItem in checkedItems) {
                if (items.indexOf(checkedItem) != -1) {
                    checkedItemIxs[items.indexOf(checkedItem)] = true
                }
            }
            for ((ix, item) in items.withIndex()) {
                itemsTitles[ix] = getStringByResName(item)
            }

            builder.setTitle(R.string.notification_categories)
                .setMultiChoiceItems(itemsTitles, checkedItemIxs) { _, which, isChecked ->
                    checkedItemIxs[which] = isChecked
                }.setPositiveButton(android.R.string.ok) { _, _ ->
                    val resultCheckedItems = ArrayList<String>()

                    for ((ix, isCheckedItemIx) in checkedItemIxs.withIndex()) {
                        if (isCheckedItemIx) {
                            resultCheckedItems.add(items[ix])
                        }
                    }

                    listener?.onResult(resultCheckedItems)
                }.setNegativeButton(android.R.string.cancel, null)
            builder.create()
        } ?: throw IllegalStateException("Activity cannot be null")
    }
}