package com.strese.pocketapp.ui.datetimecalc

import android.app.DatePickerDialog
import android.app.TimePickerDialog
import android.content.ClipData
import android.content.ClipboardManager
import android.content.Intent
import android.graphics.Typeface
import android.os.Bundle
import android.provider.CalendarContract
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat.getSystemService
import androidx.core.view.MenuProvider
import androidx.fragment.app.Fragment
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.ViewModelProvider
import com.strese.pocketapp.R
import com.strese.pocketapp.databinding.FragmentDateTimeCalcBinding
import java.util.Calendar

class DateTimeCalcFragment : Fragment() {
    private var _binding: FragmentDateTimeCalcBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = FragmentDateTimeCalcBinding.inflate(inflater, container, false)

        val dateTimeCalcViewModel = ViewModelProvider(this)[DateTimeCalcViewModel::class.java]

        val dateButton = binding.dateButton
        val timeButton = binding.timeButton
        val addUpButton = binding.addUpButton
        val subtractButton = binding.subtractButton
        val endDateResultText = binding.endDateResultText
        val endTimeResultText = binding.endTimeResultText

        // Create a DatePickerDialog/TimePickerDialog when user clicks on one of the top buttons
        dateButton.setOnClickListener { showDatePicker(dateTimeCalcViewModel) }
        timeButton.setOnClickListener { showTimePicker(dateTimeCalcViewModel) }

        // Update the text on the start date button when the start date changes
        dateTimeCalcViewModel.startDateLiveData.observe(viewLifecycleOwner) {
            val newText = getString(R.string.start_date) + it
            dateButton.text = newText
        }

        // Update the text on the start time button when the start time changes
        dateTimeCalcViewModel.startTimeLiveData.observe(viewLifecycleOwner) {
            val newText = getString(R.string.start_time_prefix) + it + getString(R.string.start_time_suffix)
            timeButton.text = newText
        }

        // Change arithmetic method when clicking either the add up text or the subtract text
        addUpButton.setOnClickListener { setArithmeticMethod(dateTimeCalcViewModel, true) }
        subtractButton.setOnClickListener { setArithmeticMethod(dateTimeCalcViewModel, false) }

        // Highlight the the selected arithmetic method when user clicks on it
        dateTimeCalcViewModel.addUpLiveData.observe(viewLifecycleOwner) {
            adjustArithmeticButtons(addUpButton, subtractButton, it)
        }

        // Listeners for all the EditText fields
        addTextChangedListener(dateTimeCalcViewModel, binding.yearsEditText)
        addTextChangedListener(dateTimeCalcViewModel, binding.monthsEditText)
        addTextChangedListener(dateTimeCalcViewModel, binding.weeksEditText)
        addTextChangedListener(dateTimeCalcViewModel, binding.daysEditText)
        addTextChangedListener(dateTimeCalcViewModel, binding.hoursEditText)
        addTextChangedListener(dateTimeCalcViewModel, binding.minsEditText)

        // Update the text with the calculated date
        dateTimeCalcViewModel.endDateLiveData.observe(viewLifecycleOwner) {
            endDateResultText.text = it
        }

        // Update the text with the calculated time
        dateTimeCalcViewModel.endTimeLiveData.observe(viewLifecycleOwner) {
            val newText = it + getString(R.string.start_time_suffix)
            endTimeResultText.text = newText
        }

        // Copy calculated date to clipboard when clicking on the text or the clipboard icon
        endDateResultText.setOnClickListener { copyToClipboard(endDateResultText.text.toString(), true) }
        binding.endDateCopyButton.setOnClickListener { copyToClipboard(endDateResultText.text.toString(), true) }

        // Copy calculated time to clipboard when clicking on the text or the clipboard icon
        endTimeResultText.setOnClickListener { copyToClipboard(endTimeResultText.text.toString(), false) }
        binding.endTimeCopyButton.setOnClickListener { copyToClipboard(endTimeResultText.text.toString(), false) }

        // Functionality for the 2 buttons at the end
        binding.continueButton.setOnClickListener { continueWithResults(dateTimeCalcViewModel) }
        binding.addToCalendarButton.setOnClickListener { startCalendarContract(dateTimeCalcViewModel) }

        // Functionality for the action buttons
        requireActivity().addMenuProvider(object : MenuProvider {
            override fun onCreateMenu(menu: Menu, menuInflater: MenuInflater) {}

            override fun onMenuItemSelected(menuItem: MenuItem): Boolean {
                return when (menuItem.itemId) {
                    R.id.action_reset -> {
                        resetFragment(dateTimeCalcViewModel)
                        true
                    }
                    else -> AppCompatActivity().onOptionsItemSelected(menuItem)
                }
            }
        }, viewLifecycleOwner, Lifecycle.State.RESUMED)

