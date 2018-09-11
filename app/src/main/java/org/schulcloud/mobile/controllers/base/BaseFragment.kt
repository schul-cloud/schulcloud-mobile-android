package org.schulcloud.mobile.controllers.base

import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment


abstract class BaseFragment : Fragment() {
    val baseActivity: BaseActivity? get() = activity as? BaseActivity

    suspend fun requestPermission(permission: String): Boolean = baseActivity?.requestPermission(permission) ?: false
    suspend fun startActivityForResult(intent: Intent, options: Bundle? = null): Intent? {
        return baseActivity?.startActivityForResult(intent, options)
    }
}
