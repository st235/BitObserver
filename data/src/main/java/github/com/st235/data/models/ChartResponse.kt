package github.com.st235.data.models

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

data class ChartResponse(
    @SerializedName("status") @Expose val status: String,
    @SerializedName("name") @Expose val name: String,
    @SerializedName("unit") @Expose val unit: String,
    @SerializedName("period") @Expose val period: String,
    @SerializedName("description") @Expose val description: String,
    @SerializedName("values") @Expose val values: List<ChartResponseValue>
)
