package io.github.study0915.kuiklyfinance.ui

import com.tencent.kuikly.core.annotations.Page
import com.tencent.kuikly.core.base.Border
import com.tencent.kuikly.core.base.BorderStyle
import com.tencent.kuikly.core.base.Color
import com.tencent.kuikly.core.base.ViewBuilder
import com.tencent.kuikly.core.base.ViewContainer
import com.tencent.kuikly.core.module.RouterModule
import com.tencent.kuikly.core.nvi.serialization.json.JSONObject
import com.tencent.kuikly.core.pager.Pager
import com.tencent.kuikly.core.reactive.handler.observable
import com.tencent.kuikly.core.views.List
import com.tencent.kuikly.core.views.Text
import com.tencent.kuikly.core.views.View
import io.github.study0915.kuiklychart.CandleChart
import io.github.study0915.kuiklychart.ChartPoint
import io.github.study0915.kuiklychart.ChartSeries
import io.github.study0915.kuiklychart.ChartTheme
import io.github.study0915.kuiklychart.LineChart
import io.github.study0915.kuiklyfinance.data.MockAnalysisProvider
import io.github.study0915.kuiklyfinance.data.MockMarketDataSource
import io.github.study0915.kuiklyfinance.data.RiskLevel
import io.github.study0915.kuiklyfinance.data.StockAnalysis
import io.github.study0915.kuiklyfinance.data.StockQuote
import kotlin.math.abs
import kotlin.math.round

private object FinancePalette {
    val background = Color(0xFF07111FL)
    val panel = Color(0xFF0D1A2AL)
    val panelRaised = Color(0xFF122237L)
    val rule = Color(0xFF1E3047L)
    val text = Color(0xFFF0F4F8L)
    val muted = Color(0xFF8EA0B8L)
    val positive = Color(0xFF20C997L)
    val negative = Color(0xFFFF5A6FL)
    val amber = Color(0xFFFFC857L)
    val blue = Color(0xFF5EA3FFL)
}

private fun Float.pretty(decimals: Int = 2): String {
    val factor = if (decimals == 0) 1f else 100f
    return (round(this * factor) / factor).toString()
}

private fun StockQuote.changeText(): String {
    val prefix = if (change >= 0f) "+" else ""
    return "$prefix${change.pretty()}  $prefix${changePercent.pretty()}%"
}

private fun StockQuote.changeColor(): Color =
    if (change >= 0f) FinancePalette.positive else FinancePalette.negative

@Page("finance_home", supportInLocal = true)
class FinanceHomePage : Pager() {
    private val market = MockMarketDataSource()
    private val quotes = market.quotesSnapshot()
    private var selectedSummary: String by observable("拖动图表查看时间点")

