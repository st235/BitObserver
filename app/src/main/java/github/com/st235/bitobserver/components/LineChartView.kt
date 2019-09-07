package github.com.st235.bitobserver.components

import android.content.Context
import android.graphics.*
import android.text.TextPaint
import android.util.AttributeSet
import android.view.View
import androidx.core.content.ContextCompat
import github.com.st235.bitobserver.R
import github.com.st235.bitobserver.utils.ObservableModel
import github.com.st235.bitobserver.utils.Observer
import github.com.st235.bitobserver.utils.spToPx
import github.com.st235.bitobserver.utils.toPx

class LineChartView @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0
) : View(context, attrs, defStyleAttr) {

    companion object {
        val LINE_WIDTH = 3F.toPx()
        val CORNER_RADIUS = 6F.toPx()
        val GRID_LINES_WIDTH = 1F.toPx()
        val GRID_GAP_LENGTH = 2F.toPx()
        val HIGHLIGHTED_POINT_RADIUS = 8F.toPx()
        val GRID_TEXT_PADDING = 8F.toPx()
        const val GRID_LINES_COUNT = 4
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
        pathEffect = CornerPathEffect(CORNER_RADIUS)
    }

    private val gridPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        style = Paint.Style.STROKE
        color = ContextCompat.getColor(context, R.color.colorChartGridLine)
        strokeWidth = GRID_LINES_WIDTH
        pathEffect = DashPathEffect(floatArrayOf(GRID_GAP_LENGTH, GRID_GAP_LENGTH), 0F)
    }

    private val gridTextPaint = TextPaint(Paint.ANTI_ALIAS_FLAG).apply {
        style = Paint.Style.FILL
        color = ContextCompat.getColor(context, R.color.colorChartGridText)
        textSize = 12F.spToPx()
    }

    private val highlightedPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        style = Paint.Style.FILL
        color = ContextCompat.getColor(context, R.color.colorChartHighlightedPoint)
    }

    private val chartBounds = RectF()
    private val viewportBounds = RectF()
    private var highlightedPoint: PointF? = null
    private var sizeResolver: LineChartSizeHelper? = null

    private val lineChartProcessor = LineChartPointsProcessor()
    private val lineChartClickListener =
        LineChartClickListener().apply {
            addObserver {
                val point = lineChartProcessor.findNearestTo(it.first, it.second)
                if (point != null) {
                    highlightedPoint = PointF(point.first, point.second)
                    pointSelectionObservers.notifyObservers(point.third)
                    invalidate()
                }
            }
        }

    private val pointSelectionObservers = ObservableModel<Any>()

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

    fun addOnPointSelectedObserver(observer: Observer<Any>) {
        pointSelectionObservers.addObserver(observer)
    }

    fun removedOnPointSelectedObserver(observer: Observer<Any>) {
        pointSelectionObservers.removeObserver(observer)
    }

    override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {
        super.onSizeChanged(w, h, oldw, oldh)
        viewportBounds.set(
            paddingLeft.toFloat(),
            paddingTop.toFloat(),
            w.toFloat() - paddingRight,
            h.toFloat() - paddingBottom)
        baseFillPaint.shader =
            LinearGradient(
                0F, 0F, 0F, h.toFloat(),
                ContextCompat.getColor(context, R.color.colorChartGradientStart),
                ContextCompat.getColor(context, R.color.colorChartGradientFinish),
                Shader.TileMode.CLAMP
            )
        populatePath()
    }

    override fun onDraw(canvas: Canvas?) {
        super.onDraw(canvas)

        drawGrid(canvas)

        canvas?.drawPath(drawPath, baseFillPaint)
        canvas?.drawPath(drawPath, basePaint)

        val highlightedPoint = highlightedPoint
        if (highlightedPoint != null) {
            canvas?.drawCircle(
                highlightedPoint.x,
                highlightedPoint.y,
                HIGHLIGHTED_POINT_RADIUS,
                highlightedPaint
            )
        }
    }

    private fun drawGrid(canvas: Canvas?) {
        if (adapter == null || adapter?.getSize() ?: 0 < 2) {
            return
        }

        val max = GRID_LINES_COUNT
        for (i in 1 until max) {
            val progress = i.toFloat() / max
            val y = height.toFloat() * progress

            canvas?.drawLine(
                0F, y,
                width.toFloat(), y,
                gridPaint
            )

            canvas?.drawText(
                sizeResolver.extractY(y),
                paddingLeft + GRID_TEXT_PADDING,
                height.toFloat() * progress - GRID_TEXT_PADDING,
                gridTextPaint
            )
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
        val sizeResolver = LineChartSizeHelper(chartBounds, viewportBounds, LINE_WIDTH, HIGHLIGHTED_POINT_RADIUS)
        this.sizeResolver = sizeResolver

        for (i in 0 until adapter.getSize()) {
            val x = sizeResolver.scaleX(adapter.getX(i))
            val scaledY = sizeResolver.scaleY(adapter.getY(i))
            val y = sizeResolver.normalizeY(scaledY, paddingTop.toFloat())
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
        highlightedPoint = null
        sizeResolver = null
        drawPath.reset()
        lineChartProcessor.clear()
    }

    private fun LineChartSizeHelper?.extractY(normalizedY: Float): String {
        if (this == null) {
            return ""
        }

        val denormalizedY = this.denormalizeY(normalizedY, paddingTop.toFloat())
        return this.rawY(denormalizedY).toInt().toString()
    }
}
