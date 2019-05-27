package org.schulcloud.mobile.jobs.base

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import java.util.*

open class RequestJobCallback {
    enum class ErrorCode {
        ERROR, CANCEL, NO_NETWORK, NO_AUTH, MAINTENANCE, API_VERSION_EXPIRED, TIMEOUT
    }

    protected open fun onSuccess() {}

    protected open fun onError(code: ErrorCode) {}

    protected open fun onDeprecated(deprecationDate: Date) {}

    fun success() {
        // EventBus.getDefault().postSticky(NetworkStateEvent(true))
        GlobalScope.launch(Dispatchers.Main) { onSuccess() }
    }

    fun error(errorCode: ErrorCode) {
        if (errorCode == ErrorCode.NO_NETWORK) {
            // EventBus.getDefault().postSticky(NetworkStateEvent(false))
        }
        GlobalScope.launch(Dispatchers.Main) { onError(errorCode) }
    }

    fun deprecated(deprecationDate: Date) {
        GlobalScope.launch(Dispatchers.Main) { onDeprecated(deprecationDate) }
    }
}
