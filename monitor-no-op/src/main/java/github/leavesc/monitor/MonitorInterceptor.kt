package github.leavesc.monitor

import android.content.Context
import okhttp3.Interceptor
import okhttp3.Response
import java.io.IOException

/**
 * 作者：leavesC
 * 时间：2020/10/21 13:55
 * 描述：
 * GitHub：https://github.com/leavesC
 */
class MonitorInterceptor(context: Context) : Interceptor {

    @Throws(IOException::class)
    override fun intercept(chain: Interceptor.Chain): Response {
        val request = chain.request()
        return chain.proceed(request)
    }

}