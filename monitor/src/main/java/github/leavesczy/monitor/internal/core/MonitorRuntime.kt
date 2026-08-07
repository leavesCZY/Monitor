package github.leavesczy.monitor.internal.core

import android.app.Application
import github.leavesczy.monitor.internal.notification.MonitorNotification

internal object MonitorRuntime {

    @Volatile
    private var isInitialized = false

    fun initialize(application: Application) {
        if (isInitialized) {
            return
        }
        synchronized(lock = MonitorRuntime::class.java) {
            if (isInitialized) {
                return
            }
            MonitorContextProvider.initialize(context = application)
            MonitorNotification.initialize(context = application)
            isInitialized = true
        }
    }

    fun ensureReady() {
        MonitorContextProvider.requireApplication()
        if (!isInitialized) {
            synchronized(lock = MonitorRuntime::class.java) {
                if (!isInitialized) {
                    MonitorNotification.initialize(context = MonitorContextProvider.requireApplication())
                    isInitialized = true
                }
            }
        }
    }

}