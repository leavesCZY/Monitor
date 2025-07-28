# Monitor

一个适用于 OkHttp / Retrofit 的可视化抓包工具

只需为 OkHttpClient 添加 MonitorInterceptor，就会自动记录并缓存所有的网络请求信息，并提供可视化界面进行查看

```kotlin
val okHttpClient = OkHttpClient
    .Builder()
    .addNetworkInterceptor(interceptor = MonitorInterceptor())
    .build()
```

同时引入 debug 和 release 版本的依赖库

- debug 依赖用于日常的开发阶段
- release 依赖用于最终的上线阶段，此模式下的 MonitorInterceptor 不包含任何依赖，也不会执行任何操作

```kotlin
dependencyResolutionManagement {
    repositories {
        google()
        mavenCentral()
    }
}

dependencies {
    val latestVersion = "x.x.x"
    debugImplementation("io.github.leavesczy:monitor:${latestVersion}")
    releaseImplementation("io.github.leavesczy:monitor-no-op:${latestVersion}")
}
```

![](https://github.com/user-attachments/assets/87308488-fe02-47bd-8f81-01470ddb9de8)
