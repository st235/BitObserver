package github.com.st235.data.net

import github.com.st235.data.models.ChartModel
import io.reactivex.Observable
import retrofit2.http.GET
import retrofit2.http.Query

internal interface ChartApi {

    @GET("charts/market-price")
    fun fetchChart(
        @Query("timespan") timeSpan: String,
        @Query("rollingAverage") rollingAverage: String,
        @Query("format") format: String,
        @Query("sampled") sampled: Boolean
        ): Observable<ChartModel>
}