        return binding.root
    }

    private fun calculateEndDate(dateTimeCalcViewModel: DateTimeCalcViewModel) {
        dateTimeCalcViewModel.updateEndTime(
            binding.yearsEditText.text.toString().toLongOrNull() ?: 0,
            binding.monthsEditText.text.toString().toLongOrNull() ?: 0,
            binding.weeksEditText.text.toString().toLongOrNull() ?: 0,
            binding.daysEditText.text.toString().toLongOrNull() ?: 0,
            binding.hoursEditText.text.toString().toLongOrNull() ?: 0,
            binding.minsEditText.text.toString().toLongOrNull() ?: 0
        )
    }

    private fun showDatePicker(dateTimeCalcViewModel: DateTimeCalcViewModel) {
        val calendar = dateTimeCalcViewModel.getStartCalendar()

        val year = calendar.get(Calendar.YEAR)
        val month = calendar.get(Calendar.MONTH)
        val day = calendar.get(Calendar.DAY_OF_MONTH)

        DatePickerDialog(requireContext(), { _, selYear, selMonth, selDay ->
            dateTimeCalcViewModel.setStartDate(selYear, selMonth, selDay)
            calculateEndDate(dateTimeCalcViewModel)
        }, year, month, day).show()
    }

    private fun showTimePicker(dateTimeCalcViewModel: DateTimeCalcViewModel) {
        val calendar = dateTimeCalcViewModel.getStartCalendar()

        val hour = calendar.get(Calendar.HOUR_OF_DAY)
        val min = calendar.get(Calendar.MINUTE)

        TimePickerDialog(requireContext(), { _, selHour, selMin ->
            dateTimeCalcViewModel.setStartTime(selHour, selMin)
            calculateEndDate(dateTimeCalcViewModel)
        }, hour, min, true).show()
    }

    private fun setArithmeticMethod(dateTimeCalcViewModel: DateTimeCalcViewModel, addUp: Boolean) {
        dateTimeCalcViewModel.shouldAddUp(addUp)
        calculateEndDate(dateTimeCalcViewModel)
    }

    private fun adjustArithmeticButtons(addUpButton: TextView, subtractButton: TextView, addUp: Boolean) {
        addUpButton.setTypeface(null, if (addUp) Typeface.BOLD else Typeface.NORMAL)
        subtractButton.setTypeface(null, if (!addUp) Typeface.BOLD else Typeface.NORMAL)
    }

    private fun addTextChangedListener(dateTimeCalcViewModel: DateTimeCalcViewModel, editText: EditText) {
        editText.addTextChangedListener(object : TextWatcher {
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun afterTextChanged(s: Editable?) { calculateEndDate(dateTimeCalcViewModel) }
        })
    }

    private fun copyToClipboard(text: String, isDate: Boolean) {
        val label = if (isDate) "CalculatedDate" else "CalculatedTime"

        val clipboardMgr = getSystemService(requireContext(), ClipboardManager::class.java) as ClipboardManager
        val clipDate = ClipData.newPlainText(label, text)

        clipboardMgr.setPrimaryClip(clipDate)
    }

    private fun resetFragment(dateTimeCalcViewModel: DateTimeCalcViewModel) {
        binding.yearsEditText.setText("")
        binding.monthsEditText.setText("")
        binding.weeksEditText.setText("")
        binding.daysEditText.setText("")
        binding.hoursEditText.setText("")
        binding.minsEditText.setText("")

        dateTimeCalcViewModel.setInitValues()
    }

    private fun continueWithResults(dateTimeCalcViewModel: DateTimeCalcViewModel) {
        val endCalendar = dateTimeCalcViewModel.getEndCalendar()

        val year = endCalendar.get(Calendar.YEAR)
        val month = endCalendar.get(Calendar.MONTH)
        val day = endCalendar.get(Calendar.DAY_OF_MONTH)
        val hour = endCalendar.get(Calendar.HOUR_OF_DAY)
        val min = endCalendar.get(Calendar.MINUTE)

        resetFragment(dateTimeCalcViewModel)

        dateTimeCalcViewModel.setStartDate(year, month, day)
        dateTimeCalcViewModel.setStartTime(hour, min)
        calculateEndDate(dateTimeCalcViewModel)
    }

    private fun startCalendarContract(dateTimeCalcViewModel: DateTimeCalcViewModel) {
        val beginTime = dateTimeCalcViewModel.getEndCalendar().timeInMillis
        val endTime = beginTime + DateTimeCalcConstants.HOUR_IN_MS

        val intent = Intent(Intent.ACTION_INSERT)
            .setData(CalendarContract.Events.CONTENT_URI)
            .putExtra(CalendarContract.EXTRA_EVENT_BEGIN_TIME, beginTime)
            .putExtra(CalendarContract.EXTRA_EVENT_END_TIME, endTime)

        startActivity(intent)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}