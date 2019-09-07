package github.com.st235.data

import github.com.st235.data.models.TimeInterval
import github.com.st235.data.net.ChartApi
import github.com.st235.data.net.RetrofitFactory
import io.reactivex.Completable
import io.reactivex.Observable
import io.reactivex.Single
import io.reactivex.subjects.PublishSubject

class ChartRepository internal constructor(
    private val chartApi: ChartApi
) {

    companion object {
        fun createInstance(): ChartRepository {
            return ChartRepository(chartApi = RetrofitFactory.createClient(BuildConfig.BASE_API_URL))
        }
    }

    private val apiSubject = PublishSubject.create<TimeInterval>()

    fun fetchCharts() =
        apiSubject
            .startWith(TimeInterval.FIVE_WEEKS)
            .flatMap {
                chartApi.fetchChart(
                    timeSpan = it.requestId,
                    rollingAverage = "8hours",
                    format = "json",
                    sampled = true
                )
        }

    fun changeTimeInterval(interval: TimeInterval) = Completable.fromAction {
        apiSubject.onNext(interval)
    }

    fun getAllAvailableTimeIntervals(): Single<MutableList<TimeInterval>> =
        Observable.fromArray(*TimeInterval.values())
            .toList()
}
