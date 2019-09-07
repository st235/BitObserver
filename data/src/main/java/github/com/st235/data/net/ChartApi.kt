package github.com.st235.data.net

import github.com.st235.data.models.ChartResponse
import io.reactivex.Observable
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query

internal interface ChartApi {

    @GET("charts/{type}")
    fun fetchChart(
        @Path("type") type: String = "transactions-per-second",
        @Query("timespan") timeSpan: String,
        @Query("rollingAverage") rollingAverage: String,
        @Query("format") format: String,
        @Query("sampled") sampled: Boolean
        ): Observable<ChartResponse>
}