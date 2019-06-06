package relaxeddd.englishnotify.dialogs

import android.app.Dialog
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.widget.Button
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.DialogFragment
import com.google.android.material.textfield.TextInputEditText
import relaxeddd.englishnotify.R
import relaxeddd.englishnotify.common.ListenerResult
import relaxeddd.englishnotify.common.isValidNickname

class DialogEnterName : DialogFragment() {

    private var editText: TextInputEditText? = null
    private var buttonApply: Button? = null
    var confirmListener: ListenerResult<String>? = null

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        return activity?.let {
            val builder = AlertDialog.Builder(it)

            builder.setTitle(R.string.enter_name)
                .setView(R.layout.dialog_enter_name)
            builder.create()
        } ?: throw IllegalStateException("Activity cannot be null")
    }

    override fun onStart() {
        super.onStart()
        editText = dialog?.findViewById(R.id.edit_text_enter_name)
        buttonApply = dialog?.findViewById(R.id.button_enter_name_apply)

        editText?.addTextChangedListener(object: TextWatcher {
            override fun afterTextChanged(s: Editable?) {}
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) { editText?.error = null }
        })
        buttonApply?.setOnClickListener {
            val nickname = editText?.text.toString()

            if (isValidNickname(nickname)) {
                confirmListener?.onResult(nickname)
                dismiss()
            } else {
                editText?.error = getString(R.string.error_name)
            }
        }
    }
}