    override fun body(): ViewBuilder {
        val ctx = this
        val demoState = pageData.params.optString("demoState").ifEmpty { "success" }
        return {
            val container = this
            attr {
                backgroundColor(FinancePalette.background)
            }
            if (demoState == "success") {
                List {
                    attr {
                        flex(1f)
                    }
                    View {
                        attr {
                            padding(left = 20f, right = 20f, top = 22f, bottom = 18f)
                        }
                        Text {
                            attr {
                                text("KUIKLY / MARKET LAB")
                                color(FinancePalette.amber)
                                fontFamily("monospace")
                                fontSize(11f)
                                letterSpacing(1.8f)
                            }
                        }
                        Text {
                            attr {
                                text("今日行情")
                                color(FinancePalette.text)
                                fontSize(30f)
                                fontWeight700()
                                marginTop(8f)
                            }
                        }
                        Text {
                            attr {
                                text("离线快照 · Mock 数据 · 2026/07/24 15:00")
                                color(FinancePalette.muted)
                                fontFamily("monospace")
                                fontSize(12f)
                                marginTop(7f)
                            }
                        }
                    }

                    View {
                        attr {
                            margin(left = 16f, right = 16f)
                            padding(16f)
                            backgroundColor(FinancePalette.panelRaised)
                            borderRadius(14f)
                        }
                        View {
                            attr {
                                flexDirectionRow()
                                justifyContentSpaceBetween()
                                alignItemsCenter()
                            }
                            View {
                                Text {
                                    attr {
                                        text("市场温度")
                                        color(FinancePalette.muted)
                                        fontSize(12f)
                                    }
                                }
                                Text {
                                    attr {
                                        text("61 / 100")
                                        color(FinancePalette.text)
                                        fontFamily("monospace")
                                        fontSize(25f)
                                        fontWeight700()
                                        marginTop(5f)
                                    }
                                }
                            }
                            View {
                                attr {
                                    padding(left = 12f, right = 12f, top = 7f, bottom = 7f)
                                    backgroundColor(Color(0x2220C997L))
                                    borderRadius(20f)
                                }
                                Text {
                                    attr {
                                        text("温和偏强")
                                        color(FinancePalette.positive)
                                        fontSize(12f)
                                        fontWeight600()
                                    }
                                }
                            }
                        }
                        View {
                            attr {
                                height(1f)
                                backgroundColor(FinancePalette.rule)
                                marginTop(14f)
                                marginBottom(12f)
                            }
                        }
                        Text {
                            attr {
                                text("4 个样本中 3 个上涨 · 数据仅用于组件演示")
                                color(FinancePalette.muted)
                                fontSize(12f)
                            }
                        }
                    }

                    View {
                        attr {
                            margin(left = 16f, right = 16f, top = 14f)
                            padding(top = 15f, bottom = 12f)
                            height(250f)
                            backgroundColor(FinancePalette.panel)
                            borderRadius(14f)
                            overflow(true)
                        }
                        Text {
                            attr {
                                text("贵州茅台 / 日内分时")
                                color(FinancePalette.text)
                                fontSize(14f)
                                fontWeight600()
                                marginLeft(15f)
                            }
                        }
                        Text {
                            attr {
                                text(ctx.selectedSummary)
                                color(FinancePalette.muted)
                                fontFamily("monospace")
                                fontSize(11f)
                                margin(left = 15f, top = 5f)
                            }
                        }
                        LineChart {
                            attr {
                                height(190f)
                                marginTop(8f)
                                data(
                                    listOf(
                                        ChartSeries(
                                            "600519",
                                            ctx.market.trendSnapshot("600519"),
                                            FinancePalette.positive,
                                        ),
                                    ),
                                )
                                theme(ChartTheme.FINANCE_DARK)
                                showDots(false)
                                showTooltip(true)
                                areaFill(true)
                            }
                            event {
                                onPointSelected {
                                    ctx.selectedSummary = "${it.label}  ¥${it.value.pretty()}"
                                }
                            }
                        }
                    }

                    View {
                        attr {
                            padding(left = 20f, right = 20f, top = 24f, bottom = 9f)
                            flexDirectionRow()
                            justifyContentSpaceBetween()
                        }
                        Text {
                            attr {
                                text("观察列表")
                                color(FinancePalette.text)
                                fontSize(18f)
                                fontWeight700()
                            }
                        }
                        Text {
                            attr {
                                text("最新价 / 涨跌")
                                color(FinancePalette.muted)
                                fontSize(11f)
                            }
                        }
                    }

                    ctx.quotes.forEachIndexed { index, quote ->
                        View {
                            attr {
                                margin(left = 16f, right = 16f)
                                padding(left = 4f, right = 4f, top = 15f, bottom = 15f)
                                flexDirectionRow()
                                alignItemsCenter()
                                justifyContentSpaceBetween()
                                if (index < ctx.quotes.lastIndex) {
                                    borderBottom(
                                        Border(1f, BorderStyle.SOLID, FinancePalette.rule),
                                    )
                                }
                            }
                            event {
                                click {
                                    val data = JSONObject()
                                    data.put("code", quote.code)
                                    ctx.acquireModule<RouterModule>(RouterModule.MODULE_NAME)
                                        .openPage("stock_detail", data)
                                }
                            }
                            View {
                                Text {
                                    attr {
                                        text(quote.name)
                                        color(FinancePalette.text)
                                        fontSize(16f)
                                        fontWeight600()
                                    }
                                }
                                Text {
                                    attr {
                                        text(quote.code)
                                        color(FinancePalette.muted)
                                        fontFamily("monospace")
                                        fontSize(11f)
                                        marginTop(5f)
                                    }
                                }
                            }
                            View {
                                attr {
                                    alignItemsFlexEnd()
                                }
                                Text {
                                    attr {
                                        text(quote.latestPrice.pretty())
                                        color(FinancePalette.text)
                                        fontFamily("monospace")
                                        fontSize(17f)
                                        fontWeight700()
                                    }
                                }
                                Text {
                                    attr {
                                        text(quote.changeText())
                                        color(quote.changeColor())
                                        fontFamily("monospace")
                                        fontSize(11f)
                                        marginTop(5f)
                                    }
                                }
                            }
                        }
                    }

                    Text {
                        attr {
                            text(StockAnalysis.DISCLAIMER)
                            color(FinancePalette.muted)
                            fontSize(11f)
                            margin(left = 20f, right = 20f, top = 24f, bottom = 28f)
                            textAlignCenter()
                        }
                    }
                }
            } else {
                ctx.statePanel(container, demoState)
            }
        }
    }

