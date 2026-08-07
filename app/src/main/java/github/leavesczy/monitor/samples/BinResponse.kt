package github.leavesczy.monitor.samples

import com.google.gson.annotations.SerializedName

data class BinResponse(
    @SerializedName("id") val id: String,
    @SerializedName("url") val url: String
)
