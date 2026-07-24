package io.github.study0915.kuiklychart

import kotlin.math.abs
import kotlin.math.max
import kotlin.math.min
import kotlin.math.roundToInt

data class ChartRect(
    val left: Float,
    val top: Float,
    val right: Float,
    val bottom: Float,
) {
    val width: Float get() = max(0f, right - left)
    val height: Float get() = max(0f, bottom - top)
}

data class ChartRange(
    val minimum: Float,
    val maximum: Float,
) {
    val span: Float get() = maximum - minimum
}

data class ChartCoordinate(
    val seriesIndex: Int,
    val pointIndex: Int,
    val label: String,
    val value: Float,
    val x: Float,
    val y: Float,
)

object ChartMath {
    fun finiteSeries(series: List<ChartSeries>): List<ChartSeries> =
        series.mapNotNull { item ->
            val points = item.points.filter { it.value.isUsable() }
            if (points.isEmpty()) null else item.copy(points = points)
        }

    fun computeRange(series: List<ChartSeries>, includeZero: Boolean = false): ChartRange? {
        val values = series.flatMap { it.points }.map { it.value }.filter { it.isUsable() }
        if (values.isEmpty()) return null

        var minimum = values.minOrNull() ?: return null
        var maximum = values.maxOrNull() ?: return null
        if (includeZero) {
            minimum = min(minimum, 0f)
            maximum = max(maximum, 0f)
        }
        if (minimum == maximum) {
            val padding = max(1f, abs(minimum) * 0.05f)
            minimum -= padding
            maximum += padding
        }
        return ChartRange(minimum, maximum)
    }

    fun ticks(range: ChartRange, count: Int = 5): List<Float> {
        val safeCount = max(2, count)
        val step = range.span / (safeCount - 1)
        return List(safeCount) { index -> range.minimum + step * index }
    }

    fun valueToY(value: Float, range: ChartRange, plot: ChartRect): Float {
        if (!value.isUsable() || range.span <= 0f || plot.height <= 0f) return plot.bottom
        val ratio = ((value - range.minimum) / range.span).coerceIn(0f, 1f)
        return plot.bottom - ratio * plot.height
    }

    fun indexToX(index: Int, count: Int, plot: ChartRect): Float {
        if (count <= 1 || plot.width <= 0f) return plot.left + plot.width / 2f
        return plot.left + plot.width * index.coerceIn(0, count - 1) / (count - 1)
    }

    fun pointIndexForX(x: Float, count: Int, plot: ChartRect): Int? {
        if (count <= 0 || plot.width <= 0f) return null
        if (count == 1) return 0
        val ratio = ((x - plot.left) / plot.width).coerceIn(0f, 1f)
        return (ratio * (count - 1)).roundToInt().coerceIn(0, count - 1)
    }

    fun layout(
        series: List<ChartSeries>,
        plot: ChartRect,
        range: ChartRange? = computeRange(series),
    ): List<ChartCoordinate> {
        if (range == null) return emptyList()
        val result = mutableListOf<ChartCoordinate>()
        series.forEachIndexed { seriesIndex, item ->
            item.points.forEachIndexed { pointIndex, point ->
                if (point.value.isUsable()) {
                    result += ChartCoordinate(
                        seriesIndex = seriesIndex,
                        pointIndex = pointIndex,
                        label = point.label,
                        value = point.value,
                        x = indexToX(pointIndex, item.points.size, plot),
                        y = valueToY(point.value, range, plot),
                    )
                }
            }
        }
        return result
    }

    fun nearestPoint(
        points: List<ChartCoordinate>,
        x: Float,
        y: Float,
        maxDistance: Float = Float.POSITIVE_INFINITY,
    ): ChartCoordinate? {
        if (!x.isUsable() || !y.isUsable() || maxDistance < 0f) return null
        var nearest: ChartCoordinate? = null
        var nearestDistanceSquared = maxDistance * maxDistance
        points.forEach { point ->
            val dx = point.x - x
            val dy = point.y - y
            val distanceSquared = dx * dx + dy * dy
            if (distanceSquared <= nearestDistanceSquared) {
                if (
                    nearest == null ||
                    distanceSquared < nearestDistanceSquared ||
                    point.seriesIndex < nearest!!.seriesIndex ||
                    (point.seriesIndex == nearest!!.seriesIndex && point.pointIndex < nearest!!.pointIndex)
                ) {
                    nearest = point
                    nearestDistanceSquared = distanceSquared
                }
            }
        }
        return nearest
    }

    fun nearestPointByX(points: List<ChartCoordinate>, x: Float): ChartCoordinate? {
        if (!x.isUsable()) return null
        return points.minWithOrNull(
            compareBy<ChartCoordinate> { abs(it.x - x) }
                .thenBy { it.seriesIndex }
                .thenBy { it.pointIndex },
        )
    }

    fun sampleLabelIndices(count: Int, maxLabels: Int): List<Int> {
        if (count <= 0 || maxLabels <= 0) return emptyList()
        if (count <= maxLabels) return List(count) { it }
        if (maxLabels == 1) return listOf(0)
        return (0 until maxLabels)
            .map { index -> (index.toFloat() * (count - 1) / (maxLabels - 1)).roundToInt() }
            .distinct()
    }
}
