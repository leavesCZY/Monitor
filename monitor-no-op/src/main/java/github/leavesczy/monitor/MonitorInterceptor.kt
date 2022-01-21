package github.leavesczy.monitor

import android.app.Application
import okhttp3.Interceptor
import okhttp3.Response

/**
 * @Author: leavesCZY
 * @Date: 2020/10/21 13:55
 * @Desc:
 * @Github：https://github.com/leavesCZY
 */
class MonitorInterceptor(context: Application) : Interceptor {

    override fun intercept(chain: Interceptor.Chain): Response {
        val request = chain.request()
        return chain.proceed(request)
    }

}