package io.github.study0915.kuiklychart

import com.tencent.kuikly.core.base.Color

data class ChartPoint(
    val label: String,
    val value: Float,
)

data class ChartSeries(
    val name: String,
    val points: List<ChartPoint>,
    val color: Color,
)

data class ChartSelection(
    val seriesIndex: Int,
    val pointIndex: Int,
    val label: String,
    val value: Float,
)

data class CandlePoint(
    val label: String,
    val open: Float,
    val high: Float,
    val low: Float,
    val close: Float,
    val volume: Float,
)

enum class ChartTheme(
    val background: Color,
    val grid: Color,
    val axis: Color,
    val text: Color,
    val tooltipBackground: Color,
    val positive: Color,
    val negative: Color,
) {
    FINANCE_DARK(
        background = Color(0xFF07111FL),
        grid = Color(0xFF1C2A3BL),
        axis = Color(0xFF506078L),
        text = Color(0xFFB8C5D9L),
        tooltipBackground = Color(0xEE15243AL),
        positive = Color(0xFF20C997L),
        negative = Color(0xFFFF5A6FL),
    ),
    FINANCE_LIGHT(
        background = Color(0xFFF7F9FCL),
        grid = Color(0xFFE5EAF1L),
        axis = Color(0xFF95A1B3L),
        text = Color(0xFF344054L),
        tooltipBackground = Color(0xEEFFFFFFL),
        positive = Color(0xFF008A68L),
        negative = Color(0xFFD9364FL),
    ),
}

internal fun Float.isUsable(): Boolean = !isNaN() && !isInfinite()
