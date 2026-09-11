package github.leavesczy.monitor.internal.core

import android.app.Application

internal object MonitorContextProvider {

    private lateinit var application: Application

    val isInitialized: Boolean
        get() = ::application.isInitialized

    fun initialize(context: Application) {
        application = context
    }

    fun requireApplication(): Application {
        if (!isInitialized) {
            error(
                "Monitor is not initialized. Call MonitorRuntime.initialize(application) in Application.onCreate(), " +
                        "or ensure MonitorFileProvider is registered in AndroidManifest."
            )
        }
        return application
    }

}