package github.com.st235.bitobserver.components

import android.content.Context
import android.graphics.*
import android.util.AttributeSet
import android.view.View
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

    private val paint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        style = Paint.Style.STROKE
        color = Color.WHITE
        strokeWidth = LINE_WIDTH
        pathEffect = CornerPathEffect(CORNER_RADIUS)
    }

    private val gridPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        style = Paint.Style.STROKE
        color = Color.GRAY
        strokeWidth = LINE_WIDTH
    }

    private val highlightedPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        style = Paint.Style.FILL
        color = Color.CYAN
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
            leftPaddingOffset.toFloat(),
            topPaddingOffset.toFloat(),
            leftPaddingOffset + w.toFloat(),
            topPaddingOffset + h.toFloat())
        populatePath()
    }

    override fun onDraw(canvas: Canvas?) {
        super.onDraw(canvas)

        canvas?.drawPath(drawPath, paint)

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
            val y = viewportBounds.bottom - sizeResolver.scaleY(adapter.getY(i))
            lineChartProcessor.addPoint(x, y, adapter.getData(i))

            if (i == 0) {
                drawPath.moveTo(x, y)
            } else {
                drawPath.lineTo(x, y)
            }
        }
    }

    private fun clearState() {
        drawPath.reset()
        lineChartProcessor.clear()
    }
}
