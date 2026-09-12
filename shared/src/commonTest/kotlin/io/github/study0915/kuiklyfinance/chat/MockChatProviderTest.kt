package io.github.study0915.kuiklyfinance.chat

import io.github.study0915.kuiklyfinance.insight.*
import io.github.study0915.kuiklyfinance.market.*
import io.github.study0915.kuiklyfinance.navigation.FinanceRoute
import kotlin.test.*

class MockChatProviderTest {
    private val provider = MockChatProvider()
    private val now = 1788764400001L
    private fun reply(q: String, context: String? = null, scenario: DemoScenario = DemoScenario.COMPLETE, attempt: Int = 0) =
        provider.reply(ChatRequest(1, 1, q, context, scenario, attempt), now)
    private fun cards(q: String, context: String? = null) = (reply(q, context) as ChatReply.Ready).blocks.filterIsInstance<AnswerBlock.EvidenceCard>()
    @Test fun comparisonUsesTwoActualSnapshotsAndIndependentFacts() {
        val cards = cards("比较 A 和 B")
        assertEquals(listOf("MOCK_A", "MOCK_B"), cards.map { it.document.key.entityId })
        val changes = cards.map { (it.document.evidence.first().fact as EvidenceFact.Return).percent }
        assertEquals(12.0, changes[0], 0.001); assertEquals(-4.0, changes[1], 0.001)
        assertEquals(1.5, (cards[0].document.evidence.last().fact as EvidenceFact.Volume).ratio, 0.001)
        assertEquals(0.8, (cards[1].document.evidence.last().fact as EvidenceFact.Volume).ratio, 0.001)
    }
    @Test fun explicitEntityOverridesFollowUpContext() {
        assertEquals("MOCK_A", cards("分析 A", "MOCK_B").single().document.key.entityId)
        assertEquals("MOCK_B", cards("这只股票有什么风险？", "MOCK_B").single().document.key.entityId)
        assertEquals("MOCK_L", cards("分析股票 L").single().document.key.entityId)
        assertEquals("MOCK_C", cards("分析 MOCK_C").single().document.key.entityId)
    }
    @Test fun unknownRealAndArbitraryTokensDoNotFabricateQuotes() {
        listOf("MOCK_Z", "查询 600519", "HTML", "hello", "今天新闻", "分析 UNKNOWN").forEach { q ->
            assertTrue(cards(q).isEmpty(), q)
        }
        assertTrue(cards("MOCK_Z", "MOCK_A").isEmpty())
    }
    @Test fun missingVolumePreservesPriceEvidenceAndSnapshotIdentity() {
        val doc = cards("如果量能缺失呢？", "MOCK_B").single().document
        assertTrue(doc.key.snapshotId.contains("missing_volume"))
        assertTrue(doc.evidence.first().available); assertFalse(doc.evidence.last().available)
        assertTrue(doc.evidence.last().reason!!.contains("2026-09-02"))
    }
    @Test fun failureRequiresRetryOfSameRequest() {
        assertIs<ChatReply.Failed>(reply("分析 B", scenario = DemoScenario.FAIL_ONCE))
        val retry = reply("分析 B", scenario = DemoScenario.FAIL_ONCE, attempt = 1) as ChatReply.Ready
        assertEquals("MOCK_B", retry.blocks.filterIsInstance<AnswerBlock.EvidenceCard>().single().document.key.entityId)
    }
    @Test fun markdownAndBusinessBlockAreDistinctWithRiskAndTime() {
        val blocks = (reply("分析 A 的风险") as ChatReply.Ready).blocks
        val text = blocks.filterIsInstance<AnswerBlock.Markdown>().single().source
        assertTrue(text.contains("历史上涨不等于未来收益")); assertTrue(text.contains("不构成投资建议"))
        val doc = blocks.filterIsInstance<AnswerBlock.EvidenceCard>().single().document
        assertEquals("2026-09-04T15:00:00+08:00", doc.document.snapshot.asOf)
    }
    @Test fun detailRouteCarriesExactDocumentAndEvidence() {
        val doc = cards("分析 B").single().document
        val route = FinanceRoute.Detail(doc.key.entityId, doc.key.snapshotId, LensFocus.EvidenceFocus("E2"), true)
        val loaded = MockMarketProvider().load(route.entityId, route.snapshotId) as MarketLoad.Ready
        val restored = EvidenceResolver.resolve(loaded.document, now)
        val state = LensState.initial(restored, route.focus)
        assertEquals(doc.key, state.documentKey); assertEquals(LensFocus.EvidenceFocus("E2"), state.focus)
        assertTrue(route.fromChat)
    }
}
