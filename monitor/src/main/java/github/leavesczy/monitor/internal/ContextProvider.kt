package github.leavesczy.monitor.internal

import android.app.Application

/**
 * @Author: leavesCZY
 * @Date: 2020/11/8 15:34
 * @Desc:
 */
internal object ContextProvider {

    lateinit var context: Application
        private set

    fun inject(context: Application) {
        ContextProvider.context = context
    }

}