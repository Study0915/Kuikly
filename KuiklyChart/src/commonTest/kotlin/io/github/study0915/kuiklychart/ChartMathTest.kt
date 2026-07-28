package io.github.study0915.kuiklychart

import com.tencent.kuikly.core.base.Color
import kotlin.math.abs
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNull
import kotlin.test.assertTrue

class ChartMathTest {
    private fun series(vararg values: Float): ChartSeries =
        ChartSeries(
            name = "test",
            points = values.mapIndexed { index, value -> ChartPoint("P$index", value) },
            color = Color.BLUE,
        )

    @Test
    fun emptyDataHasNoRangeOrLayout() {
        assertNull(ChartMath.computeRange(emptyList()))
        assertTrue(ChartMath.layout(emptyList(), ChartRect(0f, 0f, 100f, 100f)).isEmpty())
    }

    @Test
    fun singlePointGetsStablePaddingAndCenteredX() {
        val input = listOf(series(42f))
        val range = ChartMath.computeRange(input)!!
        assertTrue(range.minimum < 42f)
        assertTrue(range.maximum > 42f)
        val point = ChartMath.layout(input, ChartRect(10f, 20f, 110f, 120f), range).single()
        assertEquals(60f, point.x)
        assertEquals(70f, point.y)
    }

    @Test
    fun equalValuesDoNotDivideByZero() {
        val range = ChartMath.computeRange(listOf(series(7f, 7f, 7f)))!!
        assertTrue(range.span > 0f)
        val points = ChartMath.layout(
            listOf(series(7f, 7f, 7f)),
            ChartRect(0f, 0f, 100f, 100f),
            range,
        )
        assertTrue(points.all { it.y.isFinite() })
    }

    @Test
    fun negativeAndMixedValuesMapInsidePlot() {
        val input = listOf(series(-10f, 0f, 15f))
        val plot = ChartRect(20f, 10f, 220f, 110f)
        val points = ChartMath.layout(input, plot)
        assertEquals(3, points.size)
        assertTrue(points.all { it.x in plot.left..plot.right })
        assertTrue(points.all { it.y in plot.top..plot.bottom })
        assertTrue(points[0].y > points[1].y)
        assertTrue(points[1].y > points[2].y)
    }

    @Test
    fun includeZeroExpandsPositiveRangeForBars() {
        val range = ChartMath.computeRange(listOf(series(10f, 20f)), includeZero = true)!!
        assertEquals(0f, range.minimum)
        assertEquals(20f, range.maximum)
    }

    @Test
    fun nonFiniteValuesAreIgnoredButOriginalIndexIsKept() {
        val input = listOf(series(Float.NaN, 2f, Float.POSITIVE_INFINITY, 4f))
        val points = ChartMath.layout(input, ChartRect(0f, 0f, 90f, 90f))
        assertEquals(listOf(1, 3), points.map { it.pointIndex })
        assertEquals(listOf(2f, 4f), points.map { it.value })
    }

    @Test
    fun multipleSeriesUseStableSeriesAndPointIndices() {
        val input = listOf(series(1f, 2f), series(3f, 4f))
        val points = ChartMath.layout(input, ChartRect(0f, 0f, 100f, 100f))
        assertEquals(listOf(0, 0, 1, 1), points.map { it.seriesIndex })
        assertEquals(listOf(0, 1, 0, 1), points.map { it.pointIndex })
    }

    @Test
    fun nearestPointHandlesBoundariesAndTies() {
        val points = ChartMath.layout(listOf(series(1f, 2f, 3f)), ChartRect(10f, 10f, 110f, 110f))
        assertEquals(0, ChartMath.nearestPointByX(points, -999f)?.pointIndex)
        assertEquals(2, ChartMath.nearestPointByX(points, 999f)?.pointIndex)
        assertEquals(0, ChartMath.nearestPointByX(points, 35f)?.pointIndex)
        assertNull(ChartMath.nearestPoint(points, Float.NaN, 0f))
    }

    @Test
    fun hitDistanceCanRejectFarTouches() {
        val points = ChartMath.layout(listOf(series(1f)), ChartRect(0f, 0f, 100f, 100f))
        assertNull(ChartMath.nearestPoint(points, 0f, 0f, maxDistance = 5f))
        assertEquals(0, ChartMath.nearestPoint(points, 50f, 50f, maxDistance = 1f)?.pointIndex)
    }

    @Test
    fun longLabelsAreSampledIncludingBothEdges() {
        val indices = ChartMath.sampleLabelIndices(count = 100, maxLabels = 6)
        assertEquals(6, indices.size)
        assertEquals(0, indices.first())
        assertEquals(99, indices.last())
        assertTrue(indices.zipWithNext().all { (a, b) -> b > a })
    }

    @Test
    fun ticksCoverRangeExactly() {
        val range = ChartRange(-4f, 16f)
        val ticks = ChartMath.ticks(range, 5)
        assertEquals(-4f, ticks.first())
        assertEquals(16f, ticks.last())
        assertTrue(abs(ticks[1] - 1f) < 0.0001f)
    }
}