    private fun statePanel(container: ViewContainer<*, *>, state: String) {
        with(container) {
            View {
            attr {
                flex(1f)
                allCenter()
                padding(28f)
            }
            Text {
                attr {
                    text(
                        when (state) {
                            "loading" -> "行情载入中…"
                            "empty" -> "暂无行情数据"
                            else -> "行情加载失败"
                        },
                    )
                    color(FinancePalette.text)
                    fontSize(22f)
                    fontWeight700()
                }
            }
            Text {
                attr {
                    text(
                        when (state) {
                            "loading" -> "正在读取离线 Mock 数据"
                            "empty" -> "Provider 返回了空列表"
                            else -> "演示错误状态，可点击重试"
                        },
                    )
                    color(FinancePalette.muted)
                    fontSize(13f)
                    marginTop(10f)
                }
            }
            if (state == "error") {
                View {
                    attr {
                        marginTop(20f)
                        padding(left = 22f, right = 22f, top = 11f, bottom = 11f)
                        backgroundColor(FinancePalette.blue)
                        borderRadius(22f)
                    }
                    event {
                        click {
                            container.acquireModule<RouterModule>(RouterModule.MODULE_NAME)
                                .openPage("finance_home")
                        }
                    }
                    Text {
                        attr {
                            text("重试")
                            color(Color.WHITE)
                            fontSize(14f)
                            fontWeight700()
                        }
                    }
                }
            }
            }
        }
    }
}

@Page("stock_detail", supportInLocal = true)
class StockDetailPage : Pager() {
    private val market = MockMarketDataSource()
    private val analysisProvider = MockAnalysisProvider()
    private var selectedPoint: String by observable("拖动查看分时数据")

    override fun body(): ViewBuilder {
        val ctx = this
        val requestedCode = pageData.params.optString("code").ifEmpty { "600519" }
        val detail = runCatching { market.detailSnapshot(requestedCode) }
            .getOrElse { market.detailSnapshot("600519") }
        val quote = detail.quote
        val trend = market.trendSnapshot(quote.code)
        val candles = market.candlesSnapshot(quote.code)
        val analysis = analysisProvider.analyzeSnapshot(detail)
        return {
            val container = this
            attr {
                backgroundColor(FinancePalette.background)
            }
            List {
                attr {
                    flex(1f)
                }
                View {
                    attr {
                        padding(left = 18f, right = 18f, top = 20f, bottom = 14f)
                        flexDirectionRow()
                        alignItemsCenter()
                    }
                    event {
                        click {
                            ctx.acquireModule<RouterModule>(RouterModule.MODULE_NAME).closePage()
                        }
                    }
                    Text {
                        attr {
                            text("‹")
                            color(FinancePalette.text)
                            fontSize(32f)
                            marginRight(12f)
                        }
                    }
                    View {
                        Text {
                            attr {
                                text(quote.name)
                                color(FinancePalette.text)
                                fontSize(21f)
                                fontWeight700()
                            }
                        }
                        Text {
                            attr {
                                text("${detail.market} · ${quote.code} · MOCK")
                                color(FinancePalette.muted)
                                fontFamily("monospace")
                                fontSize(11f)
                                marginTop(3f)
                            }
                        }
                    }
                }

                View {
                    attr {
                        padding(left = 20f, right = 20f, bottom = 18f)
                    }
                    Text {
                        attr {
                            text("¥${quote.latestPrice.pretty()}")
                            color(FinancePalette.text)
                            fontFamily("monospace")
                            fontSize(38f)
                            fontWeight700()
                        }
                    }
                    Text {
                        attr {
                            text(quote.changeText())
                            color(quote.changeColor())
                            fontFamily("monospace")
                            fontSize(14f)
                            marginTop(6f)
                        }
                    }
                }

                View {
                    attr {
                        margin(left = 16f, right = 16f)
                        padding(14f)
                        backgroundColor(FinancePalette.panel)
                        borderRadius(12f)
                        flexDirectionRow()
                        justifyContentSpaceBetween()
                    }
                    ctx.addMetric(container, "今开", detail.open.pretty())
                    ctx.addMetric(container, "最高", quote.high.pretty())
                    ctx.addMetric(container, "最低", quote.low.pretty())
                    ctx.addMetric(container, "成交量", "${(quote.volume / 10000f).pretty()}万")
                }

                View {
                    attr {
                        margin(left = 16f, right = 16f, top = 14f)
                        height(280f)
                        backgroundColor(FinancePalette.panel)
                        borderRadius(14f)
                        overflow(true)
                        paddingTop(14f)
                    }
                    Text {
                        attr {
                            text("分时走势")
                            color(FinancePalette.text)
                            fontSize(14f)
                            fontWeight600()
                            marginLeft(15f)
                        }
                    }
                    Text {
                        attr {
                            text(ctx.selectedPoint)
                            color(FinancePalette.muted)
                            fontFamily("monospace")
                            fontSize(11f)
                            margin(left = 15f, top = 4f)
                        }
                    }
                    LineChart {
                        attr {
                            height(220f)
                            marginTop(4f)
                            data(listOf(ChartSeries(quote.code, trend, quote.changeColor())))
                            showDots(false)
                            showTooltip(true)
                            areaFill(true)
                            theme(ChartTheme.FINANCE_DARK)
                        }
                        event {
                            onPointSelected {
                                ctx.selectedPoint = "${it.label}  ¥${it.value.pretty()}"
                            }
                        }
                    }
                }

                View {
                    attr {
                        margin(left = 16f, right = 16f, top = 14f)
                        height(304f)
                        backgroundColor(FinancePalette.panel)
                        borderRadius(14f)
                        overflow(true)
                        paddingTop(14f)
                    }
                    Text {
                        attr {
                            text("K线 / 成交量")
                            color(FinancePalette.text)
                            fontSize(14f)
                            fontWeight600()
                            marginLeft(15f)
                        }
                    }
                    CandleChart {
                        attr {
                            height(264f)
                            marginTop(3f)
                            data(candles)
                            visibleCount(28)
                            showVolume(true)
                            showAxis(true)
                            showGrid(true)
                            showTooltip(true)
                            theme(ChartTheme.FINANCE_DARK)
                        }
                        event {
                            onCandleSelected { _, candle ->
                                ctx.selectedPoint = "${candle.label}  ¥${candle.close.pretty()}"
                            }
                        }
                    }
                }

                ctx.addAnalysisCard(container, analysis)

                Text {
                    attr {
                        text(analysis.disclaimer)
                        color(FinancePalette.muted)
                        fontSize(11f)
                        margin(left = 20f, right = 20f, top = 18f, bottom = 28f)
                        textAlignCenter()
                    }
                }
            }
        }
    }

