package github.com.st235.bitobserver.presentation.charts

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import github.com.st235.bitobserver.BitObserverApp
import github.com.st235.bitobserver.R
import github.com.st235.bitobserver.components.LineChartAdapter
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
            date.text = it.time.toString()
            value.text = getString(R.string.btc_to_usd, it.value)
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
        chart.addOnClickObserver(chartOnPointSelected)
    }

    override fun show(response: ChartResponse) {
        adapter.addAll(response.values)
    }

    override fun onStop() {
        chartPresenter.detach()
        super.onStop()
    }
}
