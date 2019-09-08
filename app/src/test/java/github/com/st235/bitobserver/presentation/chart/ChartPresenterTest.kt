package github.com.st235.bitobserver.presentation.chart

import com.nhaarman.mockitokotlin2.*
import github.com.st235.bitobserver.debug.ThreadUtils
import github.com.st235.bitobserver.presentation.charts.ChartPresenter
import github.com.st235.bitobserver.presentation.charts.ChartView
import github.com.st235.bitobserver.utils.TestRxSchedulersImpl
import github.com.st235.data.ChartRepository
import github.com.st235.data.models.ChartModel
import github.com.st235.data.models.TimeInterval
import io.reactivex.Completable
import io.reactivex.Observable
import io.reactivex.subjects.PublishSubject
import org.junit.Before
import org.junit.Test


class ChartPresenterTest {

    private val chartRepository = mock<ChartRepository>()
    private val testSchedulers = TestRxSchedulersImpl()
    private val threadAssertionUtils = mock<ThreadUtils>()
    private val chart = mock<ChartModel>()
    private val view = mock<ChartView>()

    private val publishSubject = PublishSubject.create<TimeInterval>()

    private lateinit var presenter: ChartPresenter

    @Before
    fun setUp() {
        presenter = ChartPresenter(chartRepository, threadAssertionUtils, testSchedulers)

        whenever(chartRepository.fetchCharts())
            .thenReturn(publishSubject.startWith(TimeInterval.FIVE_WEEKS).flatMap { Observable.just(chart) })
        whenever(chartRepository.getAllAvailableTimeIntervals()).thenReturn(Observable.fromArray(*TimeInterval.values()).toList())
    }

    @Test
    fun `test that attach will load chart and time intervals`() {
        presenter.attach(view)

        verify(chartRepository).fetchCharts()
        verify(chartRepository).getAllAvailableTimeIntervals()

        verify(view).hideLoader()
        verify(view).showChart(chart)
        verify(view).setAvailableTimeIntervals(TimeInterval.values().toList())
        verify(threadAssertionUtils, times(2)).assertOnMainThread()
    }

    @Test
    fun `test that change of time interval will load new chart`() {
        presenter.attach(view)
        whenever(chartRepository.changeTimeInterval(any())).thenAnswer {
            publishSubject.onNext(it.arguments[0] as TimeInterval)
            return@thenAnswer Completable.complete()
        }

        presenter.onNewTimeInterval(TimeInterval.ALL)
        verify(chartRepository).changeTimeInterval(TimeInterval.ALL)

        verify(chartRepository).fetchCharts()
        verify(view, times(2)).hideLoader()
        verify(view, times(2)).showChart(chart)
    }
}