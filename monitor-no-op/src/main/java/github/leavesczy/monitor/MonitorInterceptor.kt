package github.leavesczy.monitor

import okhttp3.Interceptor
import okhttp3.Response

class MonitorInterceptor : Interceptor {

    override fun intercept(chain: Interceptor.Chain): Response {
        return chain.proceed(request = chain.request())
    }

}