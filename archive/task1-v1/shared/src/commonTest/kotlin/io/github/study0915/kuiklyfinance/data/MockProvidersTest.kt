package io.github.study0915.kuiklyfinance.data

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith
import kotlin.test.assertTrue

class MockProvidersTest {
    @Test
    fun marketFixtureIsOfflineAndDeterministic() {
        val source = MockMarketDataSource()
        val first = source.quotesSnapshot()
        val second = source.quotesSnapshot()
        assertEquals(first, second)
        assertEquals(4, first.size)
        assertEquals("600519", first.first().code)
    }

    @Test
    fun detailsTrendAndCandlesMatchRequestedCode() {
        val source = MockMarketDataSource()
        val detail = source.detailSnapshot("300750")
        val trend = source.trendSnapshot("300750")
        val candles = source.candlesSnapshot("300750")
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
        val first = provider.analyzeSnapshot(detail)
        val second = provider.analyzeSnapshot(detail)
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
