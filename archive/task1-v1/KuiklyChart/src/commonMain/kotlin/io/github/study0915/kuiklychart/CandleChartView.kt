package io.github.study0915.kuiklychart

import com.tencent.kuikly.core.base.Color
import com.tencent.kuikly.core.base.ComposeAttr
import com.tencent.kuikly.core.base.ComposeEvent
import com.tencent.kuikly.core.base.ComposeView
import com.tencent.kuikly.core.base.ViewBuilder
import com.tencent.kuikly.core.base.ViewContainer
import com.tencent.kuikly.core.reactive.handler.observable
import com.tencent.kuikly.core.views.Canvas
import com.tencent.kuikly.core.views.CanvasContext
import com.tencent.kuikly.core.views.TextAlign
import kotlin.math.max
import kotlin.math.min
import kotlin.math.round
import kotlin.math.roundToInt

open class CandleAttr : ComposeAttr() {
    internal var candles: List<CandlePoint> by observable(emptyList())
    internal var axisVisible: Boolean by observable(true)
    internal var gridVisible: Boolean by observable(true)
    internal var volumeVisible: Boolean by observable(true)
    internal var tooltipVisible: Boolean by observable(true)
    internal var chartTheme: ChartTheme by observable(ChartTheme.FINANCE_DARK)
    internal var configuredVisibleCount: Int by observable(48)

    fun data(value: List<CandlePoint>): CandleAttr {
        candles = value
        return this
    }

    fun showAxis(value: Boolean): CandleAttr {
        axisVisible = value
        return this
    }

    fun showGrid(value: Boolean): CandleAttr {
        gridVisible = value
        return this
    }

    fun showVolume(value: Boolean): CandleAttr {
        volumeVisible = value
        return this
    }

    fun showTooltip(value: Boolean): CandleAttr {
        tooltipVisible = value
        return this
    }

    fun visibleCount(value: Int): CandleAttr {
        configuredVisibleCount = max(1, value)
        return this
    }

    fun theme(value: ChartTheme): CandleAttr {
        chartTheme = value
        return this
    }
}

class CandleEvent : ComposeEvent() {
    internal var candleSelectedHandler: ((Int, CandlePoint) -> Unit)? = null

    fun onCandleSelected(handler: (index: Int, candle: CandlePoint) -> Unit) {
        candleSelectedHandler = handler
    }
}

class CandleChartView : ComposeView<CandleAttr, CandleEvent>() {
    private var selectedIndex: Int by observable(-1)
    private var panOffset: Int by observable(0)
    private var visibleCount: Int by observable(48)
    private var panStartX = 0f
    private var panStartOffset = 0
    private var pinchStartCount = 48
    private var lastWidth = 0f
    private var lastHeight = 0f

    override fun createAttr(): CandleAttr = CandleAttr()

    override fun createEvent(): CandleEvent = CandleEvent()

    override fun body(): ViewBuilder {
        val ctx = this
        return {
            Canvas({
                attr {
                    absolutePositionAllZero()
                    backgroundColor(ctx.attr.chartTheme.background)
                }
                event {
                    click { params -> ctx.selectAt(params.x) }
                    pan { params -> ctx.handlePan(params.x, params.state == "start", params.state == "move") }
                    pinch { params -> ctx.handlePinch(params.scale, params.state == "start", params.state == "move") }
                }
            }) { canvas, width, height ->
                ctx.lastWidth = width
                ctx.lastHeight = height
                ctx.render(canvas, width, height)
            }
        }
    }

    private fun plotLeft(): Float = if (attr.axisVisible) 48f else 12f

    private fun plotRight(width: Float): Float = max(plotLeft() + 1f, width - 12f)

    private fun priceBottom(height: Float): Float =
        max(24f, height - if (attr.volumeVisible) 82f else 30f)

    private fun selectAt(x: Float) {
        val window = currentWindow() ?: return
        val index = CandleMath.nearestIndex(x, attr.candles, window, plotLeft(), plotRight(lastWidth)) ?: return
        selectedIndex = index
        attr.candles.getOrNull(index)?.let { event.candleSelectedHandler?.invoke(index, it) }
    }

