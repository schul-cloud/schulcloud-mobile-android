package org.schulcloud.mobile.controllers.base

import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.view.MenuItem
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.lifecycle.ViewModelProviders
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import kotlinx.coroutines.experimental.android.UI
import kotlinx.coroutines.experimental.launch
import kotlinx.coroutines.experimental.withContext
import org.schulcloud.mobile.R
import org.schulcloud.mobile.utils.asUri
import org.schulcloud.mobile.utils.openUrl
import org.schulcloud.mobile.utils.setup
import org.schulcloud.mobile.utils.shareLink
import org.schulcloud.mobile.viewmodels.BaseActivityViewModel
import kotlin.coroutines.experimental.suspendCoroutine
import kotlin.properties.Delegates


abstract class BaseActivity : AppCompatActivity(), ContextAware {
    override val baseActivity: BaseActivity? get() = this
    override val currentContext: Context get() = this

    open var url: String? = null
    var swipeRefreshLayout by Delegates.observable<SwipeRefreshLayout?>(null) { _, _, new ->
        new?.setup()
        new?.setOnRefreshListener { performRefresh() }
    }

    private val viewModel: BaseActivityViewModel by lazy {
        ViewModelProviders.of(this).get(BaseActivityViewModel::class.java)
    }

    override fun onOptionsItemSelected(item: MenuItem?): Boolean {
        when (item?.itemId) {
            R.id.base_action_share -> shareLink(url!!, supportActionBar?.title)
            R.id.base_action_refresh -> performRefresh()
            // TODO: Remove when deep linking is readded
            R.id.base_action_openInBrowser -> openUrl(url.asUri())
            else -> return super.onOptionsItemSelected(item)
        }
        return true
    }

    protected open suspend fun refresh() {}
    protected fun performRefresh() {
        swipeRefreshLayout?.isRefreshing = true
        launch {
            withContext(UI) { refresh() }
            withContext(UI) { swipeRefreshLayout?.isRefreshing = false }
        }
    }


    override suspend fun requestPermission(permission: String): Boolean = suspendCoroutine { cont ->
        if (ContextCompat.checkSelfPermission(this, permission) == PackageManager.PERMISSION_GRANTED) {
            cont.resume(true)
            return@suspendCoroutine
        }

        ActivityCompat.requestPermissions(this, arrayOf(permission), viewModel.addPermissionRequest(cont))
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        // Request not from this class
        if (!viewModel.onPermissionResult(requestCode, permissions, grantResults)) {
            super.onRequestPermissionsResult(requestCode, permissions, grantResults)
            return
        }
    }


    override suspend fun startActivityForResult(intent: Intent, options: Bundle?): StartActivityResult {
        return suspendCoroutine { cont ->
            startActivityForResult(intent, viewModel.addActivityRequest(cont), options)
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        // Request not from this class
        if (!viewModel.onActivityResult(requestCode, resultCode, data)) {
            super.onActivityResult(requestCode, resultCode, data)
            return
        }
    }
}
