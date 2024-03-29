package github.com.st235.bitobserver.components

import android.content.Context
import android.graphics.*
import android.text.TextPaint
import android.util.AttributeSet
import android.view.View
import androidx.core.content.ContextCompat
import github.com.st235.bitobserver.R
import github.com.st235.bitobserver.utils.*
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

        const val MIN_GRID_LINES = 3
        const val MAX_GRID_LINES = 5

        /**
         * Should be sorted in a reverse order
         */
        val POSSIBLE_GRID_STEPS = intArrayOf(10000, 5000, 2000, 1000, 500, 300, 200, 100, 10)
    }

    private val strokePath = Path()
    private val fillPath = Path()
    private val gridPath = Path()

    private val gridLineCoordinates = mutableListOf<Pair<String, Float>>()

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
        LineChartPointSelectedListener().apply {
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

        canvas?.drawPath(fillPath, baseFillPaint)
        canvas?.drawPath(strokePath, basePaint)

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

        for ((positionY, viewportY) in gridLineCoordinates) {
            canvas?.drawText(
                positionY,
                paddingLeft + GRID_TEXT_PADDING,
                viewportY - GRID_TEXT_PADDING,
                gridTextPaint
            )
        }

        canvas?.drawPath(gridPath, gridPaint)
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

            when (i) {
                0 -> strokePath.moveTo(x, y)
                else -> strokePath.lineTo(x, y)
            }

        }

        val firstPoint = lineChartProcessor.first()
        val lastPoint = lineChartProcessor.last()

        highlightedPoint = PointF(lastPoint.first, lastPoint.second)
        pointSelectionObservers.notifyObservers(lastPoint.third)

        fillPath.addPath(strokePath)

        fillPath.lineTo(lastPoint.first, height.toFloat())
        fillPath.lineTo( firstPoint.first, height.toFloat())
        fillPath.lineTo(firstPoint.first, firstPoint.second)

        calculateGrid(chartBounds) { positionY, viewportY ->
            gridPath.moveTo(0F, viewportY)
            gridPath.lineTo(width.toFloat(), viewportY)

            gridLineCoordinates.add(Pair(positionY.toString(), viewportY))
        }
    }

    private fun clearState() {
        highlightedPoint = null
        sizeResolver = null
        gridPath.reset()
        fillPath.reset()
        strokePath.reset()
        gridLineCoordinates.clear()
        lineChartProcessor.clear()
    }

    private inline fun calculateGrid(
        chartBounds: RectF,
        onLineReady: (positionY: Int, viewportY: Float) -> Unit
    ) {
        val sizeResolver = sizeResolver ?: return
        val amplitude = chartBounds.height()

        var currentStep = 0

        for (step in POSSIBLE_GRID_STEPS) {
            if (amplitude / step > MIN_GRID_LINES &&
                amplitude / step <= MAX_GRID_LINES
            ) {
                currentStep = step
            }
        }

        if (currentStep == 0) {
            return
        }

        var currentY = chartBounds.top.findNearest(currentStep)

        while (currentY <= chartBounds.bottom) {
            val scaledY = sizeResolver.scaleY(currentY)
            val viewportY = sizeResolver.normalizeY(scaledY, paddingTop.toFloat())

            onLineReady(currentY.toInt(), viewportY)

            currentY += currentStep
        }
    }

    private fun LineChartPointsProcessor.first(): Triple<Float, Float, Any> = get(0)

    private fun LineChartPointsProcessor.last(): Triple<Float, Float, Any> =
        get((adapter?.getSize() ?: 1) - 1)
}