    private fun handlePan(x: Float, isStart: Boolean, isMove: Boolean) {
        if (isStart) {
            panStartX = x
            panStartOffset = panOffset
        } else if (isMove) {
            val width = (plotRight(lastWidth) - plotLeft()).coerceAtLeast(1f)
            val delta = ((x - panStartX) / width * visibleCount).roundToInt()
            panOffset = CandleMath.panOffset(panStartOffset, delta, attr.candles.size, visibleCount)
            selectAt(x)
        }
    }

    private fun handlePinch(scale: Float, isStart: Boolean, isMove: Boolean) {
        if (isStart) {
            pinchStartCount = visibleCount
        } else if (isMove) {
            visibleCount = CandleMath.zoomVisibleCount(
                currentCount = pinchStartCount,
                scale = scale,
                totalCount = attr.candles.size,
            ).coerceAtLeast(1)
            panOffset = CandleMath.panOffset(panOffset, 0, attr.candles.size, visibleCount)
        }
    }

    private fun currentWindow(): CandleWindow? {
        if (attr.candles.isEmpty()) return null
        val requested = visibleCount.takeIf { it > 0 } ?: attr.configuredVisibleCount
        visibleCount = requested.coerceIn(1, attr.candles.size)
        return CandleMath.visibleWindow(attr.candles.size, visibleCount, panOffset)
    }

    private fun render(canvas: CanvasContext, width: Float, height: Float) {
        val range = CandleMath.computeRange(attr.candles)
        val window = currentWindow()
        if (range == null || window == null || width <= 0f || height <= 0f) {
            canvas.fillStyle(attr.chartTheme.text)
            canvas.textAlign(TextAlign.CENTER)
            canvas.font(14f)
            canvas.fillText("暂无 K 线数据", width / 2f, height / 2f)
            return
        }

        val left = plotLeft()
        val right = plotRight(width)
        val top = 14f
        val bottom = priceBottom(height)
        val volumeTop = bottom + 18f
        val volumeBottom = max(volumeTop + 1f, height - 26f)
        val coordinates = CandleMath.layout(attr.candles, window, range, left, right, top, bottom)
        drawGridAndAxes(canvas, range, left, right, top, bottom)
        drawCandles(canvas, coordinates, left, right)
        if (attr.volumeVisible) drawVolume(canvas, coordinates, volumeTop, volumeBottom)
        drawSelection(canvas, coordinates, left, right, top, bottom)
    }

    private fun drawGridAndAxes(canvas: CanvasContext, range: CandleRange, left: Float, right: Float, top: Float, bottom: Float) {
        val ticks = ChartMath.ticks(ChartRange(range.minimum, range.maximum))
        if (attr.gridVisible) {
            canvas.strokeStyle(attr.chartTheme.grid)
            canvas.lineWidth(1f)
            canvas.setLineDash(listOf(3f, 4f))
            ticks.forEach { tick ->
                val y = CandleMath.valueToY(tick, range, top, bottom)
                canvas.beginPath()
                canvas.moveTo(left, y)
                canvas.lineTo(right, y)
                canvas.stroke()
            }
            canvas.setLineDash(emptyList())
        }
        if (!attr.axisVisible) return
        canvas.strokeStyle(attr.chartTheme.axis)
        canvas.beginPath()
        canvas.moveTo(left, top)
        canvas.lineTo(left, bottom)
        canvas.lineTo(right, bottom)
        canvas.stroke()
        canvas.fillStyle(attr.chartTheme.text)
        canvas.font(10f)
        canvas.textAlign(TextAlign.RIGHT)
        ticks.forEach { tick ->
            val y = CandleMath.valueToY(tick, range, top, bottom)
            canvas.fillText(formatNumber(tick), left - 6f, y + 3f)
        }
    }

