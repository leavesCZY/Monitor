package github.leavesczy.monitor

import github.leavesczy.monitor.internal.capture.MonitorEntry
import github.leavesczy.monitor.internal.capture.MonitorHttpBodyCapture
import github.leavesczy.monitor.internal.capture.MonitorRecordFactory
import github.leavesczy.monitor.internal.core.MonitorRuntime
import github.leavesczy.monitor.internal.db.MonitorDatabaseWriter
import kotlinx.coroutines.Deferred
import okhttp3.Interceptor
import okhttp3.Request
import okhttp3.Response

class MonitorInterceptor : Interceptor {

    override fun intercept(chain: Interceptor.Chain): Response {
        val originalRequest = chain.request()
        val tracking = try {
            prepareTracking(request = originalRequest)
        } catch (monitoringError: Throwable) {
            monitoringError.printStackTrace()
            null
        }
        if (tracking == null) {
            return chain.proceed(request = originalRequest)
        }
        return try {
            val response = chain.proceed(request = tracking.request)
            finishTracking(tracking = tracking) {
                MonitorRecordFactory.complete(
                    entry = tracking.pendingEntry,
                    response = response,
                    responseCapture = MonitorHttpBodyCapture.captureResponse(response = response)
                )
            }
            response
        } catch (error: Throwable) {
            finishTracking(tracking = tracking) {
                MonitorRecordFactory.fail(
                    entry = tracking.pendingEntry,
                    error = error
                )
            }
            throw error
        }
    }

    private fun prepareTracking(request: Request): Tracking {
        MonitorRuntime.ensureReady()
        val requestCapture = MonitorHttpBodyCapture.captureRequest(request = request)
        val pendingEntry = MonitorRecordFactory.createPending(
            request = requestCapture.request,
            requestCapture = requestCapture
        )
        return Tracking(
            request = requestCapture.request,
            pendingEntry = pendingEntry,
            deferred = MonitorDatabaseWriter.beginTracking(
                record = pendingEntry.record,
                payload = pendingEntry.payload
            )
        )
    }

    private fun finishTracking(tracking: Tracking, entryProvider: () -> MonitorEntry) {
        runCatching {
            val entry = entryProvider()
            MonitorDatabaseWriter.completeTracking(
                trackingDeferred = tracking.deferred,
                record = entry.record,
                payload = entry.payload
            )
        }.onFailure { throwable ->
            throwable.printStackTrace()
        }
    }

    private class Tracking(
        val request: Request,
        val pendingEntry: MonitorEntry,
        val deferred: Deferred<Long>
    )

}
