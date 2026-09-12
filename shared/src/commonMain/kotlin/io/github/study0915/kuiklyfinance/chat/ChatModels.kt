package io.github.study0915.kuiklyfinance.chat

import io.github.study0915.kuiklyfinance.insight.ResolvedDocument
import io.github.study0915.kuiklyfinance.market.DemoScenario

sealed class AnswerBlock {
    data class Markdown(val source: String) : AnswerBlock()
    data class EvidenceCard(val document: ResolvedDocument) : AnswerBlock()
}

data class ChatRequest(val generation: Int, val turnId: Int, val question: String,
    val entityContext: String?, val scenario: DemoScenario, val attempt: Int = 0)

sealed class ChatReply {
    data class Ready(val blocks: List<AnswerBlock>) : ChatReply()
    data class Failed(val reason: String) : ChatReply()
}

enum class TurnStatus { PENDING, READY, FAILED, CANCELLED }
data class ChatTurn(val request: ChatRequest, val status: TurnStatus = TurnStatus.PENDING,
    val blocks: List<AnswerBlock> = emptyList(), val notice: String = "")

interface ChatProvider { fun reply(request: ChatRequest, now: Long): ChatReply }
