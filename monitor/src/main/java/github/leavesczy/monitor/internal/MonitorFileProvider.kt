package github.leavesczy.monitor.internal

import android.app.Application
import androidx.core.content.FileProvider

/**
 * @Author: leavesCZY
 * @Date: 2023/10/30 12:14
 * @Desc:
 */
internal class MonitorFileProvider : FileProvider() {

    override fun onCreate(): Boolean {
        ContextProvider.inject(context = context as Application)
        return super.onCreate()
    }

}