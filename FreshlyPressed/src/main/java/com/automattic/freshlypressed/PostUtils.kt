package com.automattic.freshlypressed

import java.text.DateFormat
import java.util.Date
import java.util.Locale

object PostUtils {
    fun printDate(date: Date): String {
        val dateFormat = DateFormat.getDateInstance(DateFormat.MEDIUM, Locale.US)
        return dateFormat.format(date)
    }
}
