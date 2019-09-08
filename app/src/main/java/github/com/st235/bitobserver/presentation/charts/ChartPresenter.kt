package github.com.st235.bitobserver.presentation.charts

import github.com.st235.bitobserver.debug.ThreadUtils
import github.com.st235.bitobserver.presentation.base.BasePresenter
import github.com.st235.bitobserver.utils.RxSchedulers
import github.com.st235.bitobserver.utils.clipAndFormat
import github.com.st235.bitobserver.utils.dateFromTimeStamp
import github.com.st235.data.ChartRepository
import github.com.st235.data.models.ChartPoint
import github.com.st235.data.models.TimeInterval
import io.reactivex.android.schedulers.AndroidSchedulers
import timber.log.Timber
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class ChartPresenter @Inject constructor(
    private val chartRepository: ChartRepository,
    private val threadAssertionUtils: ThreadUtils,
    private val schedulers: RxSchedulers
): BasePresenter<ChartView>() {

    override fun onAttach(v: ChartView) {
        super.onAttach(v)

        chartRepository.fetchCharts()
            .compose(schedulers.getComputationToMainTransformer())
            .subscribeTillDetach(onNext = {
                threadAssertionUtils.assertOnMainThread()
                this.view?.hideLoader()
                this.view?.showChart(it)
            })

        chartRepository.getAllAvailableTimeIntervals()
            .compose(schedulers.getComputationToMainTransformerSingle())
            .subscribeTillDetach(onNext = {
                threadAssertionUtils.assertOnMainThread()
                this.view?.setAvailableTimeIntervals(it)
            })
    }

    fun onChartHighlightedPointChanged(point: ChartPoint) {
        this.view?.setSelectedDate(point.time.dateFromTimeStamp())
        this.view?.setSelectedValue(point.value.clipAndFormat())
    }

    fun onNewTimeInterval(interval: TimeInterval) {
        this.view?.showLoader()

        chartRepository.changeTimeInterval(interval)
            .compose(schedulers.getIoToMainTransformerCompletable())
            .subscribe()
    }
}