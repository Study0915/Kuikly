package io.github.study0915.kuiklyfinance.ui

import io.github.study0915.kuiklyfinance.market.DemoScenario
import io.github.study0915.kuiklyfinance.navigation.FinanceRoute
import kotlin.test.*

class FinanceRequestsTest {
    @Test fun newerRequestAndBackBothInvalidateLateResponses() {
        val requests = FinanceRequests()
        val a = requests.begin(FinanceRoute.Detail("MOCK_A"), DemoScenario.COMPLETE, 0)
        val b = requests.begin(FinanceRoute.Detail("MOCK_B", "original"), DemoScenario.FAIL_ONCE, 0)
        assertFalse(requests.accepts(a)); assertTrue(requests.accepts(b))
        val retry = requests.begin(b.route, b.scenario, 1)
        assertFalse(requests.accepts(b)); assertEquals("original", retry.route.snapshotId)
        assertTrue(requests.accepts(retry)); requests.cancel(); assertFalse(requests.accepts(retry))
    }
}
