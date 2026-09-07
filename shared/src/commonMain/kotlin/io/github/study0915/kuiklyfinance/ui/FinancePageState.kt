package io.github.study0915.kuiklyfinance.ui

import io.github.study0915.kuiklyfinance.market.DemoScenario
import io.github.study0915.kuiklyfinance.navigation.FinanceRoute

data class FinanceRequest(val sequence: Int, val route: FinanceRoute.Detail, val scenario: DemoScenario, val attempt: Int)

/** A response may commit only while its exact request is still active in this caller. */
class FinanceRequests {
    private var sequence = 0
    private var active: FinanceRequest? = null
    fun begin(route: FinanceRoute.Detail, scenario: DemoScenario, attempt: Int): FinanceRequest =
        FinanceRequest(++sequence, route, scenario, attempt).also { active = it }
    fun accepts(request: FinanceRequest) = request == active
    fun cancel() { active = null }
}
