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

open class ChartAttr : ComposeAttr() {
    internal var chartSeries: List<ChartSeries> by observable(emptyList())
    internal var axisVisible: Boolean by observable(true)
    internal var gridVisible: Boolean by observable(true)
    internal var dotsVisible: Boolean by observable(true)
    internal var tooltipVisible: Boolean by observable(true)
    internal var valueLabelsVisible: Boolean by observable(false)
    internal var areaFillVisible: Boolean by observable(false)
    internal var chartTheme: ChartTheme by observable(ChartTheme.FINANCE_DARK)
    internal var configuredBarColors: List<Color> by observable(emptyList())
    internal var configuredCornerRadius: Float by observable(4f)

    fun data(series: List<ChartSeries>): ChartAttr {
        chartSeries = series
        return this
    }

    fun showAxis(value: Boolean): ChartAttr {
        axisVisible = value
        return this
    }

    fun showGrid(value: Boolean): ChartAttr {
        gridVisible = value
        return this
    }

    fun showDots(value: Boolean): ChartAttr {
        dotsVisible = value
        return this
    }

    fun showTooltip(value: Boolean): ChartAttr {
        tooltipVisible = value
        return this
    }

    fun showValueLabels(value: Boolean): ChartAttr {
        valueLabelsVisible = value
        return this
    }

    fun areaFill(value: Boolean): ChartAttr {
        areaFillVisible = value
        return this
    }

    fun theme(value: ChartTheme): ChartAttr {
        chartTheme = value
        return this
    }

    fun barColors(value: List<Color>): ChartAttr {
        configuredBarColors = value
        return this
    }

    fun cornerRadius(value: Float): ChartAttr {
        configuredCornerRadius = max(0f, value)
        return this
    }
}

class ChartEvent : ComposeEvent() {
    internal var pointSelectedHandler: ((ChartSelection) -> Unit)? = null

    fun onPointSelected(handler: (ChartSelection) -> Unit) {
        pointSelectedHandler = handler
    }
}

enum class ChartKind {
    LINE,
    BAR,
}

