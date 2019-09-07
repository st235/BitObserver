package github.com.st235.bitobserver.presentation.charts

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import github.com.st235.bitobserver.BitObserverApp
import github.com.st235.bitobserver.R
import github.com.st235.bitobserver.utils.Observer
import github.com.st235.data.models.ChartModel
import github.com.st235.data.models.ChartPoint
import github.com.st235.data.models.TimeInterval
import kotlinx.android.synthetic.main.activity_chart.*
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
            layoutManager = LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false)
            adapter = chartIntervalAdapter
            hasFixedSize()
        }
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
