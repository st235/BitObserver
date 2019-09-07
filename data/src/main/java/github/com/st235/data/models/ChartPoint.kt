package github.com.st235.data.models

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName
import java.math.BigDecimal

data class ChartPoint(
    @SerializedName("x") @Expose val time: Long,
    @SerializedName("y") @Expose val value: Float
)
