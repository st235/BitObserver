package github.com.st235.bitobserver.components

import android.graphics.RectF
import kotlin.math.max
import kotlin.math.min

typealias LineDataObserver = () -> Unit
typealias ObserversList = MutableList<LineDataObserver>

/**
 * Line chart data adapter.
 * Helps load data into the chart view.
 */
abstract class LineChartAdapter {

    private val observersList: ObserversList = mutableListOf()

    abstract val count: Int

    open fun getX(index: Int): Float = index.toFloat()

    abstract fun getY(index: Int): Float

    abstract fun getData(index: Int): Any

    fun addLineDataObserver(observer: LineDataObserver) {
        observersList.add(observer)
    }

    fun removeLineDataObserver(observer: LineDataObserver) {
        observersList.remove(observer)
    }

    fun calculateBounds(): RectF {
        var minX = Float.MAX_VALUE
        var maxX = Float.MIN_VALUE
        var minY = Float.MAX_VALUE
        var maxY = Float.MIN_VALUE

        for (i in 0 until count) {
            val x = getX(i)
            val y = getY(i)

            minX = min(x, minX)
            maxX = max(x, maxX)

            minY = min(y, minY)
            maxY = max(y, maxY)
        }

        return RectF(minX, minY, maxX, maxY)
    }
}