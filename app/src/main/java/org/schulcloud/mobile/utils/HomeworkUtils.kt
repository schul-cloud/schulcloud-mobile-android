package org.schulcloud.mobile.utils

import android.content.Context
import androidx.core.content.ContextCompat
import org.schulcloud.mobile.R
import org.schulcloud.mobile.models.homework.Homework

fun dueLabelRequired(homework: Homework?): Boolean {
    val days = homework?.dueTimespanDays
    return days == null || days <= WEEK_IN_DAYS
}

/**
 * Returns text for the duetill label of a homework
 * depending on how many days and hours are left until its dueDate.
 *
 * When there is more than a week left, this function returns an empty String
 */
fun Context.getDueText(homework: Homework?): String {
    val days = homework?.dueTimespanDays
    return when (days) {
        null -> getString(R.string.homework_error_invalidDueDate)
        in Int.MIN_VALUE until 0 -> getString(R.string.homework_due_outdated)
        0 -> {
            val hours = homework.dueTimespanHours ?: Int.MAX_VALUE
            if (hours >= 0)
                resources.getQuantityString(R.plurals.homework_due_hours, hours, hours)
            else
                getString(R.string.homework_due_outdated)
        }
        in 1..WEEK_IN_DAYS -> resources.getQuantityString(R.plurals.homework_due_days, days, days)
        else -> ""
    }
}

fun Context.getDueColor(homework: Homework?): Int {
    return when (homework?.dueTimespanDays) {
        null -> getColorFromAttr(R.attr.colorOnBackgroundMediumEmphasis)
        in Int.MIN_VALUE..1 -> getColorFromAttr(R.attr.colorOnBackgroundPrimary)
        in 2..WEEK_IN_DAYS -> getColorFromAttr(R.attr.colorOnBackgroundMediumEmphasis)
        else -> ContextCompat.getColor(this, android.R.color.transparent)
    }
}

fun dueLabelFlagRequired(homework: Homework?): Boolean {
    return homework?.dueTimespanDays ?: Int.MAX_VALUE <= 1
}
