package io.github.study0915.kuiklyfinance.insight

import io.github.study0915.kuiklyfinance.market.MarketBar
import kotlin.math.abs
import kotlin.math.max

/** One date grid and two independent value axes. Input bars are validated upstream. */
data class MarketPlotLayout(val width: Float, val bars: List<MarketBar>) {
    val left = 42f
    val right = max(left, width - 12f)
    val plotWidth = right - left
    val priceTop = 26f
    val priceBottom = 194f
    val volumeTop = 242f
    val volumeBottom = 316f
    val valid = width.isFinite() && plotWidth > 0 && bars.isNotEmpty()
    val step = if (valid) plotWidth / bars.size else 0f
    private val rawMin = bars.minOfOrNull { it.lowMinor }?.toFloat() ?: 0f
    private val rawMax = bars.maxOfOrNull { it.highMinor }?.toFloat() ?: 0f
    private val padding = max(1f, (rawMax - rawMin) * 0.12f)
    val minPrice = rawMin - padding
    val maxPrice = rawMax + padding
    val maxVolume = max(1.0, bars.mapNotNull { it.volumeShares }.maxOrNull()?.toDouble() ?: 0.0)
    fun center(index: Int): Float = left + (index + 0.5f) * step
    fun priceY(priceMinor: Float): Float = priceBottom -
        (priceMinor - minPrice) / (maxPrice - minPrice) * (priceBottom - priceTop)
    fun volumeY(shares: Long): Float = volumeBottom -
        (shares / maxVolume * (volumeBottom - volumeTop)).toFloat()
    fun hit(x: Float, y: Float): Int? {
        if (!valid || !x.isFinite() || !y.isFinite() || x < left || x >= right) return null
        if (y !in priceTop..priceBottom && y !in volumeTop..volumeBottom) return null
        return ((x - left) / step).toInt().takeIf { it in bars.indices }
    }
    companion object { const val HEIGHT = 348f }
}

data class PlotMark(val range: IntRange? = null, val target: Int? = null, val inspected: Int? = null)

/** Maximum displacement includes a drag that later returns to the origin. */
class PlotTap(private val threshold: Float = 8f) {
    private var start: Pair<Float, Float>? = null
    private var cancelled = false
    fun down(x: Float, y: Float, pointers: Int = 1) {
        if (start != null) { cancel(); return }
        start = x to y
        cancelled = pointers > 1 || !x.isFinite() || !y.isFinite()
    }
    fun move(x: Float, y: Float, pointers: Int = 1) {
        val origin = start ?: return
        if (pointers > 1 || !x.isFinite() || !y.isFinite() ||
            abs(x - origin.first) > threshold || abs(y - origin.second) > threshold) cancel()
    }
    fun cancel() { cancelled = true }
    fun up(x: Float, y: Float, layout: MarketPlotLayout): Int? {
        move(x, y)
        val result = if (start != null && !cancelled) layout.hit(x, y) else null
        reset()
        return result
    }
    fun reset() { start = null; cancelled = false }
}