    private fun addMetric(container: ViewContainer<*, *>, label: String, value: String) {
        with(container) {
            View {
            attr {
                alignItemsCenter()
            }
            Text {
                attr {
                    text(label)
                    color(FinancePalette.muted)
                    fontSize(10f)
                }
            }
            Text {
                attr {
                    text(value)
                    color(FinancePalette.text)
                    fontFamily("monospace")
                    fontSize(12f)
                    fontWeight600()
                    marginTop(5f)
                }
            }
            }
        }
    }

    private fun addAnalysisCard(container: ViewContainer<*, *>, analysis: StockAnalysis) {
        with(container) {
            View {
            attr {
                margin(left = 16f, right = 16f, top = 14f)
                padding(17f)
                backgroundColor(Color(0xFF1B2130L))
                borderRadius(14f)
                borderLeft(Border(3f, BorderStyle.SOLID, FinancePalette.amber))
            }
            View {
                attr {
                    flexDirectionRow()
                    justifyContentSpaceBetween()
                    alignItemsCenter()
                }
                Text {
                    attr {
                        text("AI / 结构化解读")
                        color(FinancePalette.amber)
                        fontFamily("monospace")
                        fontSize(12f)
                        letterSpacing(1f)
                        fontWeight700()
                    }
                }
                Text {
                    attr {
                        text("MOCK · ${analysis.riskLevel.name}")
                        color(
                            when (analysis.riskLevel) {
                                RiskLevel.LOW -> FinancePalette.positive
                                RiskLevel.MEDIUM -> FinancePalette.amber
                                RiskLevel.HIGH -> FinancePalette.negative
                            },
                        )
                        fontFamily("monospace")
                        fontSize(10f)
                    }
                }
            }
            Text {
                attr {
                    text(analysis.summary)
                    color(FinancePalette.text)
                    fontSize(14f)
                    lineHeight(21f)
                    marginTop(13f)
                }
            }
            View {
                attr {
                    height(1f)
                    backgroundColor(FinancePalette.rule)
                    margin(top = 14f, bottom = 12f)
                }
            }
            Text {
                attr {
                    text("支撑 ${analysis.supportLevel.pretty()}  /  压力 ${analysis.resistanceLevel.pretty()}")
                    color(FinancePalette.blue)
                    fontFamily("monospace")
                    fontSize(12f)
                }
            }
            analysis.keyPoints.forEach { point ->
                Text {
                    attr {
                        text("• $point")
                        color(FinancePalette.muted)
                        fontSize(12f)
                        marginTop(8f)
                    }
                }
            }
            if (analysis.riskNotes.isNotEmpty()) {
                Text {
                    attr {
                        text("风险提示：${analysis.riskNotes.joinToString("；")}")
                        color(FinancePalette.amber)
                        fontSize(12f)
                        lineHeight(18f)
                        marginTop(12f)
                    }
                }
            }
            }
        }
    }
}
