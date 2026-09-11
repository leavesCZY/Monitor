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
            val trackedId = runCatching {
                trackingDeferred.await().id
            }.onFailure { throwable ->
                if (throwable !is CancellationException) {
                    throwable.printStackTrace()
                }
            }.getOrElse {
                return@launch
            }
            val recordToPersist = record.copy(id = trackedId)
            val persisted = runCatching {
                MonitorDatabase.instance.monitorDao.updateRecord(record = recordToPersist)
            }
            if (persisted.isSuccess) {
                return@launch
            }
            val failure = persisted.exceptionOrNull()
            if (failure is CancellationException) {
                return@launch
            }
            failure?.printStackTrace()
            runCatching {
                MonitorDatabase.instance.monitorDao.updateRecord(record = recordToPersist)
            }.onFailure { retryError ->
                if (retryError !is CancellationException) {
                    retryError.printStackTrace()
                }
            }
        }
    }

}
