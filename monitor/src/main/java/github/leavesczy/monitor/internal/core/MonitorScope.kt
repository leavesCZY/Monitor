package github.leavesczy.monitor.internal.core

import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob

internal object MonitorScope {

    private val exceptionHandler = CoroutineExceptionHandler { _, throwable ->
        throwable.printStackTrace()
    }

    val io: CoroutineScope = CoroutineScope(
        context = SupervisorJob() + Dispatchers.IO + exceptionHandler
    )

}