package github.com.st235.bitobserver.presentation.charts

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import github.com.st235.bitobserver.BitObserverApp
import github.com.st235.bitobserver.R
import javax.inject.Inject

class ChartActivity : AppCompatActivity(), ChartView {

    @Inject
    lateinit var chartPresenter: ChartPresenter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        BitObserverApp
            .get(applicationContext)?.appComponent
            ?.inject(this)

        setContentView(R.layout.activity_main)

        chartPresenter.attach(this)
    }

    override fun onStop() {
        chartPresenter.detach()
        super.onStop()
    }
}
