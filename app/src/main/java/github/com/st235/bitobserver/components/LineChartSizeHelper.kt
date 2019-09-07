package github.com.st235.bitobserver.components

import android.graphics.RectF

/**
 * Helping class to handle resizing one basis into another.
 */
class LineChartSizeHelper(
    graphBounds: RectF,
    viewportBounds: RectF,
    lineWidth: Float
) {

    private val xScale: Float

    private val yScale: Float

    private val xTranslate: Float

    private val yTranslate: Float

    init {
        xScale = viewportBounds.width() / graphBounds.width()
        yScale = viewportBounds.height() / graphBounds.height()

        xTranslate = viewportBounds.left - graphBounds.left * xScale + lineWidth / 2
        yTranslate = viewportBounds.top - graphBounds.top * yScale + lineWidth / 2
    }

    fun scaleX(rawX: Float): Float = rawX * xScale + xTranslate

    fun scaleY(rawY: Float): Float = rawY * yScale + yTranslate
}
