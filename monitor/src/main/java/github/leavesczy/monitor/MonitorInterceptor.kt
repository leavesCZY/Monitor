package github.leavesczy.monitor

import github.leavesczy.monitor.internal.capture.MonitorHttpBodyCapture
import github.leavesczy.monitor.internal.capture.MonitorRecordFactory
import github.leavesczy.monitor.internal.core.MonitorRuntime
import github.leavesczy.monitor.internal.db.MonitorDatabaseWriter
import okhttp3.Interceptor
import okhttp3.Request
import okhttp3.Response

class MonitorInterceptor : Interceptor {

    override fun intercept(chain: Interceptor.Chain): Response {
        val originalRequest = chain.request()
        return try {
            MonitorRuntime.ensureReady()
            interceptWithMonitoring(chain = chain, originalRequest = originalRequest)
        } catch (monitoringError: Throwable) {
            monitoringError.printStackTrace()
            chain.proceed(request = originalRequest)
        }
    }

    private fun interceptWithMonitoring(
        chain: Interceptor.Chain,
        originalRequest: Request
    ): Response {
        val requestCapture = MonitorHttpBodyCapture.captureRequest(request = originalRequest)
        val pendingRecord = MonitorRecordFactory.createPending(
            request = requestCapture.request,
            requestCapture = requestCapture
        )
        val trackingDeferred = MonitorDatabaseWriter.beginTracking(record = pendingRecord)
        return try {
            val response = chain.proceed(request = requestCapture.request)
            MonitorDatabaseWriter.completeTracking(
                trackingDeferred = trackingDeferred,
                record = MonitorRecordFactory.complete(
                    record = pendingRecord,
                    response = response,
                    responseCapture = MonitorHttpBodyCapture.captureResponse(response = response)
                )
            )
            response
        } catch (error: Throwable) {
            MonitorDatabaseWriter.completeTracking(
                trackingDeferred = trackingDeferred,
                record = MonitorRecordFactory.fail(
                    record = pendingRecord,
                    error = error
                )
            )
            throw error
        }
    }

}