package github.com.st235.bitobserver.presentation.charts

import android.os.Bundle
import android.text.Spannable
import android.text.SpannableString
import android.text.SpannableStringBuilder
import android.text.format.DateFormat
import android.text.style.RelativeSizeSpan
import androidx.appcompat.app.AppCompatActivity
import github.com.st235.bitobserver.BitObserverApp
import github.com.st235.bitobserver.R
import github.com.st235.bitobserver.utils.Observer
import github.com.st235.data.models.ChartResponse
import github.com.st235.data.models.ChartResponseValue
import kotlinx.android.synthetic.main.activity_chart.*
import java.util.*
import javax.inject.Inject

class ChartActivity : AppCompatActivity(), ChartView {

    @Inject
    lateinit var chartPresenter: ChartPresenter

    private val adapter = ChartAdapter()

    private val chartOnPointSelected: Observer<Any> = {
        if (it is ChartResponseValue) {
            date.text = getDate(it.time)
            value.text = getValue(value = it.value)
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

    private fun getValue(value: Float): Spannable {
        val builder = SpannableStringBuilder()

        val iv = value.toInt()
        val lv = String.format("%1$02d", (value * 100).toInt() % 100)

        builder
            .append(iv.toString())
            .append('.')
            .append(lv)

        builder.setSpan(RelativeSizeSpan(0.5F), builder.length - 3, builder.length, SpannableString.SPAN_EXCLUSIVE_EXCLUSIVE)
        builder.append(" $")

        return builder
    }

    private fun getDate(time: Long): String {
        val cal = Calendar.getInstance(Locale.getDefault())
        cal.setTimeInMillis(time * 1000)
        return DateFormat.format("dd MMMM, yyyy", cal).toString()
    }
}
