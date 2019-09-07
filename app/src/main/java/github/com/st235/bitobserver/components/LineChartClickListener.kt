package github.com.st235.bitobserver.components

import android.view.MotionEvent
import android.view.View
import github.com.st235.bitobserver.utils.ObservableModel


class LineChartClickListener: ObservableModel<Pair<Float, Float>>(), View.OnTouchListener {

    override fun onTouch(v: View, event: MotionEvent): Boolean {
        when (event.action) {
            MotionEvent.ACTION_DOWN,
            MotionEvent.ACTION_MOVE -> {
                notifyObservers(Pair(event.x, event.y))
            }
        }
        v.parent?.requestDisallowInterceptTouchEvent(true)
        return false
    }
}
