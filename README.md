### Monitor   [![](https://jitpack.io/v/leavesC/Monitor.svg)](https://jitpack.io/#leavesC/Monitor)

Monitor 是一个适用于使用了 **OkHttp/Retrofit** 作为网络请求框架的项目，只要添加了 **MonitorInterceptor** 拦截器，Monitor 就会自动记录并保存所有的**网络请求信息**且自动弹窗展示

![](https://s1.ax1x.com/2020/10/21/BCJpz6.gif)

同时引入 debug 和 release 版本的依赖，**release 版本的 MonitorInterceptor 不会做任何操作，避免了信息泄露，也不会增加 Apk 体积大小**

```groovy
        allprojects {
            repositories {
                maven { url 'https://jitpack.io' }
            }
        }

        dependencies {
           debugImplementation 'com.github.leavesC.Monitor:monitor:1.1.3'
           releaseImplementation 'com.github.leavesC.Monitor:monitor-no-op:1.1.3'
        }
```

向 OkHttpClient 添加 MonitorInterceptor

```groovy
        val okHttpClient = OkHttpClient.Builder()
            .addInterceptor(MonitorInterceptor(Context))
            .build()
```

> Monitor 的灵感来源于另一个开源项目：[Chuck](https://github.com/jgilfelt/chuck)
