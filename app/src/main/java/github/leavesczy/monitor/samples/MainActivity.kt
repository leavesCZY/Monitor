package github.leavesczy.monitor.samples

import android.Manifest
import android.os.Build
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.NotificationManagerCompat
import github.leavesczy.monitor.MonitorInterceptor
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

/**
 * @Author: leavesCZY
 * @Date: 2020/10/20 18:34
 * @Desc:
 * @Github：https://github.com/leavesCZY
 */
class MainActivity : AppCompatActivity() {

    private val okHttpClient by lazy(mode = LazyThreadSafetyMode.NONE) {
        OkHttpClient.Builder()
            .addInterceptor(HttpLoggingInterceptor().apply {
                level = HttpLoggingInterceptor.Level.BODY
            })
            .addInterceptor(FilterInterceptor())
            .addNetworkInterceptor(MonitorInterceptor(context = application))
            .build()
    }

    private val apiServiceFirst by lazy(mode = LazyThreadSafetyMode.NONE) {
        val retrofit = Retrofit.Builder()
            .baseUrl("https://httpbin.org")
            .addConverterFactory(GsonConverterFactory.create())
            .client(okHttpClient)
            .build()
        retrofit.create(ApiServiceFirst::class.java)
    }

    private val apiServiceSecond by lazy(mode = LazyThreadSafetyMode.NONE) {
        val retrofit = Retrofit.Builder()
            .baseUrl("https://restapi.amap.com/v3/")
            .addConverterFactory(GsonConverterFactory.create())
            .client(okHttpClient)
            .build()
        retrofit.create(ApiServiceSecond::class.java)
    }

    private val requestNotificationPermissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) {
        checkNotificationPermission()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        initView()
        requestNotificationPermission()
    }

    private fun initView() {
        findViewById<View>(R.id.btnDoApiServiceFirst).setOnClickListener {
            showToast("已发起请求，请查看消息通知栏")
            doApiServiceFirst()
        }
        findViewById<View>(R.id.btnDoApiServiceSecond).setOnClickListener {
            showToast("已发起请求，请查看消息通知栏")
            doApiServiceSecond()
        }
    }

    private fun requestNotificationPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            requestNotificationPermissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
        } else {
            checkNotificationPermission()
        }
    }

    private fun checkNotificationPermission() {
        if (!NotificationManagerCompat.from(this).areNotificationsEnabled()) {
            showToast("请开启消息通知权限，以便查看网络请求")
        }
    }

    private fun showToast(msg: String) {
        Toast.makeText(this, msg, Toast.LENGTH_SHORT).show()
    }

    private fun doApiServiceFirst() {
        val callback = object : Callback<Void> {
            override fun onFailure(call: Call<Void>, t: Throwable) {
                t.printStackTrace()
            }

            override fun onResponse(call: Call<Void>, response: Response<Void>) {

            }
        }
        apiServiceFirst.get().enqueue(callback)
        apiServiceFirst.post(Data("posted")).enqueue(callback)
        apiServiceFirst.patch(Data("patched")).enqueue(callback)
        apiServiceFirst.put(Data("put")).enqueue(callback)
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