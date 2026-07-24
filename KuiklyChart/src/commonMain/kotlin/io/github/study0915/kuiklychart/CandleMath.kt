package io.github.study0915.kuiklychart

import kotlin.math.abs
import kotlin.math.max
import kotlin.math.min
import kotlin.math.roundToInt

data class CandleRange(
    val minimum: Float,
    val maximum: Float,
) {
    val span: Float get() = maximum - minimum
}

data class CandleWindow(
    val startIndex: Int,
    val endExclusive: Int,
) {
    val count: Int get() = max(0, endExclusive - startIndex)
}

data class CandleCoordinate(
    val sourceIndex: Int,
    val candle: CandlePoint,
    val x: Float,
    val openY: Float,
    val highY: Float,
    val lowY: Float,
    val closeY: Float,
    val volume: Float,
)

object CandleMath {
    fun finiteIndices(candles: List<CandlePoint>): List<Int> =
        candles.mapIndexedNotNull { index, candle ->
            if (candle.isUsable()) index else null
        }

    fun computeRange(candles: List<CandlePoint>): CandleRange? {
        val values = candles
            .filter { it.isUsable() }
            .flatMap { listOf(it.high, it.low, it.open, it.close) }
            .filter { it.isUsable() }
        if (values.isEmpty()) return null
        var minimum = values.minOrNull() ?: return null
        var maximum = values.maxOrNull() ?: return null
        if (minimum == maximum) {
            val padding = max(0.01f, abs(minimum) * 0.05f)
            minimum -= padding
            maximum += padding
        }
        return CandleRange(minimum, maximum)
    }

    fun volumeRange(candles: List<CandlePoint>): Float {
        return candles.map { it.volume }.filter { it.isUsable() }.maxOrNull()?.coerceAtLeast(1f) ?: 1f
    }

    /** offset=0 anchors the newest candles at the right edge; positive values pan toward history. */
    fun visibleWindow(totalCount: Int, visibleCount: Int, offset: Int = 0): CandleWindow {
        if (totalCount <= 0) return CandleWindow(0, 0)
        val count = visibleCount.coerceIn(1, totalCount)
        val maxStart = totalCount - count
        val start = (maxStart - offset).coerceIn(0, maxStart)
        return CandleWindow(start, start + count)
    }

    fun panOffset(currentOffset: Int, delta: Int, totalCount: Int, visibleCount: Int): Int {
        val maxOffset = max(0, totalCount - visibleCount.coerceIn(1, max(1, totalCount)))
        return (currentOffset + delta).coerceIn(0, maxOffset)
    }

    /** scale>1 zooms in, scale<1 zooms out, and the returned count is always valid. */
    fun zoomVisibleCount(
        currentCount: Int,
        scale: Float,
        totalCount: Int,
        minimumCount: Int = 12,
    ): Int {
        if (totalCount <= 0 || !scale.isUsable() || scale <= 0f) return 0
        val minimum = minimumCount.coerceIn(1, totalCount)
        return (currentCount / scale)
            .roundToInt()
            .coerceIn(minimum, totalCount)
    }

    fun valueToY(value: Float, range: CandleRange, top: Float, bottom: Float): Float {
        if (!value.isUsable() || range.span <= 0f || bottom <= top) return bottom
        return bottom - ((value - range.minimum) / range.span).coerceIn(0f, 1f) * (bottom - top)
    }

    fun indexToX(index: Int, window: CandleWindow, left: Float, right: Float): Float {
        if (window.count <= 1 || right <= left) return (left + right) / 2f
        return left + (index - window.startIndex).coerceIn(0, window.count - 1) * (right - left) /
            (window.count - 1)
    }

    fun indexForX(x: Float, window: CandleWindow, left: Float, right: Float): Int? {
        if (window.count <= 0 || right <= left || !x.isUsable()) return null
        if (window.count == 1) return window.startIndex
        val ratio = ((x - left) / (right - left)).coerceIn(0f, 1f)
        return (window.startIndex + ratio * (window.count - 1)).roundToInt()
            .coerceIn(window.startIndex, window.endExclusive - 1)
    }

    fun layout(
        candles: List<CandlePoint>,
        window: CandleWindow,
        range: CandleRange,
        left: Float,
        right: Float,
        top: Float,
        bottom: Float,
    ): List<CandleCoordinate> {
        if (window.count <= 0) return emptyList()
        return (window.startIndex until window.endExclusive).mapNotNull { index ->
            val candle = candles.getOrNull(index) ?: return@mapNotNull null
            if (!candle.isUsable()) return@mapNotNull null
            CandleCoordinate(
                sourceIndex = index,
                candle = candle,
                x = indexToX(index, window, left, right),
                openY = valueToY(candle.open, range, top, bottom),
                highY = valueToY(candle.high, range, top, bottom),
                lowY = valueToY(candle.low, range, top, bottom),
                closeY = valueToY(candle.close, range, top, bottom),
                volume = candle.volume.takeIf { it.isUsable() } ?: 0f,
            )
        }
    }

    fun nearestIndex(x: Float, candles: List<CandlePoint>, window: CandleWindow, left: Float, right: Float): Int? {
        val index = indexForX(x, window, left, right) ?: return null
        return (window.startIndex until window.endExclusive)
            .filter { candles.getOrNull(it)?.isUsable() == true }
            .minWithOrNull(compareBy<Int> { abs(it - index) }.thenBy { it })
    }

    private fun CandlePoint.isUsable(): Boolean =
        open.isUsable() && high.isUsable() && low.isUsable() && close.isUsable()
}
