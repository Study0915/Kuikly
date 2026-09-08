package io.github.study0915.kuiklyfinance.insight

import com.tencent.kuikly.core.base.Color
import com.tencent.kuikly.core.base.ViewContainer
import com.tencent.kuikly.core.views.*
import com.tencent.kuikly.core.directives.vif

private val lensInk = Color(0xFF183342L)
private val lensMuted = Color(0xFF5D737FL)
private val lensBlue = Color(0xFF176F91L)
private fun ViewContainer<*, *>.LensText(value: () -> String, size: Float = 14f, color: Color = lensInk) {
    Text { attr { text(value()); fontFamily("sans-serif"); fontSize(size); lineHeight(size * 1.6f); lines(0); color(color); marginBottom(6f) } }
}
private fun ViewContainer<*, *>.LensButton(label: () -> String, selected: () -> Boolean = { false },
    enabled: () -> Boolean = { true }, action: () -> Unit) {
    View {
        attr { padding(10f); marginBottom(6f); borderRadius(6f)
            backgroundColor(if (selected()) Color(0xFFD3E8F1L) else if (enabled()) Color(0xFFEAF2F5L) else Color(0xFFF0F2F3L))
            accessibility(label()); touchEnable(enabled()) }
        event { click { if (enabled()) action() } }
        Text { attr { text(label()); fontFamily("sans-serif"); fontSize(13f); lineHeight(22f); lines(0); color(if (enabled()) lensInk else lensMuted) } }
    }
}

/** Caller owns state and scrolling. This entry point has no route/page dependency. */
fun ViewContainer<*, *>.QuoteEvidenceLens(doc: ResolvedDocument, state: () -> LensUiState, tap: PlotTap, onAction: (LensAction) -> Unit) {
    val presenter = LensPresenter(doc)
    fun view() = presenter.present(state())

    LensText({ "AI 解读（Mock）" }, 21f)
    LensText({ doc.summary }, 15f)
    LensText({ doc.freshness.text + "；72 小时仅为演示策略。" }, 12f, lensMuted)
    LensText({ "01 / 选择依据" }, 12f, lensBlue)
    doc.evidence.forEach { evidence ->
        LensButton({ (if (view().focus == LensFocus.EvidenceFocus(evidence.evidence.id)) "已选 · " else "") + evidence.evidence.label + if (!evidence.available) " · 暂不可用" else "" },
            { view().focus == LensFocus.EvidenceFocus(evidence.evidence.id) }, { evidence.available }) { onAction(LensAction.SelectEvidence(evidence.evidence.id)) }
        if (!evidence.available) LensText({ evidence.reason ?: "依据不可用" }, 12f, lensMuted)
    }
    LensText({ "02 / 对照行情" }, 12f, lensBlue)
    LensText({ "点 K 线或量柱查看当日；用下方按钮逐日核对。" }, 12f, lensMuted)
    if (doc.canPlot) {
        MarketPlot(doc.bars, { view().mark }, tap) { onAction(LensAction.InspectDay(it)) }
        View {
            attr { flexDirectionRow(); accessibility("交易日导航") }
            View {
                attr { width(68f); marginRight(4f) }
                LensButton({ "前一日" }, enabled = { view().navigation?.previous != null }) {
                    view().navigation?.previous?.let { onAction(LensAction.InspectDay(it)) }
                }
            }
            View {
                attr { flex(1f) }
                LensButton({ view().navigation?.let { "${if (it.inspecting) "" else "检视 "}${it.date.takeLast(5)} · ${it.position}/${it.total}" } ?: "暂无日期" },
                    selected = { view().navigation?.inspecting == true }, enabled = { view().navigation != null }) {
                    view().navigation?.date?.let { onAction(LensAction.InspectDay(it)) }
                }
            }
            View {
                attr { width(68f); marginLeft(4f) }
                LensButton({ "后一日" }, enabled = { view().navigation?.next != null }) {
                    view().navigation?.next?.let { onAction(LensAction.InspectDay(it)) }
                }
            }
        }
    }
    LensText({ "红涨绿跌以开收盘比较；平盘为横线，× 为缺量。蓝色标记对应当前依据或日期。" }, 11f, lensMuted)
    View {
        attr { padding(12f); backgroundColor(Color(0xFFF1F6F8L)); borderRadius(8f); accessibility("当前行情事实") }
        LensText({ "03 / 核对数值" }, 12f, lensBlue)
        LensText({ view().title }, 17f)
        LensText({ view().value }, 22f)
        vif({ view().detail.isNotEmpty() }) { LensText({ view().detail }, 14f) }
        vif({ view().calculation.isNotEmpty() }) {
            View {
                attr { padding(10f); margin(4f, 0f, 8f, 0f); backgroundColor(Color.WHITE); borderRadius(6f) }
                LensText({ "计算过程" }, 12f, lensBlue)
                LensText({ view().calculation }, 12f)
            }
        }
        vif({ view().limitation.isNotEmpty() }) { LensText({ view().limitation }, 12f, lensMuted) }
        // Stable slots preserve UI order when the selected date changes its relation set.
        (0 until 6).forEach { index ->
            vif({ view().links.size > index }) {
                LensButton({ view().links.getOrNull(index)?.label ?: "" }) {
                    view().links.getOrNull(index)?.let { onAction(it.action) }
                }
            }
        }
        vif({ view().focus is LensFocus.DayInspect }) {
            LensText({ "已有相关依据" }, 15f)
            doc.evidence.indices.forEach { index ->
                vif({ view().related.size > index }) {
                    LensButton({ view().related.getOrNull(index)?.label ?: "" }) {
                        view().related.getOrNull(index)?.let { onAction(it.action) }
                    }
                }
            }
            LensText({ view().relationNote }, 12f, lensMuted)
        }
        LensButton({ "返回解读" }) { onAction(LensAction.ReturnToSummary) }
    }
}
