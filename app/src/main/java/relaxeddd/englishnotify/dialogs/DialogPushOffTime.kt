package relaxeddd.englishnotify.dialogs

import android.app.Dialog
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.DialogFragment
import com.google.android.material.textfield.TextInputEditText
import relaxeddd.englishnotify.R
import relaxeddd.englishnotify.common.DURATION_HOURS
import relaxeddd.englishnotify.common.START_HOUR
import relaxeddd.englishnotify.view_base.interfaces.ListenerResult

class DialogPushOffTime : DialogFragment() {

    var confirmListener: ListenerResult<Pair<Int, Int>>? = null

    private var editTextStartHour: TextInputEditText? = null
    private var editTextDurationHours: TextInputEditText? = null
    private var startHour = 0
    private var durationHours = 0

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        return activity?.let {
            val builder = AlertDialog.Builder(it)
            startHour = arguments?.getInt(START_HOUR, -1) ?: -1
            durationHours = arguments?.getInt(DURATION_HOURS, -1) ?: -1

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
        if (startHour != -1 && durationHours != -1 && durationHours != 0) {
            editTextStartHour?.setText(startHour.toString())
            editTextDurationHours?.setText(durationHours.toString())
        }

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
            val startHourStr = editTextStartHour?.text.toString()
            val durationHoursStr = editTextDurationHours?.text.toString()
            var startHour = 0
            var durationHours = 0

            try {
                if (startHourStr.isNotEmpty()) {
                    startHour = Integer.parseInt(editTextStartHour?.text.toString())
                }
                if (durationHoursStr.isNotEmpty()) {
                    durationHours = Integer.parseInt(editTextDurationHours?.text.toString())
                }
            } catch (e: NumberFormatException) {
                startHour = 0
                durationHours = 0
            }

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
