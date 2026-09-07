package io.github.study0915.kuiklyfinance.insight

import com.tencent.kuikly.core.base.Color
import com.tencent.kuikly.core.base.ViewContainer
import com.tencent.kuikly.core.views.*
import com.tencent.kuikly.core.directives.vif
import com.tencent.kuikly.core.directives.velse
import io.github.study0915.kuiklyfinance.market.*

private val lensInk = Color(0xFF183342L)
private val lensMuted = Color(0xFF677C87L)
private fun ViewContainer<*, *>.LensText(value: () -> String, size: Float = 14f, color: Color = lensInk) {
    Text { attr { text(value()); fontFamily("sans-serif"); fontSize(size); lineHeight(size * 1.6f); lines(0); color(color); marginBottom(6f) } }
}
private fun ViewContainer<*, *>.LensButton(label: () -> String, selected: () -> Boolean = { false }, enabled: Boolean = true, action: () -> Unit) {
    View {
        attr { padding(11f); marginBottom(8f); borderRadius(8f); backgroundColor(if (selected()) Color(0xFFD3E8F1L) else Color(0xFFEAF2F5L)); accessibility(label()); touchEnable(enabled) }
        if (enabled) event { click { action() } }
        LensText(label, 14f, if (!enabled) lensMuted else lensInk)
    }
}

/** Caller owns state and scrolling. This entry point has no route/page dependency. */
fun ViewContainer<*, *>.QuoteEvidenceLens(doc: ResolvedDocument, state: () -> LensUiState, tap: PlotTap, onAction: (LensAction) -> Unit) {
    fun focus() = LensState.normalize(doc, state()).focus
    fun day() = (focus() as? LensFocus.DayInspect)?.let { selected -> doc.bars.firstOrNull { it.date == selected.date } }
    fun active() = (focus() as? LensFocus.EvidenceFocus)?.let { selected -> doc.evidence.firstOrNull { it.evidence.id == selected.evidenceId } }
    fun related() = day()?.let { EvidenceResolver.related(doc, it.date) } ?: emptyList()

    LensText({ "AI 解读（Mock）" }, 21f)
    LensText({ doc.summary }, 15f)
    LensText({ doc.freshness.text + "；72 小时仅为演示策略。" }, 12f, lensMuted)
    LensText({ "点依据定位区间；点 K 线或量柱检视当天。" }, 12f, lensMuted)
    doc.evidence.forEach { evidence ->
        LensButton({ (if (focus() == LensFocus.EvidenceFocus(evidence.evidence.id)) "已选 · " else "") + evidence.evidence.label + if (!evidence.available) " · 暂不可用" else "" },
            { focus() == LensFocus.EvidenceFocus(evidence.evidence.id) }, evidence.available) { onAction(LensAction.SelectEvidence(evidence.evidence.id)) }
        if (!evidence.available) LensText({ evidence.reason ?: "依据不可用" }, 12f, lensMuted)
    }
    if (doc.canPlot) MarketPlot(doc.bars, { EvidenceResolver.mark(doc, focus()) }, tap) { onAction(LensAction.InspectDay(it)) }
    LensText({ "红实体：收盘高于开盘；绿实体：低于开盘；平盘为横线。× 为缺量。蓝色方点/竖线表示当前依据。" }, 11f, lensMuted)
    View {
        attr { padding(12f); backgroundColor(Color(0xFFF1F6F8L)); borderRadius(8f); accessibility("当前行情事实") }
        vif({ focus() is LensFocus.EvidenceFocus }) {
            LensText({ active()?.evidence?.label ?: "" }, 17f)
            LensText({ when (val fact = active()?.fact) {
                is EvidenceFact.Return -> "${fact.start.date} → ${fact.end.date}\n${MarketFormatter.price(fact.start.closeMinor)} → ${MarketFormatter.price(fact.end.closeMinor)} 元\n区间变化 ${MarketFormatter.percent(fact.percent)}"
                is EvidenceFact.Volume -> "目标日 ${fact.target.date}\n${MarketFormatter.volume(fact.target.volumeShares)} / 均量 ${MarketFormatter.decimal(fact.average / 10_000)} 万股\n量能倍数 ${MarketFormatter.decimal(fact.ratio)} 倍"
                null -> "依据暂不可用"
            } }, 15f)
            LensText({ when (val fact = active()?.fact) {
                is EvidenceFact.Return -> if (fact.wholeWindow) "整体观察：仅比较窗口首尾收盘价，不代表每一天的走势。" else "局部变化：仅比较选定端点，不是最大回撤，也不是单日涨跌解释。"
                is EvidenceFact.Volume -> "比较样本：${fact.samples.joinToString { it.date.takeLast(5) }}，不含目标日。浅蓝色为五日样本，竖线为目标日；倍数不解释成交原因。"
                null -> ""
            } }, 12f, lensMuted)
        }
        velse {
            vif({ focus() is LensFocus.DayInspect }) {
                LensText({ "检视 ${day()?.date ?: ""}" }, 17f)
                LensText({ day()?.let { "开 ${MarketFormatter.price(it.openMinor)}  高 ${MarketFormatter.price(it.highMinor)}\n低 ${MarketFormatter.price(it.lowMinor)}  收 ${MarketFormatter.price(it.closeMinor)}\n成交量 ${MarketFormatter.volume(it.volumeShares)}" } ?: "" }, 15f)
                LensText({ day()?.let { bar ->
                    val index = doc.bars.indexOf(bar)
                    val value = MarketFormatter.change(doc.bars.getOrNull(index - 1), bar)
                    if (value == null) "窗口首日无前收，当日涨跌不可计算。" else "当日${MarketFormatter.direction(value)} ${MarketFormatter.percent(value)}（相对前收）"
                } ?: "" }, 12f, lensMuted)
                LensText({ "已有相关依据" }, 15f)
                (0..3).forEach { priority ->
                    doc.evidence.forEach { evidence ->
                        vif({ related().any { it.evidence.evidence.id == evidence.evidence.id && it.priority == priority } }) {
                            LensButton({ "${related().firstOrNull { it.evidence.evidence.id == evidence.evidence.id }?.role ?: ""} · ${evidence.evidence.label} ›" }) { onAction(LensAction.SelectEvidence(evidence.evidence.id)) }
                        }
                    }
                }
                LensText({ if (related().all { it.role == "整体观察" }) "没有针对该日的单独解读。可查看原始数值与整体观察。" else "这些入口指向已有的区间或量能依据，不生成单日因果解释。" }, 12f, lensMuted)
            }
            velse { LensText({ state().notice ?: "暂无可用解读，可点图检视原始行情。" }, 14f) }
        }
        LensButton({ "返回解读" }) { onAction(LensAction.ReturnToSummary) }
    }
}
