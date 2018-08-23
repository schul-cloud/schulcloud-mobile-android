@file:Suppress("TooManyFunctions")

package org.schulcloud.mobile.utils

import android.content.Context
import android.content.res.ColorStateList
import android.content.res.Resources
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.text.format.DateUtils
import android.view.View
import android.widget.TextView
import androidx.annotation.ColorInt
import androidx.core.content.ContextCompat
import androidx.databinding.BindingAdapter
import androidx.databinding.BindingConversion
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import org.schulcloud.mobile.R

private const val COLOR_BLACK_STRING = "#00000000"
private const val LIGHTNESS_PART_RED = 0.2126
private const val LIGHTNESS_PART_GREEN = 0.7152
private const val LIGHTNESS_PART_BLUE = 0.0722
private const val LIGHTNESS_THRESHOLD = 127 // values in 0..255

private fun safeColor(color: String?): String {
    return if (color == null || color.isBlank())
        COLOR_BLACK_STRING
    else color
}

@BindingConversion
@ColorInt
fun String?.toColor() = Color.parseColor(safeColor(this))

@BindingConversion
fun String?.toColorDrawable() = ColorDrawable(Color.parseColor(safeColor(this)))

@BindingConversion
fun String?.toColorStateList() = ColorStateList.valueOf(Color.parseColor(safeColor(this)))


@BindingAdapter("displayDate")
fun showDate(view: TextView, date: String?) {
    view.text = date?.parseDate()?.let {
        DateUtils.formatDateTime(view.context, it.timeInMillis, DateUtils.FORMAT_SHOW_DATE)
    } ?: ""
}

fun Int.dpToPx(): Int = Math.round(this * Resources.getSystem().displayMetrics.density)

@BindingConversion
fun Boolean.asVisibility(): Int {
    if (this)
        return View.VISIBLE
    return View.GONE
}

fun String?.toVisible(): Int = this.isNullOrEmpty().not().asVisibility()

@Suppress("SpreadOperator")
fun SwipeRefreshLayout.setup() {
    setColorSchemeColors(*context.getColorArray(R.array.brand_swipeRefreshColors))
}

var View.visibilityBool: Boolean
    get() = visibility == View.VISIBLE
    set(value) {
        visibility = if (value) View.VISIBLE else View.GONE
    }

val Int.isLightColor: Boolean
    get() {
        // Formula from [Color#luminance()]
        return LIGHTNESS_PART_RED * Color.red(this) +
                LIGHTNESS_PART_GREEN * Color.green(this) +
                LIGHTNESS_PART_BLUE * Color.blue(this) > LIGHTNESS_THRESHOLD
    }

fun Context.getTextColorForBackground(color: Int): Int {
    return ContextCompat.getColor(this, if (color.isLightColor) R.color.material_text_primary_dark
    else R.color.material_text_primary_light)
}
