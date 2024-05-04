package github.leavesczy.monitor

import okhttp3.Interceptor
import okhttp3.Response

/**
 * @Author: leavesCZY
 * @Date: 2024/3/1 22:06
 * @Desc:
 */
class MonitorInterceptor : Interceptor {

    override fun intercept(chain: Interceptor.Chain): Response {
        return chain.proceed(chain.request())
    }

}