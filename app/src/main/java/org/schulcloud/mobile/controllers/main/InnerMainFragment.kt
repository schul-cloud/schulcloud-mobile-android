package org.schulcloud.mobile.controllers.main

import android.annotation.SuppressLint
import android.os.Bundle
import androidx.lifecycle.ViewModel
import kotlinx.coroutines.experimental.android.UI
import kotlinx.coroutines.experimental.launch
import kotlinx.coroutines.experimental.withContext
import org.schulcloud.mobile.utils.liveDataOf


@SuppressLint("ValidFragment")
abstract class InnerMainFragment<F : InnerMainFragment<F, P, VM>, P : MainFragment<P, VM>, VM : ViewModel> :
        MainFragment<F, VM>() {
    override val isInnerFragment = true

    @Suppress("UNCHECKED_CAST")
    protected val parent: P
        get() = parentFragment as P

    override fun provideSelfConfig() = liveDataOf(MainFragmentConfig())

    override fun onCreate(savedInstanceState: Bundle?) {
        viewModel = parent.viewModel
        super.onCreate(savedInstanceState)
    }


    override fun performRefresh() = performRefreshWithParent(false)
    fun performRefreshWithParent(fromParent: Boolean) {
        refreshableImpl.isRefreshing = true
        launch {
            withContext(UI) {
                refresh()
                if (!fromParent)
                    parent.performRefreshWithChild(true)
            }
            withContext(UI) { refreshableImpl.isRefreshing = false }
        }
    }
}
