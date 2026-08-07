package github.leavesczy.monitor.samples

import okhttp3.Credentials
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

internal object NetworkScenarios {

    private const val BASE_URL = "https://mockhttp.org"

    fun run(apiService: ApiService) {
        val callback = silentCallback()
        runHttpMethodScenarios(apiService = apiService, callback = callback)
        runStatusCodeScenarios(apiService = apiService, callback = callback)
        runDelayScenarios(apiService = apiService, callback = callback)
        runRedirectScenarios(apiService = apiService, callback = callback)
        runAuthScenarios(apiService = apiService, callback = callback)
        runResponseFormatScenarios(apiService = apiService, callback = callback)
        runBinaryAndStreamScenarios(apiService = apiService, callback = callback)
        runInspectionScenarios(apiService = apiService, callback = callback)
        runCacheScenarios(apiService = apiService, callback = callback)
        runFailureScenarios(apiService = apiService, callback = callback)
        runBinScenarios(apiService = apiService, callback = callback)
    }

    private fun runHttpMethodScenarios(
        apiService: ApiService,
        callback: Callback<Void>
    ) {
        val timestamp = System.currentTimeMillis()
        apiService.get().enqueue(callback)
        apiService.getWithQuery(scenario = "monitor-list", page = 1).enqueue(callback)
        apiService.post(
            body = JsonPayload(
                scenario = "create-order",
                timestamp = timestamp,
                items = listOf("item-a", "item-b")
            )
        ).enqueue(callback)
        apiService.put(
            body = JsonPayload(
                scenario = "update-profile",
                timestamp = timestamp
            )
        ).enqueue(callback)
        apiService.patch(
            body = JsonPayload(
                scenario = "patch-settings",
                timestamp = timestamp,
                items = listOf("theme=dark")
            )
        ).enqueue(callback)
        apiService.delete().enqueue(callback)
    }

    private fun runStatusCodeScenarios(
        apiService: ApiService,
        callback: Callback<Void>
    ) {
        listOf(200, 201, 400, 401, 404, 429, 500, 502, 503).forEach { code ->
            apiService.status(code = code).enqueue(callback)
        }
    }

    private fun runDelayScenarios(
        apiService: ApiService,
        callback: Callback<Void>
    ) {
        apiService.delay(seconds = 1).enqueue(callback)
        apiService.delay(seconds = 3).enqueue(callback)
    }

    private fun runRedirectScenarios(
        apiService: ApiService,
        callback: Callback<Void>
    ) {
        apiService.absoluteRedirect(count = 2).enqueue(callback)
        apiService.redirectTo(
            url = "$BASE_URL/get",
            statusCode = 302
        ).enqueue(callback)
    }

    private fun runAuthScenarios(
        apiService: ApiService,
        callback: Callback<Void>
    ) {
        apiService.bearer(authorization = "Bearer monitor-debug-token").enqueue(callback)
        apiService.basicAuth(
            user = "user",
            password = "passwd",
            authorization = Credentials.basic(username = "user", password = "passwd")
        ).enqueue(callback)
        apiService.digestAuth(user = "user", password = "passwd").enqueue(callback)
    }

    private fun runResponseFormatScenarios(
        apiService: ApiService,
        callback: Callback<Void>
    ) {
        apiService.gzip().enqueue(callback)
        apiService.deflate().enqueue(callback)
        apiService.xml().enqueue(callback)
        apiService.json().enqueue(callback)
        apiService.html().enqueue(callback)
        apiService.base64(value = "SGVsbG8gTW9uaXRvcg==").enqueue(callback)
    }

    private fun runBinaryAndStreamScenarios(
        apiService: ApiService,
        callback: Callback<Void>
    ) {
        apiService.bytes(count = 512).enqueue(callback)
        apiService.stream(lines = 5).enqueue(callback)
        apiService.streamBytes(count = 256).enqueue(callback)
        apiService.imagePng().enqueue(callback)
        apiService.imageWebp(accept = "image/webp").enqueue(callback)
        apiService.drip(duration = 2, numBytes = 128).enqueue(callback)
        apiService.range(count = 1024).enqueue(callback)
    }

    private fun runInspectionScenarios(
        apiService: ApiService,
        callback: Callback<Void>
    ) {
        apiService.headers(scenario = "monitor-sample").enqueue(callback)
        apiService.userAgent().enqueue(callback)
        apiService.ip().enqueue(callback)
        apiService.uuid().enqueue(callback)
        apiService.anything(url = "$BASE_URL/anything/monitor/demo?tag=batch-1").enqueue(callback)
        apiService.setCookie(session = "debug-001").enqueue(callback)
        apiService.responseHeaders(scenario = "custom-header").enqueue(callback)
    }

    private fun runCacheScenarios(
        apiService: ApiService,
        callback: Callback<Void>
    ) {
        apiService.cache().enqueue(callback)
        apiService.etag(etag = "monitor").enqueue(callback)
        apiService.etag(etag = "monitor", ifNoneMatch = "monitor").enqueue(callback)
    }

    private fun runFailureScenarios(
        apiService: ApiService,
        callback: Callback<Void>
    ) {
        apiService.deny().enqueue(callback)
    }

    private fun runBinScenarios(
        apiService: ApiService,
        callback: Callback<Void>
    ) {
        apiService.createBin().enqueue(object : Callback<BinResponse> {
            override fun onFailure(call: Call<BinResponse>, throwable: Throwable) {
                throwable.printStackTrace()
            }

            override fun onResponse(call: Call<BinResponse>, response: Response<BinResponse>) {
                val binId = response.body()?.id ?: return
                apiService.sendToBin(
                    url = "$BASE_URL/b/$binId/stripe/events",
                    body = WebhookPayload(
                        event = "payment_intent.succeeded",
                        source = "stripe",
                        payload = mapOf(
                            "amount" to "2000",
                            "currency" to "usd"
                        )
                    )
                ).enqueue(callback)
                apiService.sendToBin(
                    url = "$BASE_URL/b/$binId/github/hook",
                    body = WebhookPayload(
                        event = "pull_request.opened",
                        source = "github",
                        payload = mapOf(
                            "number" to "42",
                            "repo" to "Monitor"
                        )
                    )
                ).enqueue(callback)
                apiService.listBinRequests(binId = binId).enqueue(callback)
            }
        })
    }

    private fun silentCallback(): Callback<Void> {
        return object : Callback<Void> {
            override fun onFailure(call: Call<Void>, throwable: Throwable) {
                throwable.printStackTrace()
            }

            override fun onResponse(call: Call<Void>, response: Response<Void>) {
            }
        }
    }

}