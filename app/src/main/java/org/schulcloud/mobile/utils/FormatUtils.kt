package org.schulcloud.mobile.utils

import android.content.Context
import org.joda.time.DateTime
import org.joda.time.Days
import org.joda.time.LocalDateTime
import org.joda.time.format.DateTimeFormat
import org.schulcloud.mobile.R
import java.text.DecimalFormat


private val UNITS = arrayOf("B", "kB", "MB", "GB", "TB", "PB", "EB", "ZB", "YB")
private const val BYTES_FACTOR = 1000.0

fun Long.formatFileSize(): String {
    if (this <= 0)
        return "0"
    val digitGroups = (Math.log10(toDouble()) / Math.log10(BYTES_FACTOR)).toInt()

    return "${DecimalFormat("#,##0.#").format(this / Math.pow(BYTES_FACTOR, digitGroups.toDouble()))} ${UNITS[digitGroups]}"
}

fun DateTime?.formatDaysLeft(context: Context): String {
    this ?: return ""
    return when (Days.daysBetween(LocalDateTime.now(), toLocalDateTime()).days) {
        -1 -> context.getString(R.string.general_date_yesterday)
        0 -> context.getString(R.string.general_date_today)
        1 -> context.getString(R.string.general_date_tomorrow)
        else -> DateTimeFormat.mediumDate().print(this)
    }
}

fun String.ellipsizedSubstring(startIndex: Int, endIndex: Int) =
        if (endIndex < length)
            substring(startIndex, endIndex) + "..."
        else this
