package github.leavesczy.monitor.internal.db

import github.leavesczy.monitor.internal.core.MonitorScope
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.Deferred
import kotlinx.coroutines.async
import kotlinx.coroutines.launch
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock

internal object MonitorDatabaseWriter {

    private val writeMutex = Mutex()

    fun beginTracking(
        record: MonitorRecord,
        payload: MonitorPayload
    ): Deferred<Long> {
        return MonitorScope.io.async {
            writeMutex.withLock {
                runCatching {
                    MonitorDatabase.instance.monitorDao.insertPending(
                        record = record,
                        payload = payload
                    )
                }.onFailure { throwable ->
                    if (throwable !is CancellationException) {
                        throwable.printStackTrace()
                    }
                }.getOrThrow()
            }
        }
    }

    fun completeTracking(
        trackingDeferred: Deferred<Long>,
        record: MonitorRecord,
        payload: MonitorPayload
    ) {
        MonitorScope.io.launch {
            val trackedId = runCatching {
                trackingDeferred.await()
            }.onFailure { throwable ->
                if (throwable !is CancellationException) {
                    throwable.printStackTrace()
                }
            }.getOrElse {
                return@launch
            }
            writeMutex.withLock {
                runCatching {
                    MonitorDatabase.instance.monitorDao.completeRecord(
                        record = record.copy(id = trackedId),
                        payload = payload.copy(recordId = trackedId)
                    )
                }.onFailure { throwable ->
                    if (throwable !is CancellationException) {
                        throwable.printStackTrace()
                    }
                }
            }
        }
    }

}
