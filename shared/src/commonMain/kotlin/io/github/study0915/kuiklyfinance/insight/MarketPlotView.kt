package io.github.study0915.kuiklyfinance.insight

import com.tencent.kuikly.core.base.Color
import com.tencent.kuikly.core.base.ViewContainer
import com.tencent.kuikly.core.datetime.DateTime
import com.tencent.kuikly.core.views.Canvas
import com.tencent.kuikly.core.views.CanvasContext
import com.tencent.kuikly.core.views.View
import io.github.study0915.kuiklyfinance.market.MarketBar
import kotlin.math.max
import kotlin.math.min
import kotlin.math.roundToInt

fun ViewContainer<*, *>.MarketPlot(bars: List<MarketBar>, mark: () -> PlotMark, tap: PlotTap, onInspect: (String) -> Unit) {
    View {
        val plotView = this
        var suppressClickUntil = 0L
        attr { height(MarketPlotLayout.HEIGHT); superTouch(true); accessibility("行情双图：价格与成交量，点按检视交易日") }
        event {
            touchDown {
                suppressClickUntil = DateTime.currentTimestamp() + 600
                tap.down(it.x, it.y, max(1, it.touches.size))
                if (MarketPlotLayout(plotView.frame.width, bars).hit(it.x, it.y) == null) tap.cancel()
            }
            touchMove {
                if (it.consumed) tap.cancel()
                tap.move(it.x, it.y, max(1, it.touches.size))
            }
            touchCancel { suppressClickUntil = DateTime.currentTimestamp() + 600; tap.reset() }
            touchUp {
                suppressClickUntil = DateTime.currentTimestamp() + 600
                if (it.consumed) tap.cancel()
                tap.up(it.x, it.y, MarketPlotLayout(plotView.frame.width, bars))?.let { day -> onInspect(bars[day].date) }
            }
            // Hybrid Windows devices can be classified as touch-only by the H5 renderer.
            click {
                if (DateTime.currentTimestamp() > suppressClickUntil) {
                    MarketPlotLayout(plotView.frame.width, bars).hit(it.x, it.y)?.let { day -> onInspect(bars[day].date) }
                }
            }
        }
        Canvas({ attr { height(MarketPlotLayout.HEIGHT); touchEnable(false) } }) { context, width, _ ->
            drawMarketPlot(context, MarketPlotLayout(width, bars), mark())
        }
    }
}

private val grid = Color(0xFFDFE7EBL)
private val ink = Color(0xFF183342L)
private val muted = Color(0xFF677C87L)
private val up = Color(0xFFC14D43L)
private val down = Color(0xFF188579L)
private val focus = Color(0xFF176F91L)
private fun CanvasContext.line(x1: Float, y1: Float, x2: Float, y2: Float, color: Color, width: Float = 1f) {
    beginPath(); strokeStyle(color); lineWidth(width); moveTo(x1, y1); lineTo(x2, y2); stroke()
}
private fun CanvasContext.box(x: Float, y: Float, width: Float, height: Float, color: Color) {
    beginPath(); fillStyle(color); moveTo(x, y); lineTo(x + width, y)
    lineTo(x + width, y + height); lineTo(x, y + height); closePath(); fill()
}
private fun CanvasContext.label(text: String, x: Float, y: Float, color: Color = muted, size: Float = 10f) {
    fillStyle(color); font(size); fillText(text, x, y)
}
private fun cents(value: Float): String {
    val n = value.roundToInt()
    return "${n / 100}.${(n % 100).toString().padStart(2, '0')}"
}
private fun drawMarketPlot(c: CanvasContext, g: MarketPlotLayout, mark: PlotMark) {
    if (!g.valid) return
    c.label("价格 / 元", 0f, 13f, ink, 11f)
    c.label("成交量 / 万股", 0f, 227f, ink, 11f)
    mark.range?.let { range ->
        val first = range.first.coerceIn(g.bars.indices)
        val last = range.last.coerceIn(g.bars.indices)
        val x = g.left + first * g.step
        val w = (last - first + 1) * g.step
        c.box(x, g.priceTop, w, g.priceBottom - g.priceTop, Color(0xFFE6F2F7L))
        c.box(x, g.volumeTop, w, g.volumeBottom - g.volumeTop, Color(0xFFE6F2F7L))
    }
    for (i in 0..2) {
        val value = g.minPrice + (g.maxPrice - g.minPrice) * i / 2
        val y = g.priceY(value)
        c.line(g.left, y, g.right, y, grid); c.label(cents(value), 0f, y + 3)
    }
    c.line(g.left, g.volumeBottom, g.right, g.volumeBottom, grid)
    c.label((g.maxVolume / 10_000).roundToInt().toString(), 0f, g.volumeTop + 4)
    c.label("0", 22f, g.volumeBottom + 2)
    val bodyWidth = min(9f, g.step * 0.58f)
    g.bars.forEachIndexed { index, bar ->
        val x = g.center(index)
        val color = when { bar.closeMinor > bar.openMinor -> up; bar.closeMinor < bar.openMinor -> down; else -> muted }
        c.line(x, g.priceY(bar.lowMinor.toFloat()), x, g.priceY(bar.highMinor.toFloat()), color, 1.2f)
        val top = g.priceY(max(bar.openMinor, bar.closeMinor).toFloat())
        val bottom = g.priceY(min(bar.openMinor, bar.closeMinor).toFloat())
        c.box(x - bodyWidth / 2, top, bodyWidth, max(1.5f, bottom - top), color)
        val volume = bar.volumeShares
        if (volume == null) {
            c.line(x - 2f, g.volumeBottom - 6f, x + 2f, g.volumeBottom - 2f, muted)
            c.line(x - 2f, g.volumeBottom - 2f, x + 2f, g.volumeBottom - 6f, muted)
        } else {
            c.box(x - bodyWidth / 2, g.volumeY(volume), bodyWidth, max(1f, g.volumeBottom - g.volumeY(volume)),
                if (index == mark.inspected || index == mark.target) focus else color)
        }
    }
    (mark.inspected ?: mark.target)?.takeIf { it in g.bars.indices }?.let { index ->
        val x = g.center(index)
        c.line(x, g.priceTop, x, g.priceBottom, focus, 1.5f)
        c.line(x, g.volumeTop, x, g.volumeBottom, focus, 1.5f)
        if (mark.inspected != null) {
            val y = g.priceY(g.bars[index].closeMinor.toFloat())
            c.line(g.left, y, g.right, y, focus); c.box(x - 3, y - 3, 6f, 6f, focus)
        }
    }
    mark.range?.let { range ->
        for (index in listOf(range.first, range.last).distinct().filter { it in g.bars.indices }) {
            c.box(g.center(index) - 3, g.priceY(g.bars[index].closeMinor.toFloat()) - 3, 6f, 6f, focus)
        }
    }
    c.label(g.bars.first().date.takeLast(5), g.left, 337f)
    c.label(g.bars.last().date.takeLast(5), g.right - 30f, 337f)
}
