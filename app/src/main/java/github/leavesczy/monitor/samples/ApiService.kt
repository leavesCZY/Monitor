package github.leavesczy.monitor.samples

import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.PATCH
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.Path
import retrofit2.http.Query
import retrofit2.http.Url

interface ApiService {

    @GET("/get")
    fun get(): Call<Void>

    @GET("/get")
    fun getWithQuery(
        @Query("scenario") scenario: String,
        @Query("page") page: Int
    ): Call<Void>

    @POST("/post")
    fun post(@Body body: JsonPayload): Call<Void>

    @PUT("/put")
    fun put(@Body body: JsonPayload): Call<Void>

    @PATCH("/patch")
    fun patch(@Body body: JsonPayload): Call<Void>

    @DELETE("/delete")
    fun delete(): Call<Void>

    @GET("/status/{code}")
    fun status(@Path("code") code: Int): Call<Void>

    @GET("/delay/{seconds}")
    fun delay(@Path("seconds") seconds: Int): Call<Void>

    @GET("/absolute-redirect/{count}")
    fun absoluteRedirect(@Path("count") count: Int): Call<Void>

    @GET("/redirect-to")
    fun redirectTo(
        @Query("url") url: String,
        @Query("status_code") statusCode: Int
    ): Call<Void>

    @GET("/bearer")
    fun bearer(@Header("Authorization") authorization: String): Call<Void>

    @GET("/basic-auth/{user}/{passwd}")
    fun basicAuth(
        @Path("user") user: String,
        @Path("passwd") password: String,
        @Header("Authorization") authorization: String
    ): Call<Void>

    @GET("/digest-auth/auth/{user}/{passwd}/MD5")
    fun digestAuth(
        @Path("user") user: String,
        @Path("passwd") password: String
    ): Call<Void>

    @GET("/gzip")
    fun gzip(): Call<Void>

    @GET("/deflate")
    fun deflate(): Call<Void>

    @GET("/xml")
    fun xml(): Call<Void>

    @GET("/json")
    fun json(): Call<Void>

    @GET("/html")
    fun html(): Call<Void>

    @GET("/bytes/{count}")
    fun bytes(@Path("count") count: Int): Call<Void>

    @GET("/stream/{lines}")
    fun stream(@Path("lines") lines: Int): Call<Void>

    @GET("/stream-bytes/{count}")
    fun streamBytes(@Path("count") count: Int): Call<Void>

    @GET("/image/png")
    fun imagePng(): Call<Void>

    @GET("/image/webp")
    fun imageWebp(@Header("Accept") accept: String): Call<Void>

    @GET("/drip")
    fun drip(
        @Query("duration") duration: Int,
        @Query("numbytes") numBytes: Int
    ): Call<Void>

    @GET("/range/{count}")
    fun range(@Path("count") count: Int): Call<Void>

    @GET("/headers")
    fun headers(@Header("X-Monitor-Scenario") scenario: String): Call<Void>

    @GET("/user-agent")
    fun userAgent(): Call<Void>

    @GET("/ip")
    fun ip(): Call<Void>

    @GET("/uuid")
    fun uuid(): Call<Void>

    @GET
    fun anything(
        @Url url: String
    ): Call<Void>

    @GET("/cookies/set")
    fun setCookie(@Query("monitor_session") session: String): Call<Void>

    @GET("/response-headers")
    fun responseHeaders(@Query("X-Scenario") scenario: String): Call<Void>

    @GET("/cache")
    fun cache(): Call<Void>

    @GET("/etag/{etag}")
    fun etag(
        @Path("etag") etag: String,
        @Header("If-None-Match") ifNoneMatch: String? = null
    ): Call<Void>

    @GET("/deny")
    fun deny(): Call<Void>

    @GET("/base64/{value}")
    fun base64(@Path(value = "value", encoded = true) value: String): Call<Void>

    @POST("/bins")
    fun createBin(): Call<BinResponse>

    @POST
    fun sendToBin(
        @Url url: String,
        @Body body: WebhookPayload
    ): Call<Void>

    @GET("/bins/{binId}/requests")
    fun listBinRequests(@Path("binId") binId: String): Call<Void>

}