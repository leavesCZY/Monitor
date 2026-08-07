package github.leavesczy.monitor.internal.core

import android.app.Application
import androidx.core.content.FileProvider

internal class MonitorFileProvider : FileProvider() {

    override fun onCreate(): Boolean {
        MonitorRuntime.initialize(application = context as Application)
        return super.onCreate()
    }

}