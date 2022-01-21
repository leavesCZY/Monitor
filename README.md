# Monitor   [![](https://jitpack.io/v/leavesCZY/Monitor.svg)](https://jitpack.io/#leavesCZY/Monitor)

Monitor 用于在 Android 平台为 OkHttp / Retrofit 提供可视化抓包界面，只要添加了 MonitorInterceptor 拦截器，Monitor 就会自动记录并持久化缓存所有的网络请求信息

![](https://s1.ax1x.com/2020/10/21/BCJpz6.gif)

同时引入 debug 和 release 版本的依赖，release 版本的 MonitorInterceptor 不会做任何操作，避免了信息泄露，也不会增加 Apk 体积大小

```groovy
allprojects {
    repositories {
        maven { url 'https://jitpack.io' }
    }
}

dependencies {
    debugImplementation 'com.github.leavesCZY.Monitor:monitor:1.1.5'
    releaseImplementation 'com.github.leavesCZY.Monitor:monitor-no-op:1.1.5'
}
```

向 OkHttpClient 添加 MonitorInterceptor 即可

```groovy
val okHttpClient = OkHttpClient.Builder()
    .addInterceptor(MonitorInterceptor(context = application))
    .build()
```

> Monitor 的灵感来源于另一个开源项目：[Chuck](https://github.com/jgilfelt/chuck)
