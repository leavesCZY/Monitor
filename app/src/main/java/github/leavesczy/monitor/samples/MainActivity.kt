package github.leavesczy.monitor.samples

import android.Manifest
import android.os.Build
import android.os.Bundle
import android.widget.Toast
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
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
@ExperimentalMaterial3Api
class MainActivity : AppCompatActivity() {

    private val okHttpClient by lazy(mode = LazyThreadSafetyMode.NONE) {
        OkHttpClient.Builder().apply {
            addInterceptor(HttpLoggingInterceptor().apply {
                level = HttpLoggingInterceptor.Level.BODY
            })
            addNetworkInterceptor(interceptor = MonitorInterceptor())
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
        setContent {
            MonitorSampleTheme {
                Scaffold(
                    modifier = Modifier
                        .fillMaxSize(),
                    topBar = {
                        TopAppBar(
                            title = {
                                Text(
                                    modifier = Modifier,
                                    text = "Monitor"
                                )
                            }
                        )
                    }
                ) { innerPadding ->
                    Box(
                        modifier = Modifier
                            .padding(paddingValues = innerPadding)
                            .fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        Button(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 40.dp),
                            onClick = {
                                networkRequest()
                                showToast(msg = "已发起请求，请查看消息通知栏")
                            }
                        ) {
                            Text(
                                modifier = Modifier,
                                text = "Network Request"
                            )
                        }
                    }
                }
            }
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
        apiService.delay(seconds = 2).enqueue(callback)
        apiService.deny().enqueue(callback)
        apiService.status(code = 304).enqueue(callback)
        apiService.stream(lines = 2).enqueue(callback)
        apiService.streamBytes(bytes = 2).enqueue(callback)
        apiService.image(accept = "image/webp").enqueue(callback)
        apiService.gzip().enqueue(callback)
        apiService.utf8().enqueue(callback)
        apiService.xml().enqueue(callback)
    }

}