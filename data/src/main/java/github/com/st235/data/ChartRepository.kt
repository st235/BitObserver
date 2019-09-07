package github.com.st235.data

import github.com.st235.data.net.ChartApi
import github.com.st235.data.net.RetrofitFactory

class ChartRepository internal constructor(
    private val chartApi: ChartApi
) {

    companion object {
        fun createInstance(): ChartRepository {
            return ChartRepository(chartApi = RetrofitFactory.createClient(BuildConfig.BASE_API_URL))
        }
    }

    fun fetchCharts() =
        chartApi.fetchChart(
            type = "transactions-per-second",
            timeSpan = "5weeks",
            rollingAverage = "8hours",
            format = "json",
            sampled = true
        )
}
