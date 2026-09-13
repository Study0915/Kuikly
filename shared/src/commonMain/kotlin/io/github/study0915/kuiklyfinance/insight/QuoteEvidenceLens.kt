package io.github.study0915.kuiklyfinance.insight

import com.tencent.kuikly.core.base.Color
import com.tencent.kuikly.core.base.ViewContainer
import com.tencent.kuikly.core.views.*
import com.tencent.kuikly.core.directives.vif
import io.github.study0915.kuiklyfinance.ui.*

private fun ViewContainer<*, *>.LensText(value: () -> String, size: Float = 14f, color: Color = financeInk, strong: Boolean = false) {
    FinanceText(value, size, color, strong)
}
private fun ViewContainer<*, *>.LensButton(label: () -> String, selected: () -> Boolean = { false },
    enabled: () -> Boolean = { true }, display: (() -> String)? = null, action: () -> Unit) {
    View {
        attr { height(48f); paddingLeft(8f); paddingRight(8f); justifyContentCenter(); borderRadius(8f)
            if (display != null) alignItemsCenter()
            backgroundColor(if (selected()) FinanceTheme.tint else Color.TRANSPARENT)
            accessibility(label()); touchEnable(enabled()) }
        event { click { if (enabled()) action() } }
        Text { attr { text(display?.invoke() ?: label()); fontFamily(FinanceTheme.font(pagerData.isWeb)); fontSize(13f); lineHeight(20f); lines(0)
            color(if (selected()) financeBlue else financeMuted); if (selected()) fontWeightSemiBold() } }
    }
}

/** Caller owns state and scrolling. This entry point has no route/page dependency. */
fun ViewContainer<*, *>.QuoteEvidenceLens(doc: ResolvedDocument, state: () -> LensUiState, tap: PlotTap,
    onAction: (LensAction) -> Unit) = QuoteEvidenceLens(doc, state, tap, onAction, { true }, null)

/** Optional caller-owned formula disclosure; the original four-argument API stays usable. */
fun ViewContainer<*, *>.QuoteEvidenceLens(doc: ResolvedDocument, state: () -> LensUiState, tap: PlotTap,
    onAction: (LensAction) -> Unit, calculationExpanded: () -> Boolean, onToggleCalculation: (() -> Unit)?) {
    val presenter = LensPresenter(doc)
    fun view() = presenter.present(state())

    View {
        attr { flexDirectionRow(); alignItemsCenter(); justifyContentSpaceBetween(); marginBottom(8f) }
        LensText({ "行情与依据" }, 17f, strong = true)
        LensText({ "20 个交易日 · 日 K" }, 12f, financeMuted)
    }
    if (doc.canPlot) MarketPlot(doc.bars, { view().mark }, tap) { onAction(LensAction.InspectDay(it)) }
    View {
        attr { flexDirectionRow(); marginTop(4f); marginBottom(8f); accessibility("解读依据选择") }
        doc.evidence.forEach { evidence ->
            View {
                attr { flex(1f); marginRight(2f) }
                LensButton({ (if (view().focus == LensFocus.EvidenceFocus(evidence.evidence.id)) "已选 · " else "") + evidence.evidence.label + if (!evidence.available) " · 暂不可用" else "" },
                    { view().focus == LensFocus.EvidenceFocus(evidence.evidence.id) }, { evidence.available },
                    display = { evidence.evidence.label + if (!evidence.available) "\n不可用" else "" }) { onAction(LensAction.SelectEvidence(evidence.evidence.id)) }
            }
        }
    }
    doc.evidence.filter { !it.available }.forEach { evidence -> LensText({ evidence.reason ?: "依据不可用" }, 12f, financeMuted) }
    View {
        attr { padding(14f); backgroundColor(FinanceTheme.tint); borderRadius(12f); accessibility("当前行情事实") }
        LensText({ "依据核对" }, 12f, financeBlue, true)
        View { attr { marginTop(4f); marginBottom(4f) }; LensText({ view().title }, 16f, strong = true) }
        Text { attr { text(view().value); fontSize(if (view().focus == LensFocus.Overview) 15f else 24f)
            fontFamily(FinanceTheme.font(pagerData.isWeb)); fontWeightSemiBold(); lineHeight(32f); lines(0); color(financeInk) } }
        vif({ view().detail.isNotEmpty() }) { View { attr { marginTop(8f) }; LensText({ view().detail }, 14f) } }
        vif({ view().calculation.isNotEmpty() }) {
            if (onToggleCalculation != null) LensButton({ if (calculationExpanded()) "收起计算" else "查看计算" }) { onToggleCalculation() }
            vif({ calculationExpanded() }) {
                View {
                    attr { padding(12f); marginBottom(8f); backgroundColor(Color.WHITE); borderRadius(8f); accessibility("计算明细") }
                    LensText({ "计算过程" }, 12f, financeBlue, true)
                    LensText({ view().calculation }, 13f)
                }
            }
        }
        // Date/sample links stay available even when formulas are folded.
        (0 until 6).forEach { index ->
            vif({ view().links.size > index }) {
                LensButton({ view().links.getOrNull(index)?.label ?: "" }) { view().links.getOrNull(index)?.let { onAction(it.action) } }
            }
        }
        vif({ view().focus is LensFocus.DayInspect }) {
            View { attr { marginTop(8f) }; LensText({ "已有相关依据" }, 14f, strong = true) }
            doc.evidence.indices.forEach { index ->
                vif({ view().related.size > index }) {
                    LensButton({ view().related.getOrNull(index)?.label ?: "" }) { view().related.getOrNull(index)?.let { onAction(it.action) } }
                }
            }
            LensText({ view().relationNote }, 12f, financeMuted)
        }
        vif({ view().limitation.isNotEmpty() }) { LensText({ view().limitation }, 12f, financeMuted) }
        LensButton({ "返回解读" }) { onAction(LensAction.ReturnToSummary) }
    }
    if (doc.canPlot) {
        View {
            attr { flexDirectionRow(); marginTop(8f); accessibility("交易日导航") }
            View { attr { width(72f) }
                LensButton({ "前一日" }, enabled = { view().navigation?.previous != null }) { view().navigation?.previous?.let { onAction(LensAction.InspectDay(it)) } }
            }
            View { attr { flex(1f) }
                LensButton({ view().navigation?.let { "${if (it.inspecting) "" else "检视 "}${it.date.takeLast(5)} · ${it.position}/${it.total}" } ?: "暂无日期" },
                    selected = { view().navigation?.inspecting == true }, enabled = { view().navigation != null }) { view().navigation?.date?.let { onAction(LensAction.InspectDay(it)) } }
            }
            View { attr { width(72f) }
                LensButton({ "后一日" }, enabled = { view().navigation?.next != null }) { view().navigation?.next?.let { onAction(LensAction.InspectDay(it)) } }
            }
        }
    }
    View {
        attr { marginTop(12f); paddingTop(12f) }
        LensText({ "AI 解读（Mock）" }, 17f, strong = true)
        View { attr { marginTop(8f); marginBottom(8f) }; LensText({ doc.summary }, 15f) }
        LensText({ doc.freshness.text + "；72 小时仅为演示策略。" }, 12f, financeMuted)
        LensText({ "窗口 ${doc.bars.first().date} 至 ${doc.bars.last().date} · 20 个交易日" }, 12f, financeMuted)
        LensText({ "红涨绿跌以开收盘比较；平盘为横线，× 为缺量。蓝色标记对应当前依据或日期。" }, 12f, financeMuted)
    }
}
