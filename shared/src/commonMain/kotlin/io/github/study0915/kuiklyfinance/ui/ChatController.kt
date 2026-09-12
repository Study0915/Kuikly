package io.github.study0915.kuiklyfinance.ui

import com.tencent.kuikly.core.reactive.handler.observable
import com.tencent.kuikly.core.reactive.handler.observableList
import com.tencent.kuikly.core.reactive.collection.ObservableList
import io.github.study0915.kuiklyfinance.chat.*
import io.github.study0915.kuiklyfinance.market.DemoScenario
import io.github.study0915.kuiklyfinance.insight.DocumentKey

internal class AnswerCardState { var expanded by observable(false) }

/** Pager-owned UI projection; leaving chat to inspect a quote does not destroy the session. */
internal class ChatController {
    val session = ChatSession()
    var turns: ObservableList<ChatTurn> by observableList()
    var draft by observable("")
    var notice by observable("")
    var pending by observable(false)
    var scenario by observable(DemoScenario.COMPLETE)
    var offset = 0f
    var jumpToLatest: (() -> Unit)? = null
    private var viewGeneration = 0
    private val cards = mutableMapOf<Pair<Int, DocumentKey>, AnswerCardState>()
    fun cardState(turn: Int, key: DocumentKey) = cards.getOrPut(turn to key) { AnswerCardState() }
    fun attachView(): Int { detachView(); return viewGeneration }
    fun acceptsView(generation: Int) = generation == viewGeneration
    fun currentView() = viewGeneration
    fun detachView() { viewGeneration++; jumpToLatest = null }

    fun sync() {
        if (session.turns.isEmpty()) turns.clear()
        else session.turns.forEachIndexed { index, turn ->
            if (index >= turns.size) turns.add(turn) else if (turns[index] != turn) turns[index] = turn
        }
        notice = session.notice; pending = session.pending != null
    }
    fun begin(question: String, entity: String? = null, fromDraft: Boolean = false): ChatRequest? {
        val ticket = session.send(question, entity, scenario)
        if (ticket != null && fromDraft) draft = ""
        sync(); return ticket
    }
    fun reset() { session.reset(); cards.clear(); draft = ""; offset = 0f; sync() }
}
