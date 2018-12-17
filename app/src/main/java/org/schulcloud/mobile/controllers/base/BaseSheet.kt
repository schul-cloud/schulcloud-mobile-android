package org.schulcloud.mobile.controllers.base

import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.FragmentManager
import com.google.android.material.bottomsheet.BottomSheetDialogFragment


abstract class BaseSheet : BottomSheetDialogFragment(), ContextAware {
    override val baseActivity: BaseActivity? get() = activity as? BaseActivity
    override val currentContext: Context get() = context!!

    override suspend fun requestPermission(permission: String): Boolean {
        return baseActivity?.requestPermission(permission) ?: false
    }

    override suspend fun startActivityForResult(intent: Intent, options: Bundle?): StartActivityResult {
        return baseActivity?.startActivityForResult(intent, options) ?: StartActivityResult.error()
    }


    fun show(manager: FragmentManager?) = super.show(manager, tag)
}
