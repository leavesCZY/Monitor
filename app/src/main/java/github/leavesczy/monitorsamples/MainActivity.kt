package github.leavesczy.monitorsamples

import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import github.leavesczy.monitor.MonitorInterceptor
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.io.IOException

/**
 * @Author: leavesCZY
 * @Date: 2020/10/20 18:34
 * @Desc:
 * @Github：https://github.com/leavesCZY
 */
class MainActivity : AppCompatActivity() {

    private val okHttpClient by lazy {
        return@lazy OkHttpClient.Builder()
            .addInterceptor(HttpLoggingInterceptor().apply {
                level = HttpLoggingInterceptor.Level.BODY
            })
            .addInterceptor(FilterInterceptor())
            .addNetworkInterceptor(MonitorInterceptor(context = application))
            .build()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        findViewById<View>(R.id.btnDoApiServiceFirst).setOnClickListener {
            doApiServiceFirst()
        }
        findViewById<View>(R.id.btnDoApiServiceSecond).setOnClickListener {
            doApiServiceSecond()
        }
    }

    private class FilterInterceptor : Interceptor {

        @Throws(IOException::class)
        override fun intercept(chain: Interceptor.Chain): okhttp3.Response {
            val originalRequest = chain.request()
            val httpBuilder = originalRequest.url.newBuilder()
            httpBuilder.addEncodedQueryParameter("key", "fb0a1b0d89f3b93adca639f0a29dbf23")
            val requestBuilder = originalRequest.newBuilder()
                .url(httpBuilder.build())
            return chain.proceed(requestBuilder.build())
        }

    }

    private fun doApiServiceFirst() {
        val apiServiceFirst = SampleApiService.getApiServiceFirst(okHttpClient)
        val callback = object : Callback<Void> {
            override fun onFailure(call: Call<Void>, t: Throwable) {
                t.printStackTrace()
            }

            override fun onResponse(call: Call<Void>, response: Response<Void>) {

            }
        }
        apiServiceFirst.get().enqueue(callback)
        apiServiceFirst.post(SampleApiService.Data("posted")).enqueue(callback)
        apiServiceFirst.patch(SampleApiService.Data("patched")).enqueue(callback)
        apiServiceFirst.put(SampleApiService.Data("put")).enqueue(callback)
        apiServiceFirst.delete().enqueue(callback)
        apiServiceFirst.status(201).enqueue(callback)
        apiServiceFirst.status(401).enqueue(callback)
        apiServiceFirst.status(500).enqueue(callback)
        apiServiceFirst.delay(9).enqueue(callback)
        apiServiceFirst.delay(15).enqueue(callback)
        apiServiceFirst.redirectTo("https://http2.akamai.com").enqueue(callback)
        apiServiceFirst.redirect(3).enqueue(callback)
        apiServiceFirst.redirectRelative(2).enqueue(callback)
        apiServiceFirst.redirectAbsolute(4).enqueue(callback)
        apiServiceFirst.stream(500).enqueue(callback)
        apiServiceFirst.streamBytes(2048).enqueue(callback)
        apiServiceFirst.image("image/png").enqueue(callback)
        apiServiceFirst.gzip().enqueue(callback)
        apiServiceFirst.xml().enqueue(callback)
        apiServiceFirst.utf8().enqueue(callback)
        apiServiceFirst.deflate().enqueue(callback)
        apiServiceFirst.cookieSet("v").enqueue(callback)
        apiServiceFirst.basicAuth("me", "pass").enqueue(callback)
        apiServiceFirst.drip(512, 5, 1, 200).enqueue(callback)
        apiServiceFirst.deny().enqueue(callback)
        apiServiceFirst.cache("Mon").enqueue(callback)
        apiServiceFirst.cache(30).enqueue(callback)
    }

    private fun doApiServiceSecond() {
        val apiServiceSecond = SampleApiService.getApiServiceSecond(okHttpClient)
        val callback = object : Callback<String> {
            override fun onFailure(call: Call<String>, t: Throwable) {
                t.printStackTrace()
            }

            override fun onResponse(call: Call<String>, response: Response<String>) {

            }
        }
        apiServiceSecond.getProvince().enqueue(callback)
        apiServiceSecond.getCity("440000").enqueue(callback)
        apiServiceSecond.getCounty("440100").enqueue(callback)
    }

}