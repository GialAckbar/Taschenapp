package com.strese.pocketapp.ui.datetimecalc

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

class DateTimeCalcViewModel : ViewModel() {
    private val dateFormat = SimpleDateFormat("dd.MM.yyyy", Locale.getDefault())
    private val timeFormat = SimpleDateFormat("HH:mm ", Locale.getDefault())

    private var _startCalendar: Calendar? = null
    private var _endCalendar: Calendar? = null

    private val _startDateLiveData = MutableLiveData<String>()
    private val _startTimeLiveData = MutableLiveData<String>()
    private val _endDateLiveData = MutableLiveData<String>()
    private val _endTimeLiveData = MutableLiveData<String>()
    private val _addUpLiveData = MutableLiveData<Boolean>()

    val startCalendar get() = _startCalendar!!
    val endCalendar get() = _endCalendar!!

    val startDateLiveData: LiveData<String> = _startDateLiveData
    val startTimeLiveData: LiveData<String> = _startTimeLiveData
    val endDateLiveData: LiveData<String> = _endDateLiveData
    val endTimeLiveData: LiveData<String> = _endTimeLiveData
    val addUpLiveData: LiveData<Boolean> = _addUpLiveData

    init { setInitValues() }

    fun setInitValues() {
        _startCalendar = Calendar.getInstance()
        _endCalendar = startCalendar.clone() as Calendar

        _startDateLiveData.value = dateFormat.format(startCalendar.time)
        _startTimeLiveData.value = timeFormat.format(startCalendar.time)
        _endDateLiveData.value = dateFormat.format(endCalendar.time)
        _endTimeLiveData.value = timeFormat.format(endCalendar.time)
        _addUpLiveData.value = true
    }

    fun setStartDate(year: Int, month: Int, day: Int) {
        startCalendar.set(Calendar.YEAR, year)
        startCalendar.set(Calendar.MONTH, month)
        startCalendar.set(Calendar.DAY_OF_MONTH, day)

        _startDateLiveData.value = dateFormat.format(startCalendar.time)
    }

    fun setStartTime(hour: Int, min: Int) {
        startCalendar.set(Calendar.HOUR_OF_DAY, hour)
        startCalendar.set(Calendar.MINUTE, min)

        _startTimeLiveData.value = timeFormat.format(startCalendar.time)
    }

    fun shouldAddUp(value: Boolean) {
        _addUpLiveData.value = value
    }

    fun updateEndTime(years: Long, months: Long, weeks: Long, days: Long, hours: Long, mins: Long) {
        val converter = if (_addUpLiveData.value != true) -1 else 1
        var timeInMillis = startCalendar.timeInMillis

        timeInMillis += years * DateTimeCalcConstants.YEAR_IN_MS * converter
        timeInMillis += months * DateTimeCalcConstants.MONTH_IN_MS * converter
        timeInMillis += weeks * DateTimeCalcConstants.WEEK_IN_MS * converter
        timeInMillis += days * DateTimeCalcConstants.DAY_IN_MS * converter
        timeInMillis += hours * DateTimeCalcConstants.HOUR_IN_MS * converter
        timeInMillis += mins * DateTimeCalcConstants.MIN_IN_MS * converter

        endCalendar.timeInMillis = timeInMillis

        if (timeInMillis <= DateTimeCalcConstants.LOWER_TIME_LIMIT) {
            _endDateLiveData.value = "\uD83D\uDC80" // Skull emoji
        } else {
            _endDateLiveData.value = dateFormat.format(endCalendar.time)
        }

        _endTimeLiveData.value = timeFormat.format(endCalendar.time)
    }
}