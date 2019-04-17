package relaxeddd.englishnotify.dialogs

import android.app.Dialog
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.DialogFragment
import com.google.android.material.textfield.TextInputEditText
import relaxeddd.englishnotify.R
import relaxeddd.englishnotify.common.*

class DialogPushOffTime : DialogFragment() {

    var confirmListener: ListenerResult<Pair<Int, Int>>? = null

    private var editTextStartHour: TextInputEditText? = null
    private var editTextDurationHours: TextInputEditText? = null
    private var startHour = 0
    private var durationHours = 0

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        return activity?.let {
            val builder = AlertDialog.Builder(it)
            startHour = arguments?.getInt(START_HOUR, 0) ?: 0
            durationHours = arguments?.getInt(DURATION_HOURS, 0) ?: 0

            builder.setTitle(R.string.night_time)
                    .setView(R.layout.dialog_push_off_time)
                    .setPositiveButton(android.R.string.ok) { _, _ ->  }
                    .setNegativeButton(android.R.string.cancel, null)

            builder.create()
        } ?: throw IllegalStateException("Activity cannot be null")
    }

    override fun onStart() {
        super.onStart()

        editTextStartHour = dialog?.findViewById(R.id.edit_text_push_off_time_start_hour)
        editTextDurationHours = dialog?.findViewById(R.id.edit_text_push_off_time_duration)
        editTextStartHour?.setText(startHour.toString())
        editTextDurationHours?.setText(durationHours.toString())

        editTextStartHour?.addTextChangedListener(object: TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun afterTextChanged(s: Editable?) {}

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                editTextStartHour?.error = null
            }
        })
        editTextDurationHours?.addTextChangedListener(object: TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun afterTextChanged(s: Editable?) {}

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                editTextDurationHours?.error = null
            }
        })
    }

    override fun onResume() {
        super.onResume()

        (dialog as AlertDialog).getButton(AlertDialog.BUTTON_POSITIVE).setOnClickListener {
            val startHour = Integer.parseInt(editTextStartHour?.text.toString())
            val durationHours = Integer.parseInt(editTextDurationHours?.text.toString())

            if (startHour < 0 || startHour >= 24) {
                editTextStartHour?.error = getString(R.string.error_text_time)
                return@setOnClickListener
            }
            if (durationHours < 0 || durationHours >= 24) {
                editTextDurationHours?.error = getString(R.string.error_text_time)
                return@setOnClickListener
            }

            val result = Pair(startHour, durationHours)
            confirmListener?.onResult(result)
            dismiss()
        }
    }
}