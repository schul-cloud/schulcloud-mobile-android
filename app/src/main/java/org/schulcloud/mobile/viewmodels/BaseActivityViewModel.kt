package org.schulcloud.mobile.viewmodels

import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import org.schulcloud.mobile.controllers.base.StartActivityResult
import org.schulcloud.mobile.viewmodels.base.BaseViewModel
import java.util.*
import kotlin.coroutines.experimental.Continuation


class BaseActivityViewModel : BaseViewModel() {
    private val permissionRequests: MutableList<Continuation<Boolean>>
            by lazy { LinkedList<Continuation<Boolean>>() }

    private val activityRequests: MutableList<Continuation<StartActivityResult>>
            by lazy { LinkedList<Continuation<StartActivityResult>>() }

    fun addPermissionRequest(request: Continuation<Boolean>): Int {
        permissionRequests += request
        return permissionRequests.size - 1
    }

    fun onPermissionResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray): Boolean {
        if (requestCode >= permissionRequests.size) return false

        permissionRequests[requestCode].resume(
                if (permissions.isEmpty()) false
                else grantResults[0] == PackageManager.PERMISSION_GRANTED)
        return true
    }


    fun addActivityRequest(request: Continuation<StartActivityResult>): Int {
        activityRequests += request
        return activityRequests.size - 1
    }

    fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?): Boolean {
        if (requestCode >= activityRequests.size) return false

        activityRequests[requestCode].resume(
                if (resultCode == Activity.RESULT_OK) StartActivityResult.success(data)
                else StartActivityResult.error())
        return true
    }
}
