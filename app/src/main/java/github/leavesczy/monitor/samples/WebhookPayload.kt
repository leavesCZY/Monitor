package github.leavesczy.monitor.samples

import com.google.gson.annotations.SerializedName

data class WebhookPayload(
    @SerializedName("event") val event: String,
    @SerializedName("source") val source: String,
    @SerializedName("payload") val payload: Map<String, String>? = null
)
