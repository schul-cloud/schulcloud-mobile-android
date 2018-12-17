package org.schulcloud.mobile.controllers.base

import android.content.Context
import android.content.Intent
import android.os.Bundle

interface ContextAware {

    val baseActivity: BaseActivity?
    val currentContext: Context

    suspend fun requestPermission(permission: String): Boolean
    suspend fun startActivityForResult(intent: Intent, options: Bundle? = null): StartActivityResult

}

data class StartActivityResult(
    val success: Boolean,
    val data: Intent?
) {
    companion object {
        fun success(data: Intent?): StartActivityResult = StartActivityResult(true, data)
        fun error(): StartActivityResult = StartActivityResult(false, null)
    }
}
