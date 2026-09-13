package io.github.study0915.kuiklyfinance.chat

import io.github.study0915.kuiklyfinance.market.DemoScenario

/** One in-flight reply. Tickets bind retry, question, context and the session generation. */
class ChatSession {
    companion object { const val MAX_QUESTION = 500; const val MAX_TURNS = 20 }
    var turns: List<ChatTurn> = emptyList(); private set
    var pending: ChatRequest? = null; private set
    var notice = ""; private set
    private var generation = 0
    private var nextId = 1

    fun send(raw: String, context: String? = null, scenario: DemoScenario = DemoScenario.COMPLETE): ChatRequest? {
        val question = raw.trim()
        notice = when {
            pending != null -> "请等待当前回答，或先取消。"
            question.isEmpty() -> "请输入问题后发送。"
            question.length > MAX_QUESTION -> "问题最多 $MAX_QUESTION 字，请精简后发送。"
            turns.size >= MAX_TURNS -> "已达到 $MAX_TURNS 轮。请新建会话后继续，现有记录仍可查看。"
            else -> ""
        }
        if (notice.isNotEmpty()) return null
        val ticket = ChatRequest(++generation, nextId++, question, context, scenario)
        turns = turns + ChatTurn(ticket); pending = ticket
        return ticket
    }

    fun accepts(ticket: ChatRequest) = ticket == pending
    fun complete(ticket: ChatRequest, reply: ChatReply): Boolean {
        if (!accepts(ticket)) return false
        turns = turns.map { turn -> if (turn.request != ticket) turn else when (reply) {
            is ChatReply.Ready -> turn.copy(status = TurnStatus.READY, blocks = reply.blocks.toList())
            is ChatReply.Failed -> turn.copy(status = TurnStatus.FAILED, notice = reply.reason)
        } }
        pending = null; notice = ""
        return true
    }

    fun cancel() {
        val ticket = pending ?: return
        turns = turns.map { if (it.request == ticket) it.copy(status = TurnStatus.CANCELLED, notice = "已取消，可重试此问题。") else it }
        pending = null; generation++; notice = ""
    }

    fun retry(turnId: Int): ChatRequest? {
        if (pending != null) { notice = "请等待当前回答，或先取消。"; return null }
        val original = turns.firstOrNull { it.request.turnId == turnId && it.status in listOf(TurnStatus.FAILED, TurnStatus.CANCELLED) } ?: return null
        val ticket = original.request.copy(generation = ++generation, attempt = original.request.attempt + 1)
        turns = turns.map { if (it == original) ChatTurn(ticket) else it }
        pending = ticket; notice = ""
        return ticket
    }

    fun reset() { generation++; turns = emptyList(); pending = null; notice = "" }
}
