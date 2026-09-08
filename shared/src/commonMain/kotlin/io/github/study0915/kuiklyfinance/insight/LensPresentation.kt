package io.github.study0915.kuiklyfinance.insight

import io.github.study0915.kuiklyfinance.market.MarketFormatter as Format

data class LensLink(val label: String, val action: LensAction)
data class DayNavigation(val date: String, val position: Int, val total: Int, val inspecting: Boolean,
                         val previous: String?, val next: String?)
data class LensPresentation(
    val focus: LensFocus, val title: String, val value: String, val detail: String,
    val calculation: String, val limitation: String, val links: List<LensLink>,
    val related: List<LensLink>, val relationNote: String, val navigation: DayNavigation?, val mark: PlotMark,
)

/** Per-card projection: calculations/relations run once per selection, never once per text attribute. */
class LensPresenter(private val doc: ResolvedDocument) {
    private var cachedState: LensUiState? = null
    private var cached: LensPresentation? = null

    fun present(state: LensUiState): LensPresentation {
        val current = LensState.normalize(doc, state)
        if (current == cachedState) return cached!!
        val result = project(current)
        cachedState = current; cached = result
        return result
    }

    private fun project(state: LensUiState): LensPresentation {
        val focus = state.focus
        val active = (focus as? LensFocus.EvidenceFocus)?.let { selected -> doc.evidence.firstOrNull { it.evidence.id == selected.evidenceId } }
        val dayIndex = (focus as? LensFocus.DayInspect)?.let { selected -> doc.bars.indexOfFirst { it.date == selected.date } }
        val fact = active?.fact
        val anchor = dayIndex ?: when (fact) {
            is EvidenceFact.Return -> doc.bars.indexOf(fact.end)
            is EvidenceFact.Volume -> doc.bars.indexOf(fact.target)
            null -> doc.bars.lastIndex
        }
        val navigation = if (doc.canPlot && anchor in doc.bars.indices) DayNavigation(doc.bars[anchor].date, anchor + 1, doc.bars.size,
            dayIndex != null, doc.bars.getOrNull(anchor - 1)?.date, doc.bars.getOrNull(anchor + 1)?.date) else null
        fun result(title: String, value: String, detail: String = "", calculation: String = "", limitation: String = "",
                   links: List<LensLink> = emptyList(), related: List<LensLink> = emptyList(), note: String = "") =
            LensPresentation(focus, title, value, detail, calculation, limitation, links, related, note, navigation, EvidenceResolver.mark(doc, focus))

        if (dayIndex != null && dayIndex in doc.bars.indices) {
            val day = doc.bars[dayIndex]; val previous = doc.bars.getOrNull(dayIndex - 1)
            val change = Format.change(previous, day)
            val related = EvidenceResolver.related(doc, day.date)
            return result("检视 ${day.date}", "收 ${Format.price(day.closeMinor)} 元",
                "开 ${Format.price(day.openMinor)}  高 ${Format.price(day.highMinor)}\n低 ${Format.price(day.lowMinor)}  收 ${Format.price(day.closeMinor)}\n成交量 ${Format.volume(day.volumeShares)}",
                if (change == null) "窗口首日无前收，当日涨跌不可计算。" else
                    "当日${Format.direction(change)} ${Format.percent(change)}（相对前收）\n(${Format.price(day.closeMinor)} − ${Format.price(previous!!.closeMinor)}) ÷ ${Format.price(previous.closeMinor)} × 100%",
                related = related.map { LensLink("${it.role} · ${it.evidence.evidence.label} ›", LensAction.SelectEvidence(it.evidence.evidence.id)) },
                note = if (related.all { it.priority == 3 }) "没有针对该日的单独解读。可查看原始数值与整体观察。" else
                    "这些入口指向已有的区间或量能依据，不生成单日因果解释。")
        }
        return when (fact) {
            is EvidenceFact.Return -> result(active.evidence.label, "区间变化 ${Format.percent(fact.percent)}",
                "${fact.start.date} → ${fact.end.date}\n${Format.price(fact.start.closeMinor)} → ${Format.price(fact.end.closeMinor)} 元",
                "(${Format.price(fact.end.closeMinor)} − ${Format.price(fact.start.closeMinor)}) ÷ ${Format.price(fact.start.closeMinor)} × 100%",
                if (fact.wholeWindow) "整体观察：仅比较窗口首尾收盘价，不代表每一天的走势。" else
                    "局部变化：仅比较选定端点，不是最大回撤，也不是单日涨跌解释。",
                listOf(LensLink("检视起点 ${fact.start.date.takeLast(5)} ›", LensAction.InspectDay(fact.start.date)),
                    LensLink("检视终点 ${fact.end.date.takeLast(5)} ›", LensAction.InspectDay(fact.end.date))))
            is EvidenceFact.Volume -> {
                val sum = fact.samples.sumOf { it.volumeShares!!.toDouble() }
                result(active.evidence.label, "量能倍数 ${Format.decimal(fact.ratio)} 倍",
                    "目标日 ${fact.target.date}\n${Format.volume(fact.target.volumeShares)} / 均量 ${Format.decimal(fact.average / 10_000)} 万股",
                    "样本合计 ${Format.decimal(sum / 10_000)} 万股 ÷ 5 = ${Format.decimal(fact.average / 10_000)} 万股\n${Format.decimal(fact.target.volumeShares!!.toDouble() / 10_000)} ÷ ${Format.decimal(fact.average / 10_000)} = ${Format.decimal(fact.ratio)} 倍",
                    "比较样本：${fact.samples.joinToString { it.date.takeLast(5) }}，不含目标日。浅蓝色为五日样本，竖线为目标日；倍数不解释成交原因。显示值保留两位，计算使用原值。",
                    fact.samples.map { LensLink("样本 ${it.date.takeLast(5)} · ${Format.volume(it.volumeShares)} ›", LensAction.InspectDay(it.date)) } +
                        LensLink("目标 ${fact.target.date.takeLast(5)} · ${Format.volume(fact.target.volumeShares)} ›", LensAction.InspectDay(fact.target.date)))
            }
            null -> result("行情概览", state.notice ?: "暂无可用解读，可点图检视原始行情。")
        }
    }
}
