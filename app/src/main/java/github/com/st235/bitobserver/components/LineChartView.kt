package github.com.st235.bitobserver.components

import android.content.Context
import android.graphics.*
import android.util.AttributeSet
import android.view.View
import androidx.core.content.ContextCompat
import github.com.st235.bitobserver.R
import github.com.st235.bitobserver.utils.ObservableModel
import github.com.st235.bitobserver.utils.Observer
import github.com.st235.bitobserver.utils.toPx

class LineChartView @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0
) : View(context, attrs, defStyleAttr) {

    companion object {
        val LINE_WIDTH = 2F.toPx()
        val CORNER_RADIUS = 4F.toPx()
    }

    private val drawPath = Path()

    private val basePaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        style = Paint.Style.STROKE
        color = ContextCompat.getColor(context, R.color.colorChartStroke)
        strokeWidth = LINE_WIDTH
        pathEffect = CornerPathEffect(CORNER_RADIUS)
    }

    private val baseFillPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        style = Paint.Style.FILL
    }

    private val gridPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        style = Paint.Style.STROKE
        color = Color.GRAY
        strokeWidth = LINE_WIDTH
    }

    private val highlightedPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        style = Paint.Style.FILL
        color = ContextCompat.getColor(context, R.color.colorChartHighlightedPoint)
    }

    private val chartBounds = RectF()
    private val viewportBounds = RectF()
    private var highlightedPoint: PointF? = null

    private val lineChartProcessor = LineChartPointsProcessor()
    private val lineChartClickListener =
        LineChartClickListener().apply {
            addObserver {
                val point = lineChartProcessor.findNearestTo(it.first, it.second)
                if (point != null) {
                    highlightedPoint = PointF(point.first, point.second)
                    clickObserves.notifyObservers(point.third)
                    invalidate()
                }
            }
        }

    private val clickObserves = ObservableModel<Any>()

    var adapter: LineChartAdapter? = null
    set(value) {
        field = value
        value?.addObserver {
            onNewData()
        }
        onNewData()
    }

    init {
        isClickable = true
        isFocusable = true
        setOnTouchListener(lineChartClickListener)
    }

    fun addOnClickObserver(observer: Observer<Any>) {
        clickObserves.addObserver(observer)
    }

    fun removeOnClickObserver(observer: Observer<Any>) {
        clickObserves.removeObserver(observer)
    }

    override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {
        super.onSizeChanged(w, h, oldw, oldh)
        viewportBounds.set(
            paddingLeft.toFloat(),
            paddingBottom.toFloat(),
            w.toFloat() - paddingRight - paddingRight,
            h.toFloat() - paddingTop - paddingBottom)
        baseFillPaint.shader = LinearGradient(0F, 0F, 0F, h.toFloat(),
            ContextCompat.getColor(context, R.color.colorChartGradientStart), ContextCompat.getColor(context, R.color.colorChartGradientFinish), Shader.TileMode.CLAMP)
        populatePath()
    }

    override fun onDraw(canvas: Canvas?) {
        super.onDraw(canvas)

        canvas?.drawPath(drawPath, baseFillPaint)
        canvas?.drawPath(drawPath, basePaint)

//        val max = 3
//        for (i in 1 until max) {
//            val progress = i.toFloat()/max
//            canvas?.drawLine(0F, height.toFloat() * progress, width.toFloat(), height.toFloat() * progress, gridPaint)
//        }

        val highlightedPoint = highlightedPoint
        if (highlightedPoint != null) {
            canvas?.drawCircle(highlightedPoint.x, highlightedPoint.y, 8F.toPx(), highlightedPaint)
        }
    }

    private fun onNewData() {
        populatePath()
        invalidate()
    }

    private fun populatePath() {
        val adapter = adapter

        if (adapter == null || viewportBounds.isEmpty) {
            return
        }

        if (adapter.getSize() < 2) {
            return
        }

        clearState()

        chartBounds.set(adapter.calculateBounds())
        val sizeResolver = LineChartSizeHelper(chartBounds, viewportBounds, LINE_WIDTH)

        for (i in 0 until adapter.getSize()) {
            val x = sizeResolver.scaleX(adapter.getX(i))
            val y = viewportBounds.bottom - sizeResolver.scaleY(adapter.getY(i)) + paddingTop
            lineChartProcessor.addPoint(x, y, adapter.getData(i))

            if (i == 0) {
                drawPath.moveTo(x, y)
            } else {
                drawPath.lineTo(x, y)
            }
        }

        drawPath.lineTo(width.toFloat() + LINE_WIDTH / 2, height.toFloat() + LINE_WIDTH / 2)
        drawPath.lineTo( LINE_WIDTH / 2, height.toFloat() + LINE_WIDTH / 2)
        drawPath.lineTo(adapter.getX(0), adapter.getY(0))
    }

    private fun clearState() {
        drawPath.reset()
        lineChartProcessor.clear()
    }
}
