package io.github.study0915.kuiklyfinance.data

import io.github.study0915.kuiklychart.CandlePoint
import io.github.study0915.kuiklychart.ChartPoint

data class StockQuote(
    val code: String,
    val name: String,
    val latestPrice: Float,
    val previousClose: Float,
    val high: Float,
    val low: Float,
    val volume: Long,
) {
    val change: Float get() = latestPrice - previousClose
    val changePercent: Float
        get() = if (previousClose == 0f) 0f else change / previousClose * 100f
}

data class StockDetail(
    val quote: StockQuote,
    val open: Float,
    val averageVolume: Long,
    val market: String,
    val description: String,
)

enum class RiskLevel {
    LOW,
    MEDIUM,
    HIGH,
}

data class StockAnalysis(
    val summary: String,
    val riskLevel: RiskLevel,
    val supportLevel: Float,
    val resistanceLevel: Float,
    val keyPoints: List<String>,
    val riskNotes: List<String>,
    val disclaimer: String = DISCLAIMER,
) {
    companion object {
        const val DISCLAIMER = "仅作技术演示，不构成投资建议"
    }
}

interface MarketDataSource {
    suspend fun getQuotes(): List<StockQuote>
    suspend fun getStockDetail(code: String): StockDetail
    suspend fun getTrend(code: String): List<ChartPoint>
    suspend fun getCandles(code: String): List<CandlePoint>
}

interface AnalysisProvider {
    suspend fun analyze(detail: StockDetail): StockAnalysis
}

sealed class MarketLoadState<out T> {
    data object Loading : MarketLoadState<Nothing>()
    data object Empty : MarketLoadState<Nothing>()
    data class Success<T>(val value: T) : MarketLoadState<T>()
    data class Error(val message: String, val retryable: Boolean = true) : MarketLoadState<Nothing>()
}
