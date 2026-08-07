package github.leavesczy.monitor.internal.db

import github.leavesczy.monitor.internal.core.MonitorScope
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.Deferred
import kotlinx.coroutines.async
import kotlinx.coroutines.launch

internal object MonitorDatabaseWriter {

    fun beginTracking(record: MonitorRecord): Deferred<MonitorRecord> {
        return MonitorScope.io.async {
            runCatching {
                val id = MonitorDatabase.instance.monitorDao.insertRecord(record = record)
                record.copy(id = id)
            }.onFailure { throwable ->
                if (throwable !is CancellationException) {
                    throwable.printStackTrace()
                }
            }.getOrThrow()
        }
    }

    fun completeTracking(
        trackingDeferred: Deferred<MonitorRecord>,
        record: MonitorRecord
    ) {
        MonitorScope.io.launch {
            runCatching {
                val trackedRecord = trackingDeferred.await()
                MonitorDatabase.instance.monitorDao.updateRecord(
                    record = record.copy(id = trackedRecord.id)
                )
            }.onFailure { throwable ->
                if (throwable !is CancellationException) {
                    throwable.printStackTrace()
                    runCatching {
                        val trackedRecord = trackingDeferred.await()
                        MonitorDatabase.instance.monitorDao.updateRecord(
                            record = record.copy(
                                id = trackedRecord.id,
                                error = record.error ?: "Failed to persist monitor record",
                                responseTime = record.responseTime.takeIf { responseTime -> responseTime > 0L }
                                    ?: System.currentTimeMillis()
                            )
                        )
                    }.onFailure { retryError ->
                        if (retryError !is CancellationException) {
                            retryError.printStackTrace()
                        }
                    }
                }
            }
        }
    }

}