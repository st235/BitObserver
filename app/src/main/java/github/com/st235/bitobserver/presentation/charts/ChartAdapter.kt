package github.com.st235.bitobserver.presentation.charts

import github.com.st235.bitobserver.components.LineChartAdapter
import github.com.st235.data.models.ChartResponseValue

class ChartAdapter: LineChartAdapter() {

    private val points: MutableList<ChartResponseValue> = mutableListOf()

    override fun getSize(): Int = points.size

    override fun getY(index: Int): Float = points[index].value

    override fun getData(index: Int): Any = points[index]

    fun addAll(points: List<ChartResponseValue>) {
        this.points.clear()
        this.points.addAll(points)
        notifyObservers(Unit)
    }
}