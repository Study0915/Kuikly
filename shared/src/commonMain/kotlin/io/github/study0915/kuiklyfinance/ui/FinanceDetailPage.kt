package io.github.study0915.kuiklyfinance.ui

import com.tencent.kuikly.core.base.Color
import com.tencent.kuikly.core.base.ViewContainer
import com.tencent.kuikly.core.views.*
import io.github.study0915.kuiklyfinance.insight.*
import io.github.study0915.kuiklyfinance.market.*

internal fun ViewContainer<*, *>.FinanceDetail(
    doc: ResolvedDocument, state: () -> LensUiState, tap: PlotTap, scenario: DemoScenario,
    onScroll: () -> Unit, onAction: (LensAction) -> Unit, onScenario: (DemoScenario) -> Unit,
) {
    val snapshot = doc.document.snapshot; val bars = doc.bars; val latest = bars.last()
    val change = MarketFormatter.change(bars[bars.lastIndex - 1], latest)!!
    List {
        attr { flex(1f); accessibility("个股详情") }
        event { scroll { onScroll() } }
        View {
            attr { padding(20f); backgroundColor(Color.WHITE); marginBottom(12f) }
            FinanceText({ "${snapshot.name}  /  ${snapshot.entityId}" }, 19f)
            FinanceText({ "当前：${scenario.label} · 历史 Mock" }, 12f, financeBlue)
            FinanceText({ MarketFormatter.price(latest.closeMinor) + " 元" }, 38f)
            FinanceText({ "当日${MarketFormatter.direction(change)} ${MarketFormatter.decimal((latest.closeMinor - bars[bars.lastIndex - 1].closeMinor) / 100.0, true)} 元 · ${MarketFormatter.percent(change)}" }, 15f, if (change >= 0) Color(0xFFB94B40L) else Color(0xFF188579L))
            FinanceText({ "开盘 ${MarketFormatter.price(latest.openMinor)}   最高 ${MarketFormatter.price(latest.highMinor)}   最低 ${MarketFormatter.price(latest.lowMinor)}" }, 13f)
            FinanceText({ "成交量 ${MarketFormatter.volume(latest.volumeShares)}" }, 13f)
            FinanceText({ "截止 ${snapshot.asOf.replace('T', ' ')}" }, 12f, financeMuted)
            FinanceText({ "窗口 ${bars.first().date} 至 ${bars.last().date} · 20 个交易日" }, 12f, financeMuted)
        }
        View {
            attr { margin(0f, 12f, 12f, 12f); padding(12f); borderRadius(12f); backgroundColor(Color.WHITE) }
            QuoteEvidenceLens(doc, state, tap, onAction)
        }
        View {
            attr { margin(0f, 12f, 12f, 12f); padding(16f); backgroundColor(Color.WHITE); borderRadius(12f) }
            FinanceText({ "演示数据" }, 17f)
            FinanceText({ "当前：${scenario.label}。只影响这次详情；列表保留完整行情。" }, 12f, financeMuted)
            DemoScenario.entries.forEach { option -> FinanceAction(option.label) { onScenario(option) } }
            if (scenario == DemoScenario.LONG_TEXT) {
                FinanceText({ "解读边界：" + List(4) { "区间上涨不代表每天上涨；局部回落不等于最大回撤；量能倍数只比较目标日与此前五个窗口交易日，不能据此推断买卖原因。" }.joinToString(" ") }, 14f)
            }
        }
        View { attr { padding(20f) }; FinanceText({ Task1ShellContract.DISCLAIMER }, 12f, financeMuted) }
    }
}
