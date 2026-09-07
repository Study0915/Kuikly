package io.github.study0915.kuiklyfinance.ui

import com.tencent.kuikly.core.annotations.Page
import com.tencent.kuikly.core.base.Color
import com.tencent.kuikly.core.base.ViewBuilder
import com.tencent.kuikly.core.base.ViewContainer
import com.tencent.kuikly.core.pager.Pager
import com.tencent.kuikly.core.reactive.handler.observable
import com.tencent.kuikly.core.views.List
import com.tencent.kuikly.core.views.Text
import com.tencent.kuikly.core.views.View
import io.github.study0915.kuiklyfinance.insight.*
import io.github.study0915.kuiklyfinance.market.MarketBar

object Task1Routes { const val FINANCE_HOME = "finance_home" }
internal object Task1ShellContract { const val DISCLAIMER = "仅作技术演示，不构成投资建议" }

/** Temporary C1 caller, replaced by real home/detail after the interaction probe. */
@Page(Task1Routes.FINANCE_HOME, supportInLocal = true)
class Task1ShellPage : Pager() {
    private var first by observable<LensFocus>(LensFocus.EvidenceFocus("E2"))
    private var second by observable<LensFocus>(LensFocus.Overview)
    private var scrollY by observable(0)
    private val taps = listOf(PlotTap(), PlotTap())
    private val closes = listOf(1000,1010,1020,1005,1030,1050,1070,1080,1100,1120,1150,1130,1100,1080,1090,1100,1105,1100,1105,1120)
    private val dates = listOf("10","11","12","13","14","17","18","19","20","21","24","25","26","27","28","31","01","02","03","04")
    private val bars = closes.mapIndexed { i, close ->
        val open = if (i == 0) 995 else closes[i - 1]
        MarketBar("2026-${if (i < 16) "08" else "09"}-${dates[i]}",open,maxOf(open,close)+6,minOf(open,close)-6,close,900_000L+i*40_000L)
    }
    override fun body(): ViewBuilder {
        val ctx = this
        return {
        attr { backgroundColor(Color(0xFFF1F5F7L)) }
        Text { attr { text("双图交互验证"); fontFamily("sans-serif"); fontSize(22f); lineHeight(30f); height(30f); color(Color(0xFF183342L)); margin(20f) } }
        Text { attr { text("历史 Mock · 滚动 ${ctx.scrollY} · 点图检视"); fontFamily("sans-serif"); fontSize(12f); lineHeight(20f); height(20f); color(Color(0xFF677C87L)); marginLeft(20f); marginBottom(12f) } }
        List {
            attr { flex(1f) }
            event { scroll { ctx.scrollY = it.offsetY.toInt(); ctx.taps.forEach { tap -> tap.cancel() } } }
            ctx.probeCard(this, "实例 A", { ctx.first }, { ctx.first = it }, ctx.taps[0])
            ctx.probeCard(this, "实例 B", { ctx.second }, { ctx.second = it }, ctx.taps[1])
            Text { attr { text(Task1ShellContract.DISCLAIMER); margin(20f); fontFamily("sans-serif"); fontSize(12f) } }
        }
    }
    }
    private fun probeCard(parent: ViewContainer<*, *>, title: String, value: () -> LensFocus, change: (LensFocus) -> Unit, tap: PlotTap) {
        val series = bars
        parent.View {
            attr { margin(12f); padding(12f); backgroundColor(Color.WHITE); borderRadius(12f) }
            Text { attr { text(title); fontFamily("sans-serif"); fontSize(18f); color(Color(0xFF183342L)) } }
            Text {
                attr { text("查看中途回落依据"); fontFamily("sans-serif"); fontSize(14f); color(Color(0xFF176F91L)); marginTop(12f); marginBottom(12f) }
                event { click { change(LensFocus.EvidenceFocus("E2")) } }
            }
            MarketPlot(series, {
                when (val focus = value()) {
                    is LensFocus.EvidenceFocus -> PlotMark(range = 10..13)
                    is LensFocus.DayInspect -> PlotMark(inspected = series.indexOfFirst { it.date == focus.date })
                    LensFocus.Overview -> PlotMark()
                }
            }, tap) { change(LensFocus.DayInspect(it)) }
            Text {
                attr {
                    text(when(val f = value()) {
                        is LensFocus.DayInspect -> "检视 ${f.date} · 收盘 ${series.first { it.date == f.date }.closeMinor} 分"
                        is LensFocus.EvidenceFocus -> "08-24 至 08-27 · 11.50 → 10.80\n区间变化 -6.09%"
                        LensFocus.Overview -> "尚未选择日期"
                    })
                    fontFamily("sans-serif"); fontSize(13f); lines(0); lineHeight(22f); height(48f); color(Color(0xFF183342L)); marginTop(12f)
                }
            }
        }
    }
}
