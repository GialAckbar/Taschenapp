package com.strese.pocketapp.ui.datetimecalc

class DateTimeCalcConstants {
    companion object {
        const val LOWER_TIME_LIMIT: Long = -62135686408000 // Basically 01.01.0001
        const val YEAR_IN_MS: Long = 31536000000
        const val MONTH_IN_MS: Long = 2628000000
        const val WEEK_IN_MS: Int = 604800000
        const val DAY_IN_MS: Int = 86400000
        const val HOUR_IN_MS: Int = 3600000
        const val MIN_IN_MS: Int = 60000
    }
}