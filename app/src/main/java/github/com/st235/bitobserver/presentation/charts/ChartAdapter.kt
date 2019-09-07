package github.com.st235.bitobserver.presentation.charts

import github.com.st235.bitobserver.components.LineChartAdapter
import github.com.st235.data.models.ChartPoint

class ChartAdapter: LineChartAdapter() {

    private val points: MutableList<ChartPoint> = mutableListOf()

    override fun getSize(): Int = points.size

    override fun getY(index: Int): Float = points[index].value

    override fun getData(index: Int): Any = points[index]

    fun addAll(points: List<ChartPoint>) {
        this.points.clear()
        this.points.addAll(points)
        notifyObservers(Unit)
    }
}