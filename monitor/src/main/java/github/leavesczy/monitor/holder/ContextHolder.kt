package github.leavesczy.monitor.holder

import android.app.Application

/**
 * @Author: leavesCZY
 * @Date: 2020/11/8 15:34
 * @Desc:
 * @Github：https://github.com/leavesCZY
 */
internal object ContextHolder {

    lateinit var context: Application
        private set

    fun init(context: Application) {
        this.context = context
    }

}