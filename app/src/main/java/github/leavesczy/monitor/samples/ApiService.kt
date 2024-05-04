package github.leavesczy.monitor.samples

import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.Path

/**
 * @Author: leavesCZY
 * @Date: 2024/3/1 23:25
 * @Desc:
 */
interface ApiService {

    @GET("/get/{code}")
    fun get(@Path("code") code: Int): Call<Void>

    @GET("/get")
    fun get(): Call<Void>

    @POST("/post")
    fun post(@Body body: Data): Call<Void>

    @POST("/post")
    fun post(): Call<Void>

    @PUT("/put")
    fun put(@Body body: Data): Call<Void>

    @DELETE("/delete")
    fun delete(): Call<Void>

    @GET("/delay/{seconds}")
    fun delay(@Path("seconds") seconds: Int): Call<Void>

    @GET("/deny")
    fun deny(): Call<Void>

    @GET("/status/{code}")
    fun status(@Path("code") code: Int): Call<Void>

    @GET("/stream/{lines}")
    fun stream(@Path("lines") lines: Int): Call<Void>

    @GET("/stream-bytes/{bytes}")
    fun streamBytes(@Path("bytes") bytes: Int): Call<Void>

    @GET("/image")
    fun image(@Header("Accept") accept: String): Call<Void>

    @GET("/gzip")
    fun gzip(): Call<Void>

    @GET("/xml")
    fun xml(): Call<Void>

    @GET("/encoding/utf8")
    fun utf8(): Call<Void>

}