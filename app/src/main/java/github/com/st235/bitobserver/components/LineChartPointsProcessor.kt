package github.com.st235.bitobserver.components

class LineChartPointsProcessor {

    /**
     * Points list is needed for a binary search afterward
     */
    private val xCoords: MutableList<Float> = mutableListOf()
    private val yCoords: MutableList<Float> = mutableListOf()

    private val extras: MutableList<Any> = mutableListOf()

    fun addPoint(x: Float, y: Float, extra: Any) {
        xCoords.add(x)
        yCoords.add(y)
        extras.add(extra)
    }

    fun findNearestTo(x: Float, y: Float): Triple<Float, Float, Any>? {
        if (xCoords.size == 0) {
            return null
        }

        var left = 0
        var right = xCoords.size - 1

        while (left <= right) {
            if (left == right) {
                return Triple(xCoords[left], yCoords[left], extras[left])
            }

            val middle = left + (right - left) / 2

            if (xCoords[middle] == x) {
                return Triple(xCoords[middle], yCoords[middle], extras[left])
            }

            if (x < xCoords[middle]) {
                right = middle
            } else {
                left = middle + 1
            }
        }

        return null
    }

    fun clear() {
        xCoords.clear()
        yCoords.clear()
        extras.clear()
    }
}