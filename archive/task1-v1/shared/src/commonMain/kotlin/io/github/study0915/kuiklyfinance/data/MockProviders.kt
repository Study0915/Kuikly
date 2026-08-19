package io.github.study0915.kuiklyfinance.data

import com.tencent.kuikly.core.nvi.serialization.json.JSONArray
import com.tencent.kuikly.core.nvi.serialization.json.JSONObject
import io.github.study0915.kuiklychart.CandlePoint
import io.github.study0915.kuiklychart.ChartPoint
import kotlin.math.abs
import kotlin.math.max
import kotlin.math.min
import kotlin.math.sqrt

class MockMarketDataSource(
    fixtureJson: String = MARKET_FIXTURE_JSON,
) : MarketDataSource {
    private val quotes: List<StockQuote> = parseQuotes(fixtureJson)

    fun quotesSnapshot(): List<StockQuote> = quotes

    fun detailSnapshot(code: String): StockDetail {
        val quote = quotes.firstOrNull { it.code == code }
            ?: throw IllegalArgumentException("Unknown stock code: $code")
        return StockDetail(
            quote = quote,
            open = quote.previousClose * if (quote.change >= 0f) 1.002f else 0.998f,
            averageVolume = (quote.volume * 0.82f).toLong(),
            market = if (code.startsWith("6")) "SSE" else "SZSE",
            description = "${quote.name} · 离线 Mock 行情",
        )
    }

    fun trendSnapshot(code: String): List<ChartPoint> {
        val quote = detailSnapshot(code).quote
        val offsets = TREND_OFFSETS[code] ?: TREND_OFFSETS.getValue("default")
        return offsets.mapIndexed { index, offset ->
            ChartPoint(
                label = "${9 + (30 + index * 15) / 60}:${(30 + index * 15) % 60}".padStart(5, '0'),
                value = quote.previousClose * (1f + offset / 100f),
            )
        }
    }

    fun candlesSnapshot(code: String): List<CandlePoint> {
        val trend = trendSnapshot(code)
        return trend.mapIndexed { index, point ->
            val previous = trend.getOrNull(index - 1)?.value ?: detailSnapshot(code).quote.previousClose
            val spread = max(0.08f, point.value * (0.002f + (index % 4) * 0.0005f))
            CandlePoint(
                label = point.label,
                open = previous,
                high = max(previous, point.value) + spread,
                low = min(previous, point.value) - spread,
                close = point.value,
                volume = detailSnapshot(code).quote.volume.toFloat() *
                    (0.55f + (index % 5) * 0.09f) / trend.size,
            )
        }
    }

    override suspend fun getQuotes(): List<StockQuote> = quotesSnapshot()

    override suspend fun getStockDetail(code: String): StockDetail = detailSnapshot(code)

    override suspend fun getTrend(code: String): List<ChartPoint> = trendSnapshot(code)

    override suspend fun getCandles(code: String): List<CandlePoint> = candlesSnapshot(code)

    private fun parseQuotes(json: String): List<StockQuote> {
        val rows = JSONObject(json).optJSONArray("quotes") ?: JSONArray()
        return buildList {
            for (index in 0 until rows.length()) {
                val row = rows.optJSONObject(index) ?: continue
                add(
                    StockQuote(
                        code = row.optString("code"),
                        name = row.optString("name"),
                        latestPrice = row.optDouble("latestPrice").toFloat(),
                        previousClose = row.optDouble("previousClose").toFloat(),
                        high = row.optDouble("high").toFloat(),
                        low = row.optDouble("low").toFloat(),
                        volume = row.optLong("volume"),
                    ),
                )
            }
        }
    }

    companion object {
        val TREND_OFFSETS = mapOf(
            "600519" to listOf(-0.32f, -0.18f, 0.05f, -0.08f, 0.22f, 0.44f, 0.36f, 0.61f, 0.77f, 0.69f, 0.92f, 1.08f, 0.96f, 1.21f, 1.37f, 1.18f),
            "000858" to listOf(0.15f, 0.31f, 0.22f, 0.46f, 0.64f, 0.53f, 0.78f, 0.95f, 0.82f, 1.04f, 1.19f, 1.31f, 1.22f, 1.48f, 1.61f, 1.72f),
            "300750" to listOf(-0.21f, -0.46f, -0.64f, -0.52f, -0.78f, -0.91f, -0.71f, -1.02f, -1.18f, -1.05f, -1.29f, -1.41f, -1.22f, -1.55f, -1.68f, -1.84f),
            "601318" to listOf(-0.12f, 0.03f, 0.19f, 0.08f, 0.28f, 0.41f, 0.35f, 0.52f, 0.67f, 0.58f, 0.74f, 0.86f, 0.79f, 0.95f, 1.03f, 1.12f),
            "default" to listOf(-0.2f, 0f, 0.15f, 0.08f, 0.3f, 0.45f, 0.4f, 0.62f),
        )

        const val MARKET_FIXTURE_JSON = """
            {
              "quotes": [
                {"code":"600519","name":"贵州茅台","latestPrice":1518.80,"previousClose":1501.10,"high":1528.60,"low":1496.20,"volume":2385400},
                {"code":"000858","name":"五粮液","latestPrice":132.64,"previousClose":130.40,"high":133.20,"low":129.86,"volume":11682400},
                {"code":"300750","name":"宁德时代","latestPrice":268.30,"previousClose":273.33,"high":274.16,"low":267.12,"volume":18420600},
                {"code":"601318","name":"中国平安","latestPrice":58.42,"previousClose":57.77,"high":58.81,"low":57.51,"volume":32691700}
              ]
            }
        """
    }
}

class MockAnalysisProvider : AnalysisProvider {
    fun analyzeSnapshot(detail: StockDetail): StockAnalysis {
        val quote = detail.quote
        val intradayRangePercent =
            if (quote.previousClose == 0f) 0f else (quote.high - quote.low) / quote.previousClose * 100f
        val volumeRatio =
            if (detail.averageVolume == 0L) 1f else quote.volume.toFloat() / detail.averageVolume
        val momentum = quote.changePercent
        val riskScore = abs(momentum) * 0.45f + intradayRangePercent * 0.4f +
            max(0f, volumeRatio - 1f) * 1.2f
        val risk = when {
            riskScore >= 3.2f -> RiskLevel.HIGH
            riskScore >= 1.6f -> RiskLevel.MEDIUM
            else -> RiskLevel.LOW
        }
        val direction = when {
            momentum > 0.35f -> "偏强"
            momentum < -0.35f -> "偏弱"
            else -> "震荡"
        }
        return StockAnalysis(
            summary = "${quote.name}当日走势$direction，Mock 模型基于涨跌幅、日内振幅与量比生成确定性摘要。",
            riskLevel = risk,
            supportLevel = quote.low,
            resistanceLevel = quote.high,
            keyPoints = listOf(
                "涨跌幅 ${format(momentum)}%",
                "日内振幅 ${format(intradayRangePercent)}%",
                "成交量为均量 ${format(volumeRatio)} 倍",
            ),
            riskNotes = buildList {
                if (intradayRangePercent > 2f) add("日内波动扩大")
                if (volumeRatio > 1.2f) add("成交量显著放大")
                if (abs(momentum) > 2f) add("短线价格变化较快")
                if (isEmpty()) add("未检测到高波动信号，仍需关注市场风险")
            },
        )
    }

    override suspend fun analyze(detail: StockDetail): StockAnalysis = analyzeSnapshot(detail)

    private fun format(value: Float): String {
        val rounded = kotlin.math.round(value * 100f) / 100f
        return rounded.toString()
    }
}
