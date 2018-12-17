@file:Suppress("TooManyFunctions")

package org.schulcloud.mobile.utils

import android.content.Context
import android.content.Intent
import android.content.res.TypedArray
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.drawable.BitmapDrawable
import android.graphics.drawable.Drawable
import android.os.Bundle
import androidx.annotation.ArrayRes
import androidx.annotation.ColorInt
import androidx.annotation.Dimension
import androidx.annotation.StyleableRes
import androidx.core.content.res.ResourcesCompat
import androidx.core.graphics.drawable.DrawableCompat
import androidx.core.text.TextUtilsCompat
import androidx.core.view.ViewCompat
import org.schulcloud.mobile.R
import java.util.*


fun Map<String, String>.asBundle(): Bundle {
    return Bundle().apply {
        for (entry in entries)
            putString(entry.key, entry.value)
    }
}

fun Drawable.asBitmap(): Bitmap {
    if (this is BitmapDrawable)
        return bitmap

    val bitmap = Bitmap.createBitmap(intrinsicWidth, intrinsicHeight, Bitmap.Config.ARGB_8888)
    val canvas = Canvas(bitmap)
    setBounds(0, 0, canvas.width, canvas.height)
    draw(canvas)
    return bitmap
}

fun Context.shareLink(url: String, titleContent: CharSequence? = null) {
    val intent = Intent(Intent.ACTION_SEND).apply {
        type = MIME_TEXT_PLAIN
        putExtra(Intent.EXTRA_SUBJECT,
                if (titleContent != null)
                    getString(R.string.share_subject_format,
                            getString(R.string.brand_name),
                            titleContent)
                else getString(R.string.brand_name))
        putExtra(Intent.EXTRA_TEXT, url)
    }
    startActivity(Intent.createChooser(intent, getString(R.string.share_title)))
}

fun isLtr() = TextUtilsCompat.getLayoutDirectionFromLocale(Locale.getDefault()) == ViewCompat.LAYOUT_DIRECTION_LTR

fun Context.getColorArray(@ArrayRes id: Int, @ColorInt fallback: Int? = null): IntArray {
    val fallbackColor = fallback ?: ResourcesCompat.getColor(resources, R.color.brand_accent, theme)
    val ta = resources.obtainTypedArray(id)
    val colors = IntArray(ta.length()) { i -> ta.getColor(i, fallbackColor) }
    ta.recycle()
    return colors
}


@ColorInt
fun TypedArray.getColorOrNull(@StyleableRes index: Int): Int? {
    return if (hasValue(index)) getColor(index, 0) else null
}

@Dimension
fun TypedArray.getDimensionOrNull(@StyleableRes index: Int): Float? {
    return if (hasValue(index)) getDimension(index, 0f) else null
}


fun Drawable.setTintCompat(@ColorInt tint: Int) {
    DrawableCompat.setTint(this, tint)
}