abstract class BaseChartView(
    private val kind: ChartKind,
) : ComposeView<ChartAttr, ChartEvent>() {
    private var selectedSeriesIndex: Int by observable(-1)
    private var selectedPointIndex: Int by observable(-1)
    private var lastWidth = 0f
    private var lastHeight = 0f

    override fun createAttr(): ChartAttr = ChartAttr()

    override fun createEvent(): ChartEvent = ChartEvent()

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
                    pan { params -> ctx.selectAt(params.x) }
                }
            }) { canvas, width, height ->
                ctx.lastWidth = width
                ctx.lastHeight = height
                ctx.render(canvas, width, height)
            }
        }
    }

    private fun plotRect(width: Float, height: Float): ChartRect =
        ChartRect(
            left = if (attr.axisVisible) 44f else 12f,
            top = 14f,
            right = max(if (attr.axisVisible) 45f else 13f, width - 12f),
            bottom = max(15f, height - if (attr.axisVisible) 30f else 14f),
        )

    private fun dataRange(): ChartRange? =
        ChartMath.computeRange(attr.chartSeries, includeZero = kind == ChartKind.BAR)

    private fun selectAt(x: Float) {
        val range = dataRange() ?: return
        val coordinates = ChartMath.layout(attr.chartSeries, plotRect(lastWidth, lastHeight), range)
        val nearest = ChartMath.nearestPointByX(coordinates, x) ?: return
        selectedSeriesIndex = nearest.seriesIndex
        selectedPointIndex = nearest.pointIndex
        event.pointSelectedHandler?.invoke(
            ChartSelection(
                seriesIndex = nearest.seriesIndex,
                pointIndex = nearest.pointIndex,
                label = nearest.label,
                value = nearest.value,
            ),
        )
    }

    private fun render(canvas: CanvasContext, width: Float, height: Float) {
        val theme = attr.chartTheme
        val range = dataRange()
        if (range == null || width <= 0f || height <= 0f) {
            canvas.fillStyle(theme.text)
            canvas.textAlign(TextAlign.CENTER)
            canvas.font(14f)
            canvas.fillText("暂无数据", width / 2f, height / 2f)
            return
        }

        val plot = plotRect(width, height)
        val coordinates = ChartMath.layout(attr.chartSeries, plot, range)
        drawGridAndAxes(canvas, plot, range)
        if (kind == ChartKind.LINE) {
            drawLines(canvas, plot, coordinates)
        } else {
            drawBars(canvas, plot, range)
        }
        drawSelection(canvas, plot, coordinates)
    }

    private fun drawGridAndAxes(canvas: CanvasContext, plot: ChartRect, range: ChartRange) {
        val theme = attr.chartTheme
        val ticks = ChartMath.ticks(range)
        if (attr.gridVisible) {
            canvas.strokeStyle(theme.grid)
            canvas.lineWidth(1f)
            canvas.setLineDash(listOf(3f, 4f))
            ticks.forEach { tick ->
                val y = ChartMath.valueToY(tick, range, plot)
                canvas.beginPath()
                canvas.moveTo(plot.left, y)
                canvas.lineTo(plot.right, y)
                canvas.stroke()
            }
            canvas.setLineDash(emptyList())
        }

        if (!attr.axisVisible) return
        canvas.strokeStyle(theme.axis)
        canvas.lineWidth(1f)
        canvas.beginPath()
        canvas.moveTo(plot.left, plot.top)
        canvas.lineTo(plot.left, plot.bottom)
        canvas.lineTo(plot.right, plot.bottom)
        canvas.stroke()

        canvas.fillStyle(theme.text)
        canvas.font(10f)
        canvas.textAlign(TextAlign.RIGHT)
        ticks.forEach { tick ->
            val y = ChartMath.valueToY(tick, range, plot)
            canvas.fillText(formatNumber(tick), plot.left - 6f, y + 3f)
        }

        val labels = attr.chartSeries.firstOrNull { it.points.isNotEmpty() }?.points.orEmpty()
        canvas.textAlign(TextAlign.CENTER)
        ChartMath.sampleLabelIndices(labels.size, 6).forEach { index ->
            canvas.fillText(
                labels[index].label.take(10),
                ChartMath.indexToX(index, labels.size, plot),
                plot.bottom + 16f,
            )
        }
    }

    private fun drawLines(
        canvas: CanvasContext,
        plot: ChartRect,
        coordinates: List<ChartCoordinate>,
    ) {
        attr.chartSeries.forEachIndexed { seriesIndex, series ->
            val points = coordinates.filter { it.seriesIndex == seriesIndex }
            if (points.isEmpty()) return@forEachIndexed
            val color = series.color

            if (attr.areaFillVisible && points.size > 1) {
                canvas.beginPath()
                canvas.moveTo(points.first().x, plot.bottom)
                points.forEach { canvas.lineTo(it.x, it.y) }
                canvas.lineTo(points.last().x, plot.bottom)
                canvas.closePath()
                canvas.fillStyle(color.opacity(0.16f))
                canvas.fill()
            }

            canvas.beginPath()
            points.forEachIndexed { index, point ->
                if (index == 0) canvas.moveTo(point.x, point.y) else canvas.lineTo(point.x, point.y)
            }
            canvas.strokeStyle(color)
            canvas.lineWidth(2.25f)
            canvas.lineCapRound()
            canvas.stroke()

            if (attr.dotsVisible) {
                points.forEach { point ->
                    canvas.beginPath()
                    canvas.arc(point.x, point.y, 3f, 0f, FULL_CIRCLE, false)
                    canvas.fillStyle(color)
                    canvas.fill()
                }
            }
        }
    }

    private fun drawBars(canvas: CanvasContext, plot: ChartRect, range: ChartRange) {
        val maxPointCount = attr.chartSeries.maxOfOrNull { it.points.size } ?: return
        if (maxPointCount == 0) return
        val seriesCount = max(1, attr.chartSeries.size)
        val groupWidth = plot.width / maxPointCount
        val barWidth = min(24f, max(2f, groupWidth * 0.72f / seriesCount))
        val zeroY = ChartMath.valueToY(0f.coerceIn(range.minimum, range.maximum), range, plot)

        attr.chartSeries.forEachIndexed { seriesIndex, series ->
            val color = attr.configuredBarColors.getOrNull(seriesIndex) ?: series.color
            series.points.forEachIndexed { pointIndex, point ->
                if (!point.value.isUsable()) return@forEachIndexed
                val center = plot.left + groupWidth * (pointIndex + 0.5f)
                val groupStart = center - barWidth * seriesCount / 2f
                val left = groupStart + barWidth * seriesIndex
                val valueY = ChartMath.valueToY(point.value, range, plot)
                val top = min(valueY, zeroY)
                val bottom = max(valueY, zeroY)
                drawRoundedRect(
                    canvas = canvas,
                    left = left,
                    top = top,
                    right = left + barWidth * 0.84f,
                    bottom = max(top + 1f, bottom),
                    radius = min(attr.configuredCornerRadius, barWidth * 0.25f),
                    color = color,
                )
                if (attr.valueLabelsVisible) {
                    canvas.fillStyle(attr.chartTheme.text)
                    canvas.textAlign(TextAlign.CENTER)
                    canvas.font(9f)
                    canvas.fillText(formatNumber(point.value), left + barWidth * 0.42f, top - 4f)
                }
            }
        }
    }

    private fun drawSelection(
        canvas: CanvasContext,
        plot: ChartRect,
        coordinates: List<ChartCoordinate>,
    ) {
        val selected = coordinates.firstOrNull {
            it.seriesIndex == selectedSeriesIndex && it.pointIndex == selectedPointIndex
        } ?: return
        val theme = attr.chartTheme

        canvas.strokeStyle(theme.axis)
        canvas.lineWidth(1f)
        canvas.setLineDash(listOf(4f, 3f))
        canvas.beginPath()
        canvas.moveTo(selected.x, plot.top)
        canvas.lineTo(selected.x, plot.bottom)
        canvas.moveTo(plot.left, selected.y)
        canvas.lineTo(plot.right, selected.y)
        canvas.stroke()
        canvas.setLineDash(emptyList())

        canvas.beginPath()
        canvas.arc(selected.x, selected.y, 5f, 0f, FULL_CIRCLE, false)
        canvas.fillStyle(attr.chartSeries[selected.seriesIndex].color)
        canvas.fill()

        if (!attr.tooltipVisible) return
        val tooltipWidth = 116f
        val tooltipHeight = 36f
        val tooltipLeft = (selected.x + 8f).coerceAtMost(plot.right - tooltipWidth)
            .coerceAtLeast(plot.left)
        val tooltipTop = (selected.y - tooltipHeight - 8f).coerceAtLeast(plot.top)
        drawRoundedRect(
            canvas,
            tooltipLeft,
            tooltipTop,
            tooltipLeft + tooltipWidth,
            tooltipTop + tooltipHeight,
            6f,
            theme.tooltipBackground,
        )
        canvas.fillStyle(Color.WHITE)
        canvas.textAlign(TextAlign.LEFT)
        canvas.font(10f)
        canvas.fillText(selected.label.take(16), tooltipLeft + 8f, tooltipTop + 14f)
        canvas.font(12f)
        canvas.fillText(formatNumber(selected.value), tooltipLeft + 8f, tooltipTop + 29f)
    }

    private fun drawRoundedRect(
        canvas: CanvasContext,
        left: Float,
        top: Float,
        right: Float,
        bottom: Float,
        radius: Float,
        color: Color,
    ) {
        val safeRadius = min(max(0f, radius), min((right - left) / 2f, (bottom - top) / 2f))
        canvas.beginPath()
        canvas.moveTo(left + safeRadius, top)
        canvas.lineTo(right - safeRadius, top)
        canvas.quadraticCurveTo(right, top, right, top + safeRadius)
        canvas.lineTo(right, bottom - safeRadius)
        canvas.quadraticCurveTo(right, bottom, right - safeRadius, bottom)
        canvas.lineTo(left + safeRadius, bottom)
        canvas.quadraticCurveTo(left, bottom, left, bottom - safeRadius)
        canvas.lineTo(left, top + safeRadius)
        canvas.quadraticCurveTo(left, top, left + safeRadius, top)
        canvas.closePath()
        canvas.fillStyle(color)
        canvas.fill()
    }

    private fun formatNumber(value: Float): String {
        val rounded = round(value * 100f) / 100f
        return rounded.toString()
    }

    private companion object {
        const val FULL_CIRCLE = 6.2831855f
    }
}

class LineChartView : BaseChartView(ChartKind.LINE)

class BarChartView : BaseChartView(ChartKind.BAR)

fun ViewContainer<*, *>.LineChart(init: LineChartView.() -> Unit) {
    addChild(LineChartView(), init)
}

fun ViewContainer<*, *>.BarChart(init: BarChartView.() -> Unit) {
    addChild(BarChartView(), init)
}
