package io.github.study0915.kuiklyfinance.insight

import io.github.study0915.kuiklyfinance.market.*
import kotlin.test.*

class EvidenceEdgeCasesTest {
    private val original = (MockMarketProvider().load("MOCK_A") as MarketLoad.Ready).document
    private fun resolve(bars: List<MarketBar>, evidence: List<Evidence> = original.evidence) =
        EvidenceResolver.resolve(EvidenceDocument(original.snapshot.withBars("edge", bars), evidence), 1788505200000L)

    @Test fun allMissingVolumeKeepsPriceAndDateInspection() {
        val doc = resolve(original.snapshot.bars.map { it.copy(volumeShares = null) })
        assertTrue(doc.canPlot); assertTrue(doc.evidence[0].available); assertFalse(doc.evidence[2].available)
        val layout = MarketPlotLayout(272f, doc.bars)
        assertTrue(layout.maxVolume.isFinite()); assertEquals(17, layout.hit(layout.center(17), 270f))
        assertIs<LensFocus.DayInspect>(LensState.reduce(doc, LensState.initial(doc), doc.key, LensAction.InspectDay("2026-09-02")).focus)
    }
    @Test fun zeroTargetIsAValidZeroRatioButMissingTargetIsNot() {
        val bars = original.snapshot.bars.toMutableList()
        bars[19] = bars[19].copy(volumeShares = 0)
        assertEquals(0.0, (resolve(bars).evidence[2].fact as EvidenceFact.Volume).ratio)
        bars[19] = bars[19].copy(volumeShares = null)
        assertTrue(resolve(bars).evidence[2].reason!!.contains("2026-09-04"))
    }
    @Test fun tooEarlyTargetAndReversedRangeCannotProduceFacts() {
        val items = listOf(Evidence("v", "量", EvidenceRef.VolumeRatio("2026-08-14", emptyList())),
            Evidence("r", "区间", EvidenceRef.CloseReturn("2026-09-04", "2026-08-10")))
        assertTrue(resolve(original.snapshot.bars, items).evidence.none { it.available })
    }
    @Test fun constructingSnapshotAndDocumentDoesNotRetainMutableInputs() {
        val bars = original.snapshot.bars.toMutableList(); val items = original.evidence.toMutableList()
        val snapshot = original.snapshot.withBars("copy", bars); val doc = EvidenceDocument(snapshot, items)
        bars.clear(); items.clear()
        assertEquals(20, snapshot.bars.size); assertEquals(3, doc.evidence.size)
    }
    @Test fun exactInternalBoundaryBelongsToRightSlot() {
        for (width in listOf(272f, 342f)) {
            val layout = MarketPlotLayout(width, original.snapshot.bars)
            for (i in 1..19) assertEquals(i, layout.hit(layout.left + i * layout.step, 100f), "width=$width boundary=$i")
        }
    }
    @Test fun invalidExistingFocusIsClearedWhenEvidenceDisappears() {
        val doc = resolve(original.snapshot.bars, emptyList())
        val stale = LensUiState(doc.key, LensFocus.EvidenceFocus("E2"))
        val normalized = LensState.normalize(doc, stale)
        assertEquals(LensFocus.Overview, normalized.focus); assertNotNull(normalized.notice)
        assertEquals(PlotMark(), EvidenceResolver.mark(doc, normalized.focus))
    }
}
