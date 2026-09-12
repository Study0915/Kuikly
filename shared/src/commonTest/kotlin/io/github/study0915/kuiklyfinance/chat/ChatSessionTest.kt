package io.github.study0915.kuiklyfinance.chat

import io.github.study0915.kuiklyfinance.market.DemoScenario
import kotlin.test.*

class ChatSessionTest {
    private val ready = ChatReply.Ready(listOf(AnswerBlock.Markdown("结果")))
    @Test fun rejectsBlankOversizedAndDuplicateWhilePending() {
        val session = ChatSession()
        assertNull(session.send(" \n ")); assertTrue(session.turns.isEmpty())
        assertNull(session.send("字".repeat(501)))
        val first = session.send("  分析 A  ")!!
        assertEquals("分析 A", first.question)
        assertNull(session.send("分析 B")); assertEquals(1, session.turns.size)
        assertTrue(session.complete(first, ready)); assertNotNull(session.send("分析 B"))
    }
    @Test fun cancelledReplyCannotOverwriteRetryAndRetryKeepsContext() {
        val session = ChatSession()
        val old = session.send("风险？", "MOCK_B", DemoScenario.FAIL_ONCE)!!
        session.cancel()
        assertFalse(session.complete(old, ready))
        val retry = session.retry(old.turnId)!!
        assertEquals("MOCK_B", retry.entityContext); assertEquals(old.question, retry.question)
        assertEquals(DemoScenario.FAIL_ONCE, retry.scenario); assertEquals(1, retry.attempt)
        assertFalse(session.complete(old, ready)); assertTrue(session.complete(retry, ready))
        assertEquals(1, session.turns.size); assertEquals(TurnStatus.READY, session.turns.single().status)
        assertNull(session.retry(old.turnId))
    }
    @Test fun resetInvalidatesTicketsWithoutReusingIds() {
        val session = ChatSession(); val old = session.send("分析 A")!!
        session.reset(); val fresh = session.send("分析 A")!!
        assertNotEquals(old.turnId, fresh.turnId); assertFalse(session.complete(old, ready))
        assertTrue(session.complete(fresh, ready))
    }
    @Test fun retryFailureInPlaceWithoutAppendingDuplicateQuestion() {
        val session = ChatSession(); val a = session.send("分析 A")!!
        session.complete(a, ChatReply.Failed("失败"))
        val b = session.send("分析 B")!!
        assertNull(session.retry(a.turnId)); session.complete(b, ready)
        val retry = session.retry(a.turnId)!!; session.complete(retry, ready)
        assertEquals(listOf("分析 A", "分析 B"), session.turns.map { it.request.question })
        assertEquals(2, session.turns.size)
    }
    @Test fun capRetainsAllExistingRecordsAndCanReset() {
        val session = ChatSession()
        repeat(ChatSession.MAX_TURNS) { session.complete(session.send("问题 $it")!!, ready) }
        assertNull(session.send("超过上限")); assertEquals(20, session.turns.size)
        assertEquals("问题 0", session.turns.first().request.question)
        session.reset(); assertNotNull(session.send("重新开始"))
    }
    @Test fun twoSessionInstancesDoNotShareMessages() {
        val a = ChatSession(); val b = ChatSession()
        a.send("分析 A"); assertTrue(b.turns.isEmpty()); b.reset(); assertNotNull(a.pending)
    }
}
