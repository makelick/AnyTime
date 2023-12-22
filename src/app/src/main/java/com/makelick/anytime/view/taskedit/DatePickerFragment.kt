package com.makelick.anytime.view.taskedit

import android.app.DatePickerDialog
import android.app.Dialog
import android.os.Bundle
import android.widget.DatePicker
import androidx.fragment.app.DialogFragment
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

class DatePickerFragment(
    private val date: String,
    private val onDateSet: (day: Int, month: Int, year: Int) -> Unit
) : DialogFragment(), DatePickerDialog.OnDateSetListener {

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val calendar = Calendar.getInstance()
        val selectedDate = SimpleDateFormat("dd.MM.yyyy", Locale.getDefault()).parse(date)
        calendar.time = selectedDate ?: calendar.time

        return DatePickerDialog(
            requireContext(),
            this,
            calendar.get(Calendar.YEAR),
            calendar.get(Calendar.MONTH),
            calendar.get(Calendar.DAY_OF_MONTH)
        )

    }

    override fun onDateSet(view: DatePicker, year: Int, month: Int, day: Int) {
        onDateSet(day, month, year)
    }
}