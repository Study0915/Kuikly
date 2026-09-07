package io.github.study0915.kuiklyfinance.ui

import com.tencent.kuikly.core.annotations.Page
import com.tencent.kuikly.core.base.*
import com.tencent.kuikly.core.datetime.DateTime
import com.tencent.kuikly.core.module.BackPressModule
import com.tencent.kuikly.core.module.NotifyModule
import com.tencent.kuikly.core.nvi.serialization.json.JSONObject
import com.tencent.kuikly.core.pager.Pager
import com.tencent.kuikly.core.reactive.handler.observable
import com.tencent.kuikly.core.timer.setTimeout
import com.tencent.kuikly.core.views.*
import com.tencent.kuikly.core.directives.vif
import com.tencent.kuikly.core.directives.velse
import io.github.study0915.kuiklyfinance.insight.*
import io.github.study0915.kuiklyfinance.market.*
import io.github.study0915.kuiklyfinance.navigation.FinanceRoute

internal val financeInk = Color(0xFF183342L)
internal val financeMuted = Color(0xFF677C87L)
internal val financeBlue = Color(0xFF176F91L)

internal fun ViewContainer<*, *>.FinanceText(value: () -> String, size: Float = 14f, ink: Color = financeInk) {
    Text { attr { text(value()); fontFamily("sans-serif"); fontSize(size); lineHeight(size * 1.55f); lines(0); color(ink) } }
}
internal fun ViewContainer<*, *>.FinanceAction(label: String, action: () -> Unit) {
    View {
        attr { padding(12f); marginTop(8f); backgroundColor(Color(0xFFE7F1F5L)); borderRadius(8f); accessibility(label) }
        event { click { action() } }
        FinanceText({ label }, 14f, financeBlue)
    }
}

@Page(Task1Routes.FINANCE_HOME, supportInLocal = true)
class FinanceHomePage : Pager() {
    private val provider = MockMarketProvider()
    private val requests = FinanceRequests()
    private var route by observable<FinanceRoute>(FinanceRoute.Home)
    private var load by observable<MarketLoad>(MarketLoad.Loading)
    private var model: ResolvedDocument? = null
    private var lens by observable<LensUiState?>(null)
    private var scenario = DemoScenario.COMPLETE
    private var attempt = 0
    private var homeOffset = 0f
    private val tap = PlotTap()

    override fun body(): ViewBuilder {
        val ctx = this
        return {
            attr { backgroundColor(Color(0xFFF1F5F7L)) }
            vif({ ctx.route == FinanceRoute.Home }) { ctx.home(this) }
            velse {
                View {
                    attr { padding(12f); backgroundColor(Color.WHITE) }
                    FinanceAction("‹ 返回行情列表") { ctx.back() }
                }
                vif({ ctx.load is MarketLoad.Ready }) {
                    val doc = ctx.model!!
                    FinanceDetail(doc, { ctx.lens!! }, ctx.tap, ctx.scenario,
                        onScroll = { ctx.tap.cancel() },
                        onAction = { event -> ctx.lens = LensState.reduce(doc, ctx.lens!!, doc.key, event) },
                        onScenario = { selected -> ctx.switchScenario(selected) })
                }
                velse { ctx.loadState(this) }
            }
        }
    }

    private fun home(parent: ViewContainer<*, *>) {
        val ctx = this
        parent.View {
            attr { padding(20f); backgroundColor(Color.WHITE) }
            FinanceText({ "行情观察" }, 26f)
            FinanceText({ "12 支示例股票 · 历史 Mock" }, 13f, financeMuted)
            FinanceText({ "从一段解读，回到它的行情依据。" }, 14f, financeBlue)
        }
        parent.List {
            val list = this
            val savedOffset = ctx.homeOffset
            attr { flex(1f); accessibility("行情列表，12 支示例股票") }
            event { scroll { if (ctx.route == FinanceRoute.Home) ctx.homeOffset = it.offsetY } }
            ctx.provider.entityIds.forEach { id ->
                val doc = (ctx.provider.load(id) as MarketLoad.Ready).document
                val bars = doc.snapshot.bars; val last = bars.last()
                val change = MarketFormatter.change(bars[bars.lastIndex - 1], last)!!
                View {
                    attr { margin(8f, 12f, 0f, 12f); padding(16f); borderRadius(12f); backgroundColor(Color.WHITE); accessibility("查看${doc.snapshot.name}") }
                    event { click { ctx.open(FinanceRoute.Detail(id)) } }
                    View {
                        attr { flexDirectionRow(); justifyContentSpaceBetween() }
                        FinanceText({ doc.snapshot.name }, 17f)
                        FinanceText({ MarketFormatter.price(last.closeMinor) }, 22f)
                    }
                    FinanceText({ "当日涨跌额 ${MarketFormatter.decimal((last.closeMinor - bars[bars.lastIndex - 1].closeMinor) / 100.0, true)} 元" }, 12f, financeMuted)
                    View {
                        attr { flexDirectionRow(); justifyContentSpaceBetween() }
                        FinanceText({ "$id · 查看依据 ›" }, 12f, financeMuted)
                        FinanceText({ "当日${MarketFormatter.direction(change)} ${MarketFormatter.percent(change)}" }, 13f,
                            if (change >= 0) Color(0xFFB94B40L) else Color(0xFF188579L))
                    }
                }
            }
            View { attr { padding(20f) }; FinanceText({ Task1ShellContract.DISCLAIMER }, 12f, financeMuted) }
            ctx.setTimeout(30) { if (ctx.route == FinanceRoute.Home) list.setContentOffset(0f, savedOffset, false) }
        }
    }

