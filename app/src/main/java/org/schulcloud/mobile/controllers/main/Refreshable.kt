package org.schulcloud.mobile.controllers.main

import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import kotlinx.coroutines.experimental.android.UI
import kotlinx.coroutines.experimental.launch
import kotlinx.coroutines.experimental.withContext
import org.schulcloud.mobile.utils.setup
import kotlin.properties.Delegates


interface Refreshable {
    var swipeRefreshLayout: SwipeRefreshLayout?
    val isRefreshing: Boolean

    fun performRefresh()
}

class RefreshableImpl : Refreshable {
    lateinit var refresh: suspend () -> Unit

    override var swipeRefreshLayout by Delegates.observable<SwipeRefreshLayout?>(null) { _, _, new ->
        new?.setup()
        new?.setOnRefreshListener { performRefresh() }
        new?.isRefreshing = isRefreshing
    }
    override var isRefreshing: Boolean by Delegates.observable(false) { _, _, new ->
        swipeRefreshLayout?.isRefreshing = new
    }


    override fun performRefresh() {
        isRefreshing = true
        launch {
            withContext(UI) { refresh.invoke() }
            withContext(UI) { isRefreshing = false }
        }
    }
}
