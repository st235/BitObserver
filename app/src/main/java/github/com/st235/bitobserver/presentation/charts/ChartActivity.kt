package github.com.st235.bitobserver.presentation.charts

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import github.com.st235.bitobserver.BitObserverApp
import github.com.st235.bitobserver.R
import github.com.st235.bitobserver.components.LineChartAdapter
import github.com.st235.data.models.ChartResponse
import kotlinx.android.synthetic.main.activity_chart.*
import javax.inject.Inject


class ChartActivity : AppCompatActivity(), ChartView {

    @Inject
    lateinit var chartPresenter: ChartPresenter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        BitObserverApp
            .get(applicationContext)?.appComponent
            ?.inject(this)

        setContentView(R.layout.activity_chart)

        chartPresenter.attach(this)
    }

    override fun show(response: ChartResponse) {
        chart.adapter = object : LineChartAdapter() {
            override val count: Int = response.values.size

            override fun getY(index: Int): Float = response.values[index].value

            override fun getData(index: Int): Any = response.values[index]
        }
    }

    override fun onStop() {
        chartPresenter.detach()
        super.onStop()
    }
}
