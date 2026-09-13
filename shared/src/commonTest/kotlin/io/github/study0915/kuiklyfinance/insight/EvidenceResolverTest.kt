package io.github.study0915.kuiklyfinance.insight

import io.github.study0915.kuiklyfinance.market.*
import kotlin.test.*

class EvidenceResolverTest {
    private val provider = MockMarketProvider()
    private val now = 1788764400001L
    private fun document(id: String = "MOCK_A", scenario: DemoScenario = DemoScenario.COMPLETE) = (provider.load(id, scenario = scenario) as MarketLoad.Ready).document
    private fun resolved(id: String = "MOCK_A", scenario: DemoScenario = DemoScenario.COMPLETE) = EvidenceResolver.resolve(document(id, scenario), now)

    @Test fun goldenFactsAreCalculatedForBothEntities() {
        for ((id, expected) in listOf("MOCK_A" to listOf("+12.00%", "-6.09%", "1.50"), "MOCK_B" to listOf("-4.00%", "+5.49%", "0.80"))) {
            val d = resolved(id)
            assertEquals(expected.take(2), d.evidence.take(2).map { MarketFormatter.percent((it.fact as EvidenceFact.Return).percent) })
            val volume = d.evidence[2].fact as EvidenceFact.Volume
            assertEquals(expected[2], MarketFormatter.decimal(volume.ratio))
            assertEquals(5, volume.samples.size); assertFalse(volume.target in volume.samples)
            assertTrue(d.summary.contains(expected[2]))
        }
    }
    @Test fun missingVolumeInvalidatesOnlyDependentEvidenceAndDoesNotMutateOriginal() {
        val original = document(); val complete = EvidenceResolver.resolve(original, now)
        val missing = resolved(scenario = DemoScenario.MISSING_VOLUME)
        assertNotEquals(complete.key, missing.key)
        assertEquals(complete.evidence.take(2).map { it.fact }, missing.evidence.take(2).map { it.fact })
        assertNull(missing.evidence[2].fact); assertTrue(missing.evidence[2].reason!!.contains("2026-09-02"))
        assertFalse(missing.summary.contains("1.50")); assertTrue(original.snapshot.bars.all { it.volumeShares != null })
        assertEquals(complete.summary, resolved().summary)
    }
    @Test fun invalidMarketNeverProducesNormalPlot() {
        val d = document(); val s = d.snapshot
        val variants = listOf(s.bars.reversed(), s.bars.toMutableList().apply { this[1] = this[0] },
            s.bars.toMutableList().apply { this[0] = this[0].copy(lowMinor = 2000) },
            s.bars.toMutableList().apply { this[0] = this[0].copy(volumeShares = -1) }, s.bars.dropLast(1))
        variants.forEach { bars -> assertFalse(EvidenceResolver.resolve(EvidenceDocument(s.withBars("bad", bars), d.evidence), now).canPlot) }
        assertFalse(EvidenceResolver.resolve(EvidenceDocument(s.withBars("empty", emptyList()), d.evidence), now).canPlot)
        assertFalse(MarketTime.validDate("2026-02-29")); assertTrue(MarketTime.validDate("2024-02-29"))
    }
    @Test fun invalidReferencesDuplicatesAndZeroBaselineAreExplicit() {
        val d = document()
        val duplicate = EvidenceResolver.resolve(EvidenceDocument(d.snapshot, d.evidence + d.evidence[0]), now)
        assertTrue(duplicate.canPlot); assertTrue(duplicate.evidence.none { it.available })
        val bad = resolved(scenario = DemoScenario.BAD_REFERENCE)
        assertFalse(bad.evidence[1].available); assertTrue(bad.evidence[0].available)
        val zero = d.snapshot.withBars("zero", d.snapshot.bars.map { it.copy(volumeShares = 0) })
        assertTrue(EvidenceResolver.resolve(EvidenceDocument(zero, d.evidence), now).evidence[2].reason!!.contains("为 0"))
        val badSample = Evidence("x", "量能", EvidenceRef.VolumeRatio("2026-09-04", listOf("2026-08-10")))
        assertFalse(EvidenceResolver.resolve(EvidenceDocument(d.snapshot, listOf(badSample)), now).evidence[0].available)
    }
    @Test fun relatedEvidenceHasExplicitRolesAndStablePriority() {
        val a = resolved(); val b = resolved("MOCK_B")
        assertEquals(listOf("目标日", "整体观察"), EvidenceResolver.related(a, "2026-09-04").map { it.role })
        assertEquals(listOf("区间内", "比较样本", "整体观察"), EvidenceResolver.related(b, "2026-09-02").map { it.role })
        assertEquals(listOf("整体观察"), EvidenceResolver.related(a, "2026-08-10").map { it.role })
        assertTrue(EvidenceResolver.related(a, "2030-01-01").isEmpty())
    }
    @Test fun focusIsExclusiveAndStaleEventsCannotCrossDocumentsOrInstances() {
        val a = resolved(); val b = resolved("MOCK_B"); val first = LensState.initial(a); val second = LensState.initial(a)
        val inspect = LensState.reduce(a, first, a.key, LensAction.InspectDay("2026-08-25"))
        assertIs<LensFocus.DayInspect>(inspect.focus); assertEquals(LensFocus.EvidenceFocus("E1"), second.focus)
        assertEquals(LensFocus.EvidenceFocus("E2"), LensState.reduce(a, inspect, a.key, LensAction.SelectEvidence("E2")).focus)
        assertEquals(LensState.initial(b), LensState.reduce(b, inspect, a.key, LensAction.InspectDay("2026-08-25")))
        assertEquals(inspect, LensState.reduce(a, inspect, a.key, LensAction.SelectEvidence("unknown")))
        assertEquals(LensFocus.Overview, LensState.initial(a, LensFocus.DayInspect("bad")).focus)
        assertNotNull(LensState.initial(a, LensFocus.EvidenceFocus("bad")).notice)
        assertEquals(LensFocus.Overview, LensState.initial(resolved(scenario = DemoScenario.NO_INSIGHT)).focus)
    }
    @Test fun timeAndFormattingBoundariesAreDeterministic() {
        val t = MarketTime.epoch("2026-09-04T15:00:00+08:00")!!
        assertEquals(1788505200000L, t)
        assertEquals(Freshness.RECENT, MarketTime.freshness(t, t + MarketTime.STALE_AFTER_MS))
        assertEquals(Freshness.STALE, MarketTime.freshness(t, t + MarketTime.STALE_AFTER_MS + 1))
        assertEquals(Freshness.FUTURE, MarketTime.freshness(t, t - 1))
        assertEquals("1.01", MarketFormatter.decimal(1.005)); assertEquals("-1.01", MarketFormatter.decimal(-1.005))
        assertEquals("0.00", MarketFormatter.decimal(-0.001)); assertEquals("0.00 万股", MarketFormatter.volume(0)); assertEquals("缺失", MarketFormatter.volume(null))
        assertNull(MarketFormatter.change(null, document().snapshot.bars.first()))
    }
    @Test fun providerHasTwelveDeterministicEntitiesAndRecoverableFailures() {
        assertEquals(12, provider.entityIds.size)
        provider.entityIds.forEach { id -> assertTrue(resolved(id).canPlot); assertEquals(document(id).snapshot.bars, document(id).snapshot.bars) }
        assertIs<MarketLoad.UnknownEntity>(provider.load("MISSING"))
        assertIs<MarketLoad.SnapshotUnavailable>(provider.load("MOCK_A", "old"))
        assertIs<MarketLoad.Empty>(provider.load("MOCK_A", scenario = DemoScenario.EMPTY))
        assertIs<MarketLoad.Failed>(provider.load("MOCK_B", scenario = DemoScenario.FAIL_ONCE))
        val retry = provider.load("MOCK_B", scenario = DemoScenario.FAIL_ONCE, attempt = 1) as MarketLoad.Ready
        assertEquals("MOCK_B", retry.document.key.entityId)
    }
}
