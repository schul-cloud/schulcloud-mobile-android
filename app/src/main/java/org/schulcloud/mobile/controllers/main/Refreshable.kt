package org.schulcloud.mobile.controllers.main

import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
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
        private set


    override fun performRefresh() {
        isRefreshing = true
        GlobalScope.launch(Dispatchers.Default) {
            withContext(Dispatchers.Main) { refresh.invoke() }
            withContext(Dispatchers.Main) { isRefreshing = false }
        }
    }
}
