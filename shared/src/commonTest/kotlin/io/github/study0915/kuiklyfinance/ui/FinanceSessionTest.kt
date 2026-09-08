package io.github.study0915.kuiklyfinance.ui

import io.github.study0915.kuiklyfinance.insight.*
import io.github.study0915.kuiklyfinance.market.*
import io.github.study0915.kuiklyfinance.navigation.FinanceRoute
import kotlin.test.*

class FinanceSessionTest {
    private val provider = MockMarketProvider()
    private fun document(id: String = "MOCK_A", scenario: DemoScenario = DemoScenario.COMPLETE) =
        EvidenceResolver.resolve((provider.load(id, scenario = scenario) as MarketLoad.Ready).document, 1788764400001L)
    private fun open(session: FinanceSession, id: String = "MOCK_A"): FinanceContent {
        val ticket = session.begin(FinanceRoute.Detail(id), DemoScenario.COMPLETE, 0)
        return session.complete(ticket, document(id))!!
    }

    @Test fun oldCardCannotResetCurrentDocumentOrAnotherMountOfSameDocument() {
        val session = FinanceSession(); val a = open(session); val b = open(session, "MOCK_B")
        assertSame(b, session.dispatch(a.request, LensAction.InspectDay("2026-08-25")))
        val reopened = open(session)
        assertSame(reopened, session.dispatch(a.request, LensAction.SelectEvidence("E2")))
        assertEquals(LensFocus.EvidenceFocus("E1"), session.content!!.lens.focus)
    }

    @Test fun pendingLoadAndBackRejectOldEventsAndResponses() {
        val session = FinanceSession(); val a = open(session)
        val b = session.begin(FinanceRoute.Detail("MOCK_B"), DemoScenario.COMPLETE, 0)
        assertNull(session.dispatch(a.request, LensAction.ReturnToSummary))
        assertNull(session.complete(a.request, a.document))
        session.cancel(); assertNull(session.complete(b, document("MOCK_B")))
        assertNull(session.content)
    }

    @Test fun mountedSessionUsesLatestSelectionAndKeepsOtherCallerIndependent() {
        val first = FinanceSession(); val second = FinanceSession()
        val a = open(first); val independent = open(second)
        first.dispatch(a.request, LensAction.InspectDay("2026-08-25"))
        val updated = first.dispatch(a.request, LensAction.SelectEvidence("E2"))!!
        assertEquals(LensFocus.EvidenceFocus("E2"), updated.lens.focus)
        assertSame(independent, second.content)
        assertEquals(updated.document.key, updated.lens.documentKey)
    }

    @Test fun explicitMissingSnapshotRestoresDayAndScenarioWithNoStaleRatio() {
        val doc = document(scenario = DemoScenario.MISSING_VOLUME)
        assertEquals(DemoScenario.MISSING_VOLUME, provider.scenarioForSnapshot("MOCK_A", doc.key.snapshotId))
        assertNull(provider.scenarioForSnapshot("MOCK_B", doc.key.snapshotId))
        val session = FinanceSession()
        val ticket = session.begin(FinanceRoute.Detail("MOCK_A", doc.key.snapshotId, LensFocus.DayInspect("2026-09-02")), DemoScenario.MISSING_VOLUME, 0)
        assertNull(session.complete(ticket, document()))
        val content = session.complete(ticket, doc)!!
        assertEquals(LensFocus.DayInspect("2026-09-02"), content.lens.focus)
        assertFalse(content.document.summary.contains("1.50"))
    }
}
