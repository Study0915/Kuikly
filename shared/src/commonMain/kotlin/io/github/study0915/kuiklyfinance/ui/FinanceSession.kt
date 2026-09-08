package io.github.study0915.kuiklyfinance.ui

import io.github.study0915.kuiklyfinance.insight.*
import io.github.study0915.kuiklyfinance.market.*
import io.github.study0915.kuiklyfinance.navigation.FinanceRoute

data class FinanceContent(val request: FinanceRequest, val document: ResolvedDocument, val lens: LensUiState)

/** One caller owns one session. View callbacks carry a mount's request, not just a document key. */
class FinanceSession {
    private val requests = FinanceRequests()
    var content: FinanceContent? = null
        private set

    fun begin(route: FinanceRoute.Detail, scenario: DemoScenario, attempt: Int): FinanceRequest {
        content = null
        return requests.begin(route, scenario, attempt)
    }

    fun accepts(request: FinanceRequest) = requests.accepts(request)

    fun complete(request: FinanceRequest, document: ResolvedDocument): FinanceContent? {
        if (!accepts(request) || document.error != null || document.key.entityId != request.route.entityId ||
            (request.route.snapshotId != null && document.key.snapshotId != request.route.snapshotId)) return null
        return FinanceContent(request, document, LensState.initial(document, request.route.focus)).also { content = it }
    }

    fun dispatch(origin: FinanceRequest, action: LensAction): FinanceContent? {
        val current = content ?: return null
        if (!accepts(origin) || current.request != origin) return current
        val next = LensState.reduce(current.document, current.lens, current.document.key, action)
        return if (next == current.lens) current else current.copy(lens = next).also { content = it }
    }

    fun cancel() { requests.cancel(); content = null }
}
