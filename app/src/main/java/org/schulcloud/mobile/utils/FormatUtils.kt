package org.schulcloud.mobile.utils

import java.text.DecimalFormat

/**
 * Date: 7/12/2018
 */

private val UNITS = arrayOf("B", "kB", "MB", "GB", "TB", "PB", "EB", "ZB", "YB")

fun Long.formatFileSize(): String {
    if (this <= 0)
        return "0"
    val digitGroups = (Math.log10(toDouble()) / Math.log10(1024.0)).toInt()

    return "${DecimalFormat("#,##0.#").format(this / Math.pow(1024.0, digitGroups.toDouble()))} ${UNITS[digitGroups]}"
}
