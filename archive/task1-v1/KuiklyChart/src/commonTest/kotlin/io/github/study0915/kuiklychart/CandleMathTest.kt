package io.github.study0915.kuiklychart

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNull
import kotlin.test.assertTrue

class CandleMathTest {
    private val candles = listOf(
        CandlePoint("09:30", 10f, 12f, 9f, 11f, 100f),
        CandlePoint("09:35", 11f, 13f, 10f, 12f, 130f),
        CandlePoint("09:40", 12f, 14f, 11f, 13f, 160f),
        CandlePoint("09:45", 13f, 13.5f, 8f, 9f, 200f),
    )

    @Test
    fun emptyAndInvalidInputsAreSafe() {
        assertNull(CandleMath.computeRange(emptyList()))
        assertEquals(CandleWindow(0, 0), CandleMath.visibleWindow(0, 10))
        assertEquals(emptyList(), CandleMath.finiteIndices(listOf(CandlePoint("bad", Float.NaN, 1f, 0f, 1f, 1f))))
    }

    @Test
    fun rangeIncludesWicksAndPadsEqualValues() {
        val range = CandleMath.computeRange(candles)!!
        assertEquals(8f, range.minimum)
        assertEquals(14f, range.maximum)

        val equal = CandleMath.computeRange(listOf(CandlePoint("x", 5f, 5f, 5f, 5f, 1f)))!!
        assertTrue(equal.minimum < 5f)
        assertTrue(equal.maximum > 5f)
    }

    @Test
    fun visibleWindowAndPanAreClamped() {
        assertEquals(CandleWindow(1, 4), CandleMath.visibleWindow(6, 3, offset = 2))
        assertEquals(CandleWindow(0, 3), CandleMath.visibleWindow(6, 3, offset = 99))
        assertEquals(0, CandleMath.panOffset(0, -10, 6, 3))
        assertEquals(3, CandleMath.panOffset(0, 99, 6, 3))
    }

    @Test
    fun zoomAndCoordinateBoundariesAreStable() {
        assertEquals(12, CandleMath.zoomVisibleCount(20, 2f, 40))
        assertEquals(40, CandleMath.zoomVisibleCount(20, 0.1f, 40))
        val window = CandleMath.visibleWindow(candles.size, 3)
        assertEquals(1, CandleMath.indexForX(-100f, window, 0f, 100f))
        assertEquals(3, CandleMath.indexForX(100f, window, 0f, 100f))
        assertEquals(2, CandleMath.nearestIndex(50f, candles, window, 0f, 100f))
    }

    @Test
    fun layoutNeverProducesNonFiniteCoordinates() {
        val window = CandleMath.visibleWindow(candles.size, candles.size)
        val range = CandleMath.computeRange(candles)!!
        val points = CandleMath.layout(candles, window, range, 0f, 100f, 0f, 80f)
        assertEquals(candles.size, points.size)
        assertTrue(points.all { listOf(it.x, it.openY, it.highY, it.lowY, it.closeY).all(Float::isFinite) })
    }
}
