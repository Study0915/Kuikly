package io.github.study0915.kuiklyfinance.insight

import io.github.study0915.kuiklyfinance.market.*
import kotlin.test.*

class LensPresentationTest {
    private fun doc(id: String = "MOCK_A", scenario: DemoScenario = DemoScenario.COMPLETE) =
        EvidenceResolver.resolve((MockMarketProvider().load(id, scenario = scenario) as MarketLoad.Ready).document, 1788764400001L)

    @Test fun dateNavigationVisitsAllTradingDaysAndStopsAtWindowEdges() {
        val doc = doc(); val presenter = LensPresenter(doc)
        doc.bars.forEachIndexed { i, bar ->
            val view = presenter.present(LensState.initial(doc, LensFocus.DayInspect(bar.date)))
            assertEquals(i + 1, view.navigation!!.position)
            assertEquals(doc.bars.getOrNull(i - 1)?.date, view.navigation.previous)
            assertEquals(doc.bars.getOrNull(i + 1)?.date, view.navigation.next)
            assertEquals(i, view.mark.inspected)
        }
        val monday = presenter.present(LensState.initial(doc, LensFocus.DayInspect("2026-08-24")))
        assertEquals("2026-08-21", monday.navigation!!.previous)
    }

    @Test fun localEvidenceExposesRealEndpointsAndDifferentDayDenominator() {
        val doc = doc(); val presenter = LensPresenter(doc)
        val interval = presenter.present(LensState.initial(doc, LensFocus.EvidenceFocus("E2")))
        assertEquals("区间变化 -6.09%", interval.value)
        assertEquals("(10.80 − 11.50) ÷ 11.50 × 100%", interval.calculation)
        assertEquals(listOf("2026-08-24", "2026-08-27"), interval.links.map { (it.action as LensAction.InspectDay).date })
        val inspected = presenter.present(LensState.reduce(doc, LensState.initial(doc), doc.key, interval.links.last().action))
        assertEquals(LensFocus.DayInspect("2026-08-27"), inspected.focus)
        assertTrue(inspected.calculation.contains("相对前收"))
        assertFalse(inspected.calculation.contains("÷ 11.50"))
        assertEquals("区间内 · 中途回落 ›", inspected.related.first().label)
    }

    @Test fun volumeBreakdownAndSampleLinksComeFromResolvedFactsForBothVariants() {
        for ((id, sum, average, ratio) in listOf(listOf("MOCK_A", "600.00", "120.00", "1.50"), listOf("MOCK_B", "500.00", "100.00", "0.80"))) {
            val doc = doc(id); val view = LensPresenter(doc).present(LensState.initial(doc, LensFocus.EvidenceFocus("E3")))
            assertTrue(view.calculation.contains("样本合计 $sum 万股 ÷ 5 = $average 万股"))
            assertEquals("量能倍数 $ratio 倍", view.value)
            assertEquals(doc.bars.takeLast(6).map { it.date }, view.links.map { (it.action as LensAction.InspectDay).date })
            assertTrue(view.links.take(5).all { it.label.startsWith("样本") })
            assertTrue(view.links.last().label.startsWith("目标"))
        }
    }

    @Test fun projectionReplacesRelationsInPriorityOrderAndDropsUnavailableVolume() {
        val doc = doc("MOCK_B"); val presenter = LensPresenter(doc)
        val first = presenter.present(LensState.initial(doc, LensFocus.DayInspect("2026-09-04")))
        assertEquals(listOf("目标日", "区间内", "整体观察"), first.related.map { it.label.substringBefore(" ·") })
        val second = presenter.present(LensState.initial(doc, LensFocus.DayInspect("2026-09-02")))
        assertEquals(listOf("区间内", "比较样本", "整体观察"), second.related.map { it.label.substringBefore(" ·") })
        val missing = doc("MOCK_B", DemoScenario.MISSING_VOLUME)
        val degraded = LensPresenter(missing).present(LensState.initial(missing, LensFocus.DayInspect("2026-09-02")))
        assertTrue(degraded.detail.contains("成交量 缺失"))
        assertEquals(2, degraded.related.size)
        assertTrue(degraded.related.none { (it.action as LensAction.SelectEvidence).id == "E3" })
    }

    @Test fun unavailableFocusAndNoInsightRemainInspectableWithoutInventedFacts() {
        val missing = doc(scenario = DemoScenario.MISSING_VOLUME)
        val view = LensPresenter(missing).present(LensState.initial(missing, LensFocus.EvidenceFocus("E3")))
        assertEquals(LensFocus.Overview, view.focus); assertTrue(view.calculation.isEmpty()); assertTrue(view.links.isEmpty())
        assertTrue(view.value.contains("原选择已失效")); assertNotNull(view.navigation)
        val silent = doc(scenario = DemoScenario.NO_INSIGHT)
        val day = LensPresenter(silent).present(LensState.initial(silent, LensFocus.DayInspect(silent.bars.first().date)))
        assertTrue(day.related.isEmpty()); assertTrue(day.relationNote.contains("没有针对该日的单独解读"))
        assertTrue(day.calculation.contains("首日无前收"))
    }
}
