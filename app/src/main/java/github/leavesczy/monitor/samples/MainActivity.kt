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
import java.util.concurrent.TimeUnit

/**
 * @Author: leavesCZY
 * @Date: 2020/10/20 18:34
 * @Desc:
 * @Github：https://github.com/leavesCZY
 */
class MainActivity : AppCompatActivity() {

    private val okHttpClient by lazy(mode = LazyThreadSafetyMode.NONE) {
        OkHttpClient.Builder().apply {
            callTimeout(30, TimeUnit.SECONDS)
            connectTimeout(30, TimeUnit.SECONDS)
            readTimeout(30, TimeUnit.SECONDS)
            writeTimeout(30, TimeUnit.SECONDS)
            retryOnConnectionFailure(true)
            addInterceptor(HttpLoggingInterceptor().apply {
                level = HttpLoggingInterceptor.Level.BODY
            })
            addInterceptor(FilterInterceptor())
            addNetworkInterceptor(MonitorInterceptor(context = application))
        }.build()
    }

    private val apiServiceMock by lazy(mode = LazyThreadSafetyMode.NONE) {
        val retrofit = Retrofit.Builder()
            .baseUrl("https://httpbin.org")
            .addConverterFactory(GsonConverterFactory.create())
            .client(okHttpClient)
            .build()
        retrofit.create(ApiServiceMock::class.java)
    }

    private val apiServiceWeather by lazy(mode = LazyThreadSafetyMode.NONE) {
        val retrofit = Retrofit.Builder()
            .baseUrl("https://restapi.amap.com/v3/")
            .addConverterFactory(GsonConverterFactory.create())
            .client(okHttpClient)
            .build()
        retrofit.create(ApiServiceWeather::class.java)
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
        findViewById<View>(R.id.btnNetworkRequest).setOnClickListener {
            showToast("已发起请求，请查看消息通知栏")
            networkRequest()
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

    private fun networkRequest() {
        val callback = object : Callback<Void> {
            override fun onFailure(call: Call<Void>, throwable: Throwable) {
                throwable.printStackTrace()
            }

            override fun onResponse(call: Call<Void>, response: Response<Void>) {

            }
        }
        apiServiceMock.get().enqueue(callback)
        apiServiceMock.post(body = Data(thing = "posted")).enqueue(callback)
        apiServiceMock.patch(body = Data(thing = "patched")).enqueue(callback)
        apiServiceMock.put(body = Data(thing = "put")).enqueue(callback)
        apiServiceMock.delete().enqueue(callback)
        apiServiceMock.status(code = 200).enqueue(callback)
        apiServiceMock.status(code = 201).enqueue(callback)
        apiServiceMock.delay(seconds = 1).enqueue(callback)
        apiServiceMock.delay(seconds = 2).enqueue(callback)
        apiServiceMock.stream(lines = 200).enqueue(callback)
        apiServiceMock.streamBytes(bytes = 2048).enqueue(callback)
        apiServiceMock.image("image/png").enqueue(callback)
        apiServiceMock.gzip().enqueue(callback)
        apiServiceMock.xml().enqueue(callback)
        apiServiceMock.utf8().enqueue(callback)
        apiServiceMock.deflate().enqueue(callback)
        apiServiceWeather.getProvince().enqueue(callback)
        apiServiceWeather.getCity("440000").enqueue(callback)
        apiServiceWeather.getCounty("440100").enqueue(callback)
    }

}