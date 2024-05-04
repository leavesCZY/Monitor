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
 * @Date: 2024/3/1 23:24
 * @Desc:
 */
class MainActivity : AppCompatActivity() {

    private val okHttpClient by lazy(mode = LazyThreadSafetyMode.NONE) {
        OkHttpClient.Builder().apply {
            addInterceptor(HttpLoggingInterceptor().apply {
                level = HttpLoggingInterceptor.Level.BODY
            })
            addNetworkInterceptor(MonitorInterceptor())
        }.build()
    }

    private val apiService by lazy(mode = LazyThreadSafetyMode.NONE) {
        val retrofit = Retrofit.Builder()
            .baseUrl("https://httpbin.org")
            .addConverterFactory(GsonConverterFactory.create())
            .client(okHttpClient)
            .build()
        retrofit.create(ApiService::class.java)
    }

    private val requestNotificationPermissionLauncher =
        registerForActivityResult(ActivityResultContracts.RequestPermission()) {
            checkNotificationPermission()
        }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        findViewById<View>(R.id.btnNetworkRequest).setOnClickListener {
            showToast("已发起请求，请查看消息通知栏")
            networkRequest()
        }
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
        apiService.get().enqueue(callback)
        apiService.get(code = 404).enqueue(callback)
        apiService.post().enqueue(callback)
        apiService.post(body = Data(random = "posted")).enqueue(callback)
        apiService.put(body = Data(random = "put")).enqueue(callback)
        apiService.delete().enqueue(callback)
        apiService.deny().enqueue(callback)
        apiService.gzip().enqueue(callback)
        apiService.xml().enqueue(callback)
        apiService.utf8().enqueue(callback)
        apiService.delay(seconds = 2).enqueue(callback)
        apiService.stream(lines = 2).enqueue(callback)
    }

}