    private fun loadState(parent: ViewContainer<*, *>) {
        val ctx = this
        parent.View {
            attr { margin(16f); padding(20f); backgroundColor(Color.WHITE); borderRadius(12f) }
            FinanceText({ when (val status = ctx.load) {
                MarketLoad.Loading -> "正在载入历史 Mock 行情…"
                MarketLoad.Empty -> "暂无演示行情"
                is MarketLoad.Failed -> status.reason
                is MarketLoad.UnknownEntity -> "无法识别该股票：${status.entityId}"
                is MarketLoad.SnapshotUnavailable -> "原快照不可用：${status.entityId} / ${status.snapshotId}"
                is MarketLoad.Ready -> ""
            } }, 17f)
            vif({ ctx.load is MarketLoad.Failed || ctx.load == MarketLoad.Empty }) {
                FinanceAction("重试当前股票") { ctx.attempt++; ctx.request() }
                FinanceAction("恢复完整演示行情") { ctx.switchScenario(DemoScenario.COMPLETE) }
            }
            FinanceText({ "历史 Mock · ${Task1ShellContract.DISCLAIMER}" }, 12f, financeMuted)
        }
    }

    private fun open(detail: FinanceRoute.Detail, notifyHost: Boolean = true) {
        route = detail; scenario = DemoScenario.COMPLETE; attempt = 0
        if (notifyHost) notifyRoute("detail", detail.entityId, detail.snapshotId)
        request()
    }
    private fun switchScenario(selected: DemoScenario) {
        val current = route as? FinanceRoute.Detail ?: return
        scenario = selected; attempt = 0
        route = current.copy(snapshotId = provider.snapshotId(current.entityId, selected), focus = null)
        request()
    }
    private fun request() {
        val detail = route as? FinanceRoute.Detail ?: return
        val ticket = requests.begin(detail, scenario, attempt)
        load = MarketLoad.Loading; tap.reset()
        setTimeout(150) {
            if (!requests.accepts(ticket)) return@setTimeout
            val result = provider.load(detail.entityId, detail.snapshotId, ticket.scenario, ticket.attempt)
            if (!requests.accepts(ticket)) return@setTimeout
            if (result is MarketLoad.Ready) {
                val resolved = EvidenceResolver.resolve(result.document, DateTime.currentTimestamp())
                model = resolved; lens = LensState.initial(resolved, detail.focus)
                load = if (resolved.error == null) result else MarketLoad.Failed(resolved.error)
            } else load = result
        }
    }
    private fun back(notifyHost: Boolean = true): Boolean {
        if (route == FinanceRoute.Home) return false
        requests.cancel(); tap.reset(); route = FinanceRoute.Home; load = MarketLoad.Loading
        if (notifyHost) notifyRoute("home")
        return true
    }
    private fun notifyRoute(destination: String, entity: String = "", snapshot: String? = null) {
        acquireModule<NotifyModule>(NotifyModule.MODULE_NAME).postNotify(Task1Routes.HOST_ROUTE_EVENT,
            JSONObject().put("route", destination).put("entityId", entity).put("snapshotId", snapshot ?: ""))
    }
    override fun onReceivePagerEvent(pagerEvent: String, eventData: JSONObject) {
        super.onReceivePagerEvent(pagerEvent, eventData)
        when (pagerEvent) {
            "onBackPressed" -> acquireModule<BackPressModule>(BackPressModule.MODULE_NAME).backHandle(back())
            Task1Routes.HOST_BACK_EVENT -> back(false)
            Task1Routes.HOST_OPEN_EVENT -> {
                val evidence = eventData.optString("evidenceId"); val date = eventData.optString("date")
                val focus = when { evidence.isNotEmpty() -> LensFocus.EvidenceFocus(evidence); date.isNotEmpty() -> LensFocus.DayInspect(date); else -> null }
                open(FinanceRoute.Detail(eventData.optString("entityId"), eventData.optString("snapshotId").ifEmpty { null }, focus), false)
            }
        }
    }
    override fun pageWillDestroy() { requests.cancel(); tap.reset(); super.pageWillDestroy() }
}
