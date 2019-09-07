package github.com.st235.bitobserver.components

import android.content.Context
import android.graphics.*
import android.util.AttributeSet
import android.view.View
import github.com.st235.bitobserver.utils.toPx

class LineChartView @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0
) : View(context, attrs, defStyleAttr) {

    companion object {
        val LINE_WIDTH = 2F.toPx()
        val CORNER_RADIUS = 4F.toPx()
    }

    private val drawPath = Path()

    private val paint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        style = Paint.Style.STROKE
        color = Color.BLACK
        strokeWidth = LINE_WIDTH
        pathEffect = CornerPathEffect(CORNER_RADIUS)
    }

    private val viewportBounds = RectF()

    var adapter: LineChartAdapter? = null
    set(value) {
        field = value
        populatePath()
        invalidate()
    }

    override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {
        super.onSizeChanged(w, h, oldw, oldh)
        viewportBounds.set(
            leftPaddingOffset.toFloat(),
            topPaddingOffset.toFloat(),
            leftPaddingOffset + w.toFloat(),
            topPaddingOffset + h.toFloat())
    }

    override fun onDraw(canvas: Canvas?) {
        super.onDraw(canvas)

        canvas?.drawPath(drawPath, paint)
    }

    private fun populatePath() {
        val adapter = adapter

        if (adapter == null || viewportBounds.isEmpty) {
            return
        }

        drawPath.reset()

        val bounds = adapter.calculateBounds()
        val sizeResolver = LineChartSizeHelper(bounds, viewportBounds, LINE_WIDTH)

        for (i in 0 until adapter.count) {
            val x = sizeResolver.scaleX(adapter.getX(i))
            val y = sizeResolver.scaleY(adapter.getY(i))

            if (i == 0) {
                drawPath.moveTo(x, y)
            } else {
                drawPath.lineTo(x, y)
            }
        }
    }
}
