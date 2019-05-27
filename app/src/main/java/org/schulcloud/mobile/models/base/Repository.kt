package org.schulcloud.mobile.models.base

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlin.coroutines.CoroutineContext

abstract class Repository : CoroutineScope {
    override val coroutineContext: CoroutineContext
        get() = Job() + Dispatchers.IO
}
