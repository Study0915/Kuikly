package io.github.study0915.kuiklyfinance.insight

import io.github.study0915.kuiklyfinance.market.MarketBar
import kotlin.test.*

class MarketPlotLayoutTest {
    private val bars = (1..20).map { MarketBar("d$it",1000,1000,1000,1000,0) }
    @Test fun bothPanelsSelectTheSameDateAtBothWidths() {
        for (width in listOf(272f,342f)) {
            val g = MarketPlotLayout(width,bars)
            for (day in bars.indices) {
                assertEquals(day,g.hit(g.center(day),100f)); assertEquals(day,g.hit(g.center(day),270f))
            }
            assertNull(g.hit(g.right,100f)); assertNull(g.hit(g.left-1,100f))
            assertNull(g.hit(g.center(4),220f)); assertEquals(0,g.hit(g.left,100f))
        }
    }
    @Test fun flatPriceZeroVolumeAndInvalidSizesAreDefined() {
        val g = MarketPlotLayout(272f,bars)
        assertTrue(g.priceY(1000f).isFinite()); assertEquals(g.volumeBottom,g.volumeY(0))
        assertNull(MarketPlotLayout(0f,bars).hit(5f,100f))
        assertNull(MarketPlotLayout(Float.NaN,bars).hit(5f,100f))
        assertNull(MarketPlotLayout(272f,emptyList()).hit(50f,100f))
    }
    @Test fun dragBackToOriginNeverBecomesATap() {
        val g = MarketPlotLayout(272f,bars); val tap = PlotTap()
        tap.down(g.center(3),100f); tap.move(g.center(3),125f); tap.move(g.center(3),100f)
        assertNull(tap.up(g.center(3),100f,g))
    }
    @Test fun cancelAndMultitouchDoNotSelect() {
        val g = MarketPlotLayout(272f,bars); val tap = PlotTap()
        tap.down(g.center(3),100f); tap.cancel(); assertNull(tap.up(g.center(3),100f,g))
        tap.down(g.center(3),100f,2); assertNull(tap.up(g.center(3),100f,g))
        tap.down(g.center(3),100f); tap.down(g.center(4),100f); assertNull(tap.up(g.center(3),100f,g))
    }
    @Test fun tapEmitsOnceAndStateIsPerInstance() {
        val g = MarketPlotLayout(272f,bars); val a = PlotTap(); val b = PlotTap()
        a.down(g.center(3),100f); b.down(g.center(9),270f)
        assertEquals(3,a.up(g.center(3),100f,g)); assertNull(a.up(g.center(3),100f,g))
        assertEquals(9,b.up(g.center(9),270f,g))
    }
}
