package io.github.study0915.kuiklyfinance.data

import kotlin.coroutines.Continuation
import kotlin.coroutines.EmptyCoroutineContext
import kotlin.coroutines.startCoroutine
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith
import kotlin.test.assertTrue

class MockProvidersTest {
    private fun <T> runSuspend(block: suspend () -> T): T {
        var result: Result<T>? = null
        block.startCoroutine(
            object : Continuation<T> {
                override val context = EmptyCoroutineContext
                override fun resumeWith(value: Result<T>) {
                    result = value
                }
            },
        )
        return result!!.getOrThrow()
    }

    @Test
    fun marketFixtureIsOfflineAndDeterministic() {
        val source = MockMarketDataSource()
        val first = runSuspend { source.getQuotes() }
        val second = runSuspend { source.getQuotes() }
        assertEquals(first, second)
        assertEquals(4, first.size)
        assertEquals("600519", first.first().code)
    }

    @Test
    fun detailsTrendAndCandlesMatchRequestedCode() {
        val source = MockMarketDataSource()
        val detail = runSuspend { source.getStockDetail("300750") }
        val trend = runSuspend { source.getTrend("300750") }
        val candles = runSuspend { source.getCandles("300750") }
        assertEquals("300750", detail.quote.code)
        assertEquals(16, trend.size)
        assertEquals(trend.size, candles.size)
        assertTrue(candles.all { it.high >= it.open && it.high >= it.close })
        assertTrue(candles.all { it.low <= it.open && it.low <= it.close })
    }

    @Test
    fun unknownCodeReturnsExplicitError() {
        assertFailsWith<IllegalArgumentException> {
            MockMarketDataSource().detailSnapshot("UNKNOWN")
        }
    }

    @Test
    fun analysisIsDeterministicAndCarriesDisclaimer() {
        val source = MockMarketDataSource()
        val detail = source.detailSnapshot("600519")
        val provider = MockAnalysisProvider()
        val first = runSuspend { provider.analyze(detail) }
        val second = runSuspend { provider.analyze(detail) }
        assertEquals(first, second)
        assertEquals(StockAnalysis.DISCLAIMER, first.disclaimer)
        assertTrue(first.keyPoints.isNotEmpty())
        assertTrue(first.riskNotes.isNotEmpty())
    }

    @Test
    fun loadStatesCoverLoadingEmptySuccessAndRetryableError() {
        val states: List<MarketLoadState<List<Int>>> = listOf(
            MarketLoadState.Loading,
            MarketLoadState.Empty,
            MarketLoadState.Success(listOf(1)),
            MarketLoadState.Error("offline"),
        )
        assertEquals(4, states.size)
        assertTrue((states.last() as MarketLoadState.Error).retryable)
    }
}