    private fun drawCandles(canvas: CanvasContext, coordinates: List<CandleCoordinate>, left: Float, right: Float) {
        val candleWidth = min(18f, max(2f, (right - left) / max(1, coordinates.size) * 0.62f))
        coordinates.forEach { coordinate ->
            val color = if (coordinate.candle.close >= coordinate.candle.open) attr.chartTheme.positive else attr.chartTheme.negative
            canvas.strokeStyle(color)
            canvas.lineWidth(1f)
            canvas.beginPath()
            canvas.moveTo(coordinate.x, coordinate.highY)
            canvas.lineTo(coordinate.x, coordinate.lowY)
            canvas.stroke()
            val top = min(coordinate.openY, coordinate.closeY)
            val bottom = max(coordinate.openY, coordinate.closeY)
            drawRect(canvas, coordinate.x - candleWidth / 2f, top, coordinate.x + candleWidth / 2f, max(top + 1f, bottom), color, filled = true)
        }
    }

    private fun drawVolume(canvas: CanvasContext, coordinates: List<CandleCoordinate>, top: Float, bottom: Float) {
        val maxVolume = coordinates.maxOfOrNull { it.volume }?.coerceAtLeast(1f) ?: return
        val barWidth = min(18f, max(2f, (plotRight(lastWidth) - plotLeft()) / max(1, coordinates.size) * 0.62f))
        coordinates.forEach { coordinate ->
            val color = if (coordinate.candle.close >= coordinate.candle.open) attr.chartTheme.positive else attr.chartTheme.negative
            val barTop = bottom - (coordinate.volume / maxVolume).coerceIn(0f, 1f) * (bottom - top)
            drawRect(canvas, coordinate.x - barWidth / 2f, barTop, coordinate.x + barWidth / 2f, bottom, color.opacity(0.42f), filled = true)
        }
    }

    private fun drawSelection(canvas: CanvasContext, coordinates: List<CandleCoordinate>, left: Float, right: Float, top: Float, bottom: Float) {
        val selected = coordinates.firstOrNull { it.sourceIndex == selectedIndex } ?: return
        canvas.strokeStyle(attr.chartTheme.axis)
        canvas.lineWidth(1f)
        canvas.setLineDash(listOf(4f, 3f))
        canvas.beginPath()
        canvas.moveTo(selected.x, top)
        canvas.lineTo(selected.x, bottom)
        canvas.stroke()
        canvas.setLineDash(emptyList())
        if (!attr.tooltipVisible) return
        val tooltipWidth = 142f
        val tooltipLeft = (selected.x + 8f).coerceAtMost(right - tooltipWidth).coerceAtLeast(left)
        val tooltipTop = max(top, selected.highY - 50f)
        drawRect(canvas, tooltipLeft, tooltipTop, tooltipLeft + tooltipWidth, tooltipTop + 38f, attr.chartTheme.tooltipBackground, filled = true)
        canvas.fillStyle(Color.WHITE)
        canvas.textAlign(TextAlign.LEFT)
        canvas.font(10f)
        canvas.fillText(selected.candle.label.take(16), tooltipLeft + 7f, tooltipTop + 14f)
        canvas.font(10f)
        canvas.fillText("O ${formatNumber(selected.candle.open)}  C ${formatNumber(selected.candle.close)}", tooltipLeft + 7f, tooltipTop + 29f)
    }

    private fun drawRect(canvas: CanvasContext, left: Float, top: Float, right: Float, bottom: Float, color: Color, filled: Boolean) {
        canvas.beginPath()
        canvas.moveTo(left, top)
        canvas.lineTo(right, top)
        canvas.lineTo(right, bottom)
        canvas.lineTo(left, bottom)
        canvas.closePath()
        if (filled) {
            canvas.fillStyle(color)
            canvas.fill()
        } else {
            canvas.strokeStyle(color)
            canvas.stroke()
        }
    }

    private fun formatNumber(value: Float): String = (round(value * 100f) / 100f).toString()
}

fun ViewContainer<*, *>.CandleChart(init: CandleChartView.() -> Unit) {
    addChild(CandleChartView(), init)
}
