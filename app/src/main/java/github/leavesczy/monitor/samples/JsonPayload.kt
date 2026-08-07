package github.leavesczy.monitor.samples

import com.google.gson.annotations.SerializedName

data class JsonPayload(
    @SerializedName("scenario") val scenario: String,
    @SerializedName("timestamp") val timestamp: Long,
    @SerializedName("items") val items: List<String>? = null
)
