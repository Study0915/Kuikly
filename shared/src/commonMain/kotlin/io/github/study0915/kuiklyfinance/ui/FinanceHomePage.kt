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
import io.github.study0915.kuiklyfinance.chat.*

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
    private val session = FinanceSession()
    private var route by observable<FinanceRoute>(FinanceRoute.Home)
    private var load by observable<MarketLoad>(MarketLoad.Loading)
    private var content by observable<FinanceContent?>(null)
    private var scenario = DemoScenario.COMPLETE
    private var attempt = 0
    private var homeOffset = 0f
    private val tap = PlotTap()
    private val chat = ChatController()
    private val chatProvider: ChatProvider = MockChatProvider(provider)

    override fun body(): ViewBuilder {
        val ctx = this
        return {
            attr { backgroundColor(Color(0xFFF1F5F7L)) }
            vif({ ctx.route == FinanceRoute.Home }) { ctx.home(this) }
            vif({ ctx.route == FinanceRoute.Chat }) {
                FinanceChat(ctx.chat, { delay, action -> ctx.setTimeout(delay) { action() } },
                    { ctx.answer(it) }, { ctx.open(it) }, { ctx.back() })
            }
            vif({ ctx.route is FinanceRoute.Detail }) {
                View {
                    attr { padding(12f); backgroundColor(Color.WHITE) }
                    vif({ (ctx.route as? FinanceRoute.Detail)?.fromChat == true }) { FinanceAction("‹ 返回问答会话") { ctx.back() } }
                    velse { FinanceAction("‹ 返回行情列表") { ctx.back() } }
                }
                vif({ ctx.load is MarketLoad.Ready }) {
                    val mounted = ctx.content!!
                    FinanceDetail(mounted.document, { ctx.content?.takeIf { it.request == mounted.request }?.lens ?: mounted.lens }, ctx.tap, ctx.scenario,
                        onScroll = { ctx.tap.cancel() },
                        onAction = { event -> ctx.dispatch(mounted.request, event) },
                        onScenario = { selected -> if (ctx.session.accepts(mounted.request)) ctx.switchScenario(selected) })
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
            FinanceAction("打开证据问答") { ctx.route = FinanceRoute.Chat; ctx.notifyRoute("push") }
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
        if (route == FinanceRoute.Chat) chat.detachView()
        route = detail
        scenario = detail.snapshotId?.let { provider.scenarioForSnapshot(detail.entityId, it) } ?: DemoScenario.COMPLETE
        attempt = 0
        if (notifyHost) notifyRoute("push")
        request()
    }
    private fun switchScenario(selected: DemoScenario) {
        val current = route as? FinanceRoute.Detail ?: return
        scenario = selected; attempt = 0
        route = current.copy(snapshotId = provider.snapshotId(current.entityId, selected), focus = null)
        notifyRoute("replace")
        request()
    }
    private fun request() {
        val detail = route as? FinanceRoute.Detail ?: return
        val ticket = session.begin(detail, scenario, attempt)
        load = MarketLoad.Loading; content = null; tap.reset()
        setTimeout(150) {
            if (!session.accepts(ticket)) return@setTimeout
            val result = provider.load(detail.entityId, detail.snapshotId, ticket.scenario, ticket.attempt)
            if (!session.accepts(ticket)) return@setTimeout
            if (result is MarketLoad.Ready) {
                val resolved = EvidenceResolver.resolve(result.document, DateTime.currentTimestamp())
                content = session.complete(ticket, resolved)
                load = if (content != null) result else MarketLoad.Failed(resolved.error ?: "行情快照为空或与当前请求不匹配。")
                content?.let { syncSelection(it) }
            } else load = result
        }
    }
    private fun back(notifyHost: Boolean = true): Boolean {
        if (route == FinanceRoute.Home) return false
        if (route == FinanceRoute.Chat) chat.detachView()
        val destination = if ((route as? FinanceRoute.Detail)?.fromChat == true) FinanceRoute.Chat else FinanceRoute.Home
        session.cancel(); tap.reset(); route = destination; load = MarketLoad.Loading; content = null
        if (notifyHost) notifyRoute("back")
        return true
    }
    private fun answer(ticket: ChatRequest) {
        val mounted = chat.currentView()
        chat.sync()
        setTimeout(600) {
            if (!chat.session.accepts(ticket)) return@setTimeout
            val result = chatProvider.reply(ticket, DateTime.currentTimestamp())
            chat.session.complete(ticket, result); chat.sync()
            setTimeout(60) {
                if (chat.acceptsView(mounted) && chat.session.turns.lastOrNull()?.request == ticket) chat.jumpToLatest?.invoke()
            }
        }
    }
    private fun dispatch(origin: FinanceRequest, action: LensAction) {
        val before = content
        val next = session.dispatch(origin, action)
        if (next == before) return
        content = next
        next?.let { syncSelection(it) }
    }
    private fun syncSelection(current: FinanceContent) {
        route = FinanceRoute.Detail(current.document.key.entityId, current.document.key.snapshotId, current.lens.focus,
            fromChat = (route as? FinanceRoute.Detail)?.fromChat == true)
        notifyRoute("replace")
    }
    private fun notifyRoute(operation: String) {
        val detail = route as? FinanceRoute.Detail
        acquireModule<NotifyModule>(NotifyModule.MODULE_NAME).postNotify(Task1Routes.HOST_ROUTE_EVENT,
            JSONObject().put("route", if (route == FinanceRoute.Chat) "chat" else if (detail == null) "home" else "detail").put("operation", operation)
                .put("fromChat", if (detail?.fromChat == true) "1" else "")
                .put("entityId", detail?.entityId ?: "").put("snapshotId", detail?.snapshotId ?: "")
                .put("date", (detail?.focus as? LensFocus.DayInspect)?.date ?: "")
                .put("evidenceId", (detail?.focus as? LensFocus.EvidenceFocus)?.evidenceId ?: "")
                .put("overview", if (detail?.focus == LensFocus.Overview) "1" else ""))
    }
    override fun onReceivePagerEvent(pagerEvent: String, eventData: JSONObject) {
        super.onReceivePagerEvent(pagerEvent, eventData)
        when (pagerEvent) {
            "onBackPressed" -> acquireModule<BackPressModule>(BackPressModule.MODULE_NAME).backHandle(back())
            Task1Routes.HOST_BACK_EVENT -> {
                if (route == FinanceRoute.Chat) chat.detachView()
                session.cancel(); tap.reset(); route = FinanceRoute.Home; load = MarketLoad.Loading; content = null
            }
            Task1Routes.HOST_CHAT_EVENT -> {
                session.cancel(); tap.reset(); route = FinanceRoute.Chat; load = MarketLoad.Loading; content = null
            }
            Task1Routes.HOST_OPEN_EVENT -> {
                val evidence = eventData.optString("evidenceId"); val date = eventData.optString("date")
                val focus = when { eventData.optString("overview") == "1" -> LensFocus.Overview; evidence.isNotEmpty() -> LensFocus.EvidenceFocus(evidence); date.isNotEmpty() -> LensFocus.DayInspect(date); else -> null }
                open(FinanceRoute.Detail(eventData.optString("entityId"), eventData.optString("snapshotId").ifEmpty { null }, focus,
                    fromChat = eventData.optString("fromChat") == "1"), false)
            }
        }
    }
    override fun pageWillDestroy() { session.cancel(); chat.session.reset(); chat.detachView(); tap.reset(); super.pageWillDestroy() }
}
