package github.com.st235.bitobserver.presentation.charts

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import github.com.st235.bitobserver.BitObserverApp
import github.com.st235.bitobserver.R
import github.com.st235.bitobserver.utils.Observer
import github.com.st235.data.models.ChartResponse
import github.com.st235.data.models.ChartResponseValue
import kotlinx.android.synthetic.main.activity_chart.*
import javax.inject.Inject

class ChartActivity : AppCompatActivity(), ChartView {

    @Inject
    lateinit var chartPresenter: ChartPresenter

    private val adapter = ChartAdapter()

    private val chartOnPointSelected: Observer<Any> = {
        if (it is ChartResponseValue) {
            chartPresenter.onChartHighlightedPointChanged(it)
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        BitObserverApp
            .get(applicationContext)?.appComponent
            ?.inject(this)

        setContentView(R.layout.activity_chart)
        chartPresenter.attach(this)

        chart.adapter = adapter
        chart.addOnPointSelectedObserver(chartOnPointSelected)
    }

    override fun setSelectedDate(dateText: CharSequence) {
        date.text = dateText
    }

    override fun setSelectedValue(valueText: CharSequence) {
        value.text = valueText
    }

    override fun showChart(response: ChartResponse) {
        adapter.addAll(response.values)
    }

    override fun onStop() {
        chartPresenter.detach()
        super.onStop()
    }
}
