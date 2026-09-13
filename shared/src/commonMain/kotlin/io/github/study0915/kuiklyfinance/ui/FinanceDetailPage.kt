package io.github.study0915.kuiklyfinance.ui

import com.tencent.kuikly.core.base.Color
import com.tencent.kuikly.core.base.ViewContainer
import com.tencent.kuikly.core.views.*
import io.github.study0915.kuiklyfinance.insight.*
import io.github.study0915.kuiklyfinance.market.*

internal fun ViewContainer<*, *>.FinanceDetail(
    doc: ResolvedDocument, state: () -> LensUiState, tap: PlotTap, scenario: DemoScenario,
    presentation: DetailPresentationState, isCurrent: () -> Boolean,
    onScroll: (Float) -> Unit, onAction: (LensAction) -> Unit,
) {
    val snapshot = doc.document.snapshot; val bars = doc.bars; val latest = bars.last()
    val change = MarketFormatter.change(bars[bars.lastIndex - 1], latest)!!
    List {
        val list = this
        val savedOffset = presentation.offset
        attr { flex(1f); backgroundColor(Color.WHITE); accessibility("个股详情") }
        event { scroll { if (isCurrent()) onScroll(it.offsetY) } }
        View {
            attr { padding(8f, 16f, 12f, 16f) }
            View {
                attr { flexDirectionRow(); justifyContentSpaceBetween(); alignItemsCenter() }
                FinanceText({ "${snapshot.name}  /  ${snapshot.entityId}" }, 16f, strong = true)
                FinanceText({ "历史 Mock" }, 12f, financeMuted)
            }
            View {
                attr { flexDirectionRow(); alignItemsCenter(); marginTop(4f) }
                FinancePrice(MarketFormatter.price(latest.closeMinor), 36f)
                View { attr { marginLeft(12f) }
                    FinanceText({ "${MarketFormatter.decimal((latest.closeMinor - bars[bars.lastIndex - 1].closeMinor) / 100.0, true)} 元" }, 14f, if (change >= 0) FinanceTheme.up else FinanceTheme.down)
                    FinanceText({ "当日${MarketFormatter.direction(change)} ${MarketFormatter.percent(change)}" }, 13f, if (change >= 0) FinanceTheme.up else FinanceTheme.down)
                }
            }
            View {
                attr { flexDirectionRow(); marginTop(8f); marginBottom(8f) }
                listOf("开盘" to MarketFormatter.price(latest.openMinor), "最高" to MarketFormatter.price(latest.highMinor),
                    "最低" to MarketFormatter.price(latest.lowMinor), "成交量" to MarketFormatter.volume(latest.volumeShares)).forEach { (label, value) ->
                    View { attr { flex(1f) }
                        FinanceText({ label }, 12f, financeMuted)
                        FinanceText({ value }, 13f, strong = true)
                    }
                }
            }
            FinanceText({ "截止 ${snapshot.asOf.take(16).replace('T', ' ')} · 当前：${scenario.label}" }, 12f, financeMuted)
        }
        View {
            attr { height(1f); marginLeft(16f); marginRight(16f); backgroundColor(FinanceTheme.line) }
        }
        View {
            attr { padding(12f, 16f, 16f, 16f) }
            QuoteEvidenceLens(doc, state, tap, onAction,
                calculationExpanded = { presentation.calculationExpanded },
                onToggleCalculation = { if (isCurrent()) { presentation.calculationExpanded = !presentation.calculationExpanded; tap.cancel() } })
        }
        if (scenario == DemoScenario.LONG_TEXT) {
            View { attr { padding(16f) }
                FinanceText({ "解读边界：" + List(4) { "区间上涨不代表每天上涨；局部回落不等于最大回撤；量能倍数只比较目标日与此前五个窗口交易日，不能据此推断买卖原因。" }.joinToString(" ") }, 14f)
            }
        }
        View { attr { padding(16f); backgroundColor(FinanceTheme.background) }; FinanceText({ Task1ShellContract.DISCLAIMER }, 12f, financeMuted) }
        // Restore once after real content layout, before the user can scroll this mount.
        list.addScrollerViewEventObserver(object : IScrollerViewEventObserver {
            override fun onContentOffsetDidChanged(contentOffsetX: Float, contentOffsetY: Float, params: ScrollParams) = Unit
            override fun subViewsDidLayout() {
                if (!isCurrent()) { list.removeScrollerViewEventObserver(this); return }
                val contentHeight = list.contentView?.frame?.height ?: 0f
                if (contentHeight <= 0f || list.frame.height <= 0f) return
                list.removeScrollerViewEventObserver(this)
                val maximum = (contentHeight - list.frame.height).coerceAtLeast(0f)
                list.setContentOffset(0f, savedOffset.coerceIn(0f, maximum), false)
            }
        })
    }
}
