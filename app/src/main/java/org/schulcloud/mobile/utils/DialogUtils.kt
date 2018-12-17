package org.schulcloud.mobile.utils

import android.app.ProgressDialog
import android.content.Context
import android.os.Looper
import android.widget.Toast
import androidx.annotation.StringRes
import kotlinx.coroutines.experimental.android.UI
import kotlinx.coroutines.experimental.launch
import kotlinx.coroutines.experimental.withContext
import org.schulcloud.mobile.R


fun Context.showGenericError(@StringRes messageRes: Int) = showGenericError(getString(messageRes))
fun Context.showGenericError(message: String) {
    return showToast(getString(R.string.dialog_error_format, message), Toast.LENGTH_SHORT)
}

fun Context.showGenericNeutral(@StringRes messageRes: Int) = showGenericNeutral(getString(messageRes))
fun Context.showGenericNeutral(message: String) {
    return showToast(message, Toast.LENGTH_SHORT)
}

fun Context.showGenericSuccess(@StringRes messageRes: Int) = showGenericSuccess(getString(messageRes))
fun Context.showGenericSuccess(message: String) {
    return showToast(message, Toast.LENGTH_SHORT)
}

private fun Context.showToast(message: String, length: Int = Toast.LENGTH_LONG) {
    fun showToastActual() {
        Toast.makeText(this, message, Toast.LENGTH_SHORT)
                .apply { show() }
    }

    if (Looper.myLooper() != Looper.getMainLooper())
        launch(UI) { showToastActual() }
    else showToastActual()
}

suspend fun <T> Context.withProgressDialog(@StringRes messageRes: Int, block: suspend () -> T): T {
    return withProgressDialog(getString(messageRes), block)
}

suspend fun <T> Context.withProgressDialog(message: String, block: suspend () -> T): T {
    val dialog = ProgressDialog(this).apply {
        setMessage(message)
        show()
    }
    val res = if (Looper.myLooper() != Looper.getMainLooper())
        withContext(UI) { block() }
    else block()
    dialog.dismiss()
    return res
}
