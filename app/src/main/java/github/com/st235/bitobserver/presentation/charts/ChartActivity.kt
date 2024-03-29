package github.com.st235.bitobserver.presentation.charts

import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import github.com.st235.bitobserver.BitObserverApp
import github.com.st235.bitobserver.R
import github.com.st235.bitobserver.utils.Observer
import github.com.st235.bitobserver.utils.alternateForOrientation
import github.com.st235.data.models.ChartModel
import github.com.st235.data.models.ChartPoint
import github.com.st235.data.models.TimeInterval
import kotlinx.android.synthetic.main.activity_chart.*
import kotlinx.android.synthetic.main.content_loading.*
import javax.inject.Inject

class ChartActivity : AppCompatActivity(), ChartView {

    @Inject
    lateinit var chartPresenter: ChartPresenter

    private val chartAdapter = ChartAdapter()
    private val chartIntervalAdapter = ChartIntervalAdapter()

    private val chartOnPointSelected: Observer<Any> = {
        if (it is ChartPoint) {
            chartPresenter.onChartHighlightedPointChanged(it)
        }
    }

    private val onTimeIntervalChangedListener = { timeInterval: TimeInterval, index: Int ->
        chartPresenter.onNewTimeInterval(timeInterval)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        BitObserverApp
            .get(applicationContext)?.appComponent
            ?.inject(this)

        setContentView(R.layout.activity_chart)
        chartPresenter.attach(this)

        chart.adapter = chartAdapter
        chart.addOnPointSelectedObserver(chartOnPointSelected)

        chartIntervalAdapter.itemClickListener = onTimeIntervalChangedListener

        with(recycler) {
            layoutManager = alternateForOrientation(
                portraitResource = LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false),
                landscapeResource = LinearLayoutManager(context)
            )
            adapter = chartIntervalAdapter
            itemAnimator = null
            hasFixedSize()
        }
    }

    override fun showLoader() {
        loader.visibility = View.VISIBLE
    }

    override fun hideLoader() {
        loader.visibility = View.GONE
    }

    override fun setSelectedDate(dateText: CharSequence) {
        date.text = dateText
    }

    override fun setSelectedValue(valueText: CharSequence) {
        value.text = valueText
    }

    override fun showChart(model: ChartModel) {
        chartAdapter.addAll(model.values)
    }

    override fun setAvailableTimeIntervals(intervals: List<TimeInterval>) {
        chartIntervalAdapter.addAll(intervals)
    }

    override fun onDestroy() {
        chartPresenter.detach()
        super.onDestroy()
    }
}
