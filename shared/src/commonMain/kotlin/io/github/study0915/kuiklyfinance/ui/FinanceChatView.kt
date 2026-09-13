package io.github.study0915.kuiklyfinance.ui

import com.tencent.kuikly.core.base.*
import com.tencent.kuikly.core.directives.*
import com.tencent.kuikly.core.views.*
import io.github.study0915.kuiklyfinance.chat.*
import io.github.study0915.kuiklyfinance.insight.*
import io.github.study0915.kuiklyfinance.market.*
import io.github.study0915.kuiklyfinance.navigation.FinanceRoute

internal fun ViewContainer<*, *>.FinanceChat(
    chat: ChatController, later: (Int, () -> Unit) -> Unit, onRequest: (ChatRequest) -> Unit,
    onDetail: (FinanceRoute.Detail) -> Unit, onHome: () -> Unit,
) {
    val mounted = chat.attachView()
    val turnViews = mutableMapOf<Int, DeclarativeBaseView<*, *>>()
    fun send(question: String, entity: String? = null, fromDraft: Boolean = false) {
        chat.begin(question, entity, fromDraft)?.let { ticket ->
            onRequest(ticket)
            later(60) { if (chat.acceptsView(mounted) && chat.session.turns.lastOrNull()?.request == ticket) chat.jumpToLatest?.invoke() }
        }
    }
    View {
        attr { padding(4f, 16f, 4f, 16f); backgroundColor(Color.WHITE) }
        View {
            attr { flexDirectionRow(); justifyContentSpaceBetween(); alignItemsCenter() }
            FinanceText({ "证据问答" }, 22f, strong = true)
            FinanceIconAction("演示设置", FinanceIcon.MORE) { chat.demoPanelOpen = !chat.demoPanelOpen }
        }
        FinanceText({ "历史 Mock · 结论与行情，一起核对" }, 12f, financeMuted)
    }
    vif({ chat.demoPanelOpen }) {
        View {
            attr { padding(12f, 16f, 12f, 16f); backgroundColor(FinanceTheme.tint); accessibility("演示设置面板") }
            View { attr { flexDirectionRow(); alignItemsCenter(); justifyContentSpaceBetween() }
                FinanceText({ "回答场景" }, 16f, strong = true)
                FinanceAction("关闭演示设置", FinanceActionStyle.QUIET) { chat.demoPanelOpen = false }
            }
            View {
                attr { height(48f); justifyContentCenter(); accessibility("切换回答场景") }
                event { click { chat.scenario = if (chat.scenario == DemoScenario.COMPLETE) DemoScenario.FAIL_ONCE else DemoScenario.COMPLETE; chat.demoPanelOpen = false } }
                FinanceText({ if (chat.scenario == DemoScenario.COMPLETE) "正常演示 · 点击改为首次失败" else "首次失败 · 点击恢复正常演示" }, 14f, financeBlue)
            }
            FinanceAction("新建会话", FinanceActionStyle.QUIET) { chat.reset(); turnViews.clear(); chat.demoPanelOpen = false; chat.jumpToLatest?.invoke() }
        }
    }
    List {
        val list = this
        var navigationRevision = 0
        // At most 20 turns: measure all anchors on mount; DOM rendering remains virtualized.
        attr { flex(1f); firstContentLoadMaxIndex(ChatSession.MAX_TURNS + 2); accessibility("问答会话记录") }
        event { scroll { if (chat.acceptsView(mounted)) chat.offset = it.offsetY } }
        chat.jumpToLatest = {
            if (chat.acceptsView(mounted)) {
                navigationRevision++
                val latest = chat.turns.lastOrNull()?.request?.turnId
                val top = turnViews[latest]?.frame?.y ?: 0f
                val maxOffset = ((list.contentView?.frame?.height ?: 0f) - list.frame.height).coerceAtLeast(0f)
                list.setContentOffset(0f, top.coerceIn(0f, maxOffset), false)
            }
        }
        View {
            attr { padding(20f, 16f, 16f, 16f) }
            FinanceText({ "让每个结论，都有依据。" }, 21f, strong = true)
            FinanceText({ "提问 A–L，点击回答中的数值回看行情。" }, 13f, financeMuted)
            View { attr { flexDirectionRow(); marginTop(8f) }
                View { attr { flex(1f); marginRight(8f) }; FinanceAction("分析 A 的走势") { send("分析 A 的走势") } }
                View { attr { flex(1f) }; FinanceAction("比较 A 和 B") { send("比较 A 和 B") } }
            }
            View { attr { marginTop(8f) }; FinanceText({ "规则模板生成 · 不连接真实模型 · 刷新清空会话" }, 12f, financeMuted) }
        }
        vfor({ chat.turns }) { turn ->
            View {
            turnViews[turn.request.turnId] = this
            View {
                attr { margin(4f, 16f, 16f, 48f); padding(12f, 16f, 12f, 16f); backgroundColor(FinanceTheme.tint); borderRadius(14f); accessibility("问题 ${turn.request.turnId}") }
                FinanceText({ "你 · ${turn.request.turnId.toString().padStart(2, '0')}" }, 12f, financeBlue)
                FinanceText({ turn.request.question }, 16f)
            }
            View {
                attr { margin(0f, 16f, 24f, 16f); accessibility("回答 ${turn.request.turnId}") }
                FinanceText({ "证据助手 · Mock" }, 12f, financeBlue, true)
                when (turn.status) {
                    TurnStatus.PENDING -> {
                        FinanceText({ "正在整理行情依据…" }, 16f)
                        FinanceAction("取消本次回答") { chat.session.cancel(); chat.sync() }
                    }
                    TurnStatus.CANCELLED, TurnStatus.FAILED -> {
                        FinanceText({ turn.notice }, 15f)
                        FinanceAction("重试问题 ${turn.request.turnId}") { chat.session.retry(turn.request.turnId)?.let { onRequest(it) }; chat.sync() }
                    }
                    TurnStatus.READY -> turn.blocks.forEach { block -> when (block) {
                        is AnswerBlock.Markdown -> SafeMarkdownView(block.source)
                        is AnswerBlock.EvidenceCard -> EvidenceAnswerCard(block.document, chat.cardState(turn.request.turnId, block.document.key), onDetail) { question, entity -> send(question, entity) }
                    } }
                }
            }
            }
        }
        View { attr { height(16f) } }
        val restore = chat.offset
        val restoreFirstTurn = chat.turns.firstOrNull()?.request?.turnId
        later(60) {
            if (chat.acceptsView(mounted) && navigationRevision == 0 && chat.turns.firstOrNull()?.request?.turnId == restoreFirstTurn)
                list.setContentOffset(0f, restore, false)
        }
    }
    View {
        attr { padding(8f, 16f, 8f, 16f); backgroundColor(Color.WHITE); accessibility("问答输入区") }
        vif({ chat.notice.isNotEmpty() }) { FinanceText({ chat.notice }, 12f, Color(0xFFB94B40L)) }
        View {
            attr { flexDirectionRow(); alignItemsCenter() }
            View {
                attr { flex(1f); padding(8f, 12f, 8f, 12f); backgroundColor(FinanceTheme.background); borderRadius(14f) }
                TextArea {
                attr { height(42f)
                    text(chat.draft); placeholder("输入问题，如：分析 A 的风险"); fontSize(15f); color(financeInk); accessibility("股票问题输入") }
                event { textDidChange { chat.draft = it.text } }
                }
            }
            View {
                attr { width(64f); height(58f); marginLeft(8f); allCenter(); borderRadius(14f)
                    backgroundColor(if (chat.pending) financeMuted else financeBlue); accessibility("发送问题") }
                event { click { send(chat.draft, fromDraft = true) } }
                FinanceText({ if (chat.pending) "等待中" else "发送" }, 15f, Color.WHITE)
            }
        }
        View {
            attr { flexDirectionRow(); alignItemsCenter(); justifyContentSpaceBetween() }
            View { attr { flex(1f) }; FinanceText({ Task1ShellContract.DISCLAIMER }, 12f, financeMuted) }
            View { attr { padding(4f); height(48f); justifyContentCenter(); accessibility("定位最新回答") }; event { click { chat.jumpToLatest?.invoke() } }; FinanceText({ "最新 ↓" }, 12f, financeBlue) }
        }
    }
}

internal fun ViewContainer<*, *>.SafeMarkdownView(source: String) {
    SafeMarkdown.parse(source).forEach { line ->
        RichText {
            attr { fontSize(if (line.kind == MarkdownKind.HEADING) 18f else 15f); lineHeight(24f); lines(0)
                color(if (line.kind == MarkdownKind.QUOTE) financeMuted else financeInk); marginTop(8f); fontFamily(FinanceTheme.font(pagerData.isWeb)) }
            line.spans.forEach { part -> Span {
                text(part.text)
                if (part.bold || line.kind == MarkdownKind.HEADING) fontWeightBold()
                if (part.code) { fontFamily("monospace"); color(financeBlue) }
            } }
        }
    }
}

/** Reused for single-stock, comparison and missing-data replies. No navigation globals. */
internal fun ViewContainer<*, *>.EvidenceAnswerCard(doc: ResolvedDocument, state: AnswerCardState,
    onDetail: (FinanceRoute.Detail) -> Unit, onFollowUp: (String, String) -> Unit,
) {
    val stock = doc.document.snapshot
    val short = stock.entityId.last().toString()
    val presenter = LensPresenter(doc)
    val tap = PlotTap()
    fun detail(focus: LensFocus? = null) = onDetail(FinanceRoute.Detail(stock.entityId, stock.snapshotId, focus, fromChat = true))
    View {
        attr { marginTop(12f); padding(14f); backgroundColor(Color.WHITE); borderRadius(14f); accessibility("$short 行情证据卡") }
        View { attr { flexDirectionRow(); justifyContentSpaceBetween(); alignItemsCenter() }
            View { attr { flex(1f) }; FinanceText({ stock.name }, 16f, strong = true); FinanceText({ stock.entityId }, 12f, financeMuted) }
            FinancePrice(MarketFormatter.price(doc.bars.last().closeMinor), 24f)
        }
        FinanceText({ "${stock.asOf.take(16).replace('T', ' ')} · 历史 Mock" }, 12f, financeMuted)
        doc.evidence.forEach { evidence ->
            val fact = presenter.present(LensState.initial(doc, LensFocus.EvidenceFocus(evidence.evidence.id)))
            View {
                attr { minHeight(56f); marginTop(4f); paddingTop(8f); paddingBottom(8f)
                    accessibility("$short · 核对${evidence.evidence.label}"); touchEnable(evidence.available) }
                event { click { if (evidence.available) detail(LensFocus.EvidenceFocus(evidence.evidence.id)) } }
                View { attr { flexDirectionRow(); alignItemsCenter(); justifyContentSpaceBetween() }
                    FinanceText({ evidence.evidence.label + if (evidence.available) " ›" else " · 暂不可用" }, 13f, if (evidence.available) financeBlue else financeMuted)
                    if (evidence.available) FinanceText({ fact.value.removePrefix("区间变化 ").removePrefix("量能倍数 ") }, 18f, strong = true)
                }
                if (!evidence.available) FinanceText({ evidence.reason ?: "依据不可用" }, 12f, financeMuted)
            }
            View { attr { height(1f); backgroundColor(FinanceTheme.line) } }
        }
        View { attr { marginTop(8f); marginBottom(4f) }; FinanceText({ doc.freshness.text }, 12f, financeMuted) }
        FinanceText({ "风险：历史数值不预测后市；局部区间不是最大回撤，量能不证明涨跌原因。" }, 12f, financeMuted)
        FinanceAction("$short · 查看行情详情", FinanceActionStyle.PRIMARY) { detail() }
        FinanceAction("$short · 展开/收起走势", FinanceActionStyle.QUIET) { state.expanded = !state.expanded; tap.reset() }
        vif({ state.expanded }) {
            FinanceText({ "20 日走势 · 点按交易日进入详情核对" }, 12f, financeBlue)
            MarketPlot(doc.bars, { PlotMark() }, tap) { date -> detail(LensFocus.DayInspect(date)) }
        }
        View {
            attr { flexDirectionRow() }
            View { attr { flex(1f); marginRight(4f) }; FinanceAction("追问 $short 风险", FinanceActionStyle.QUIET) { onFollowUp("这只股票有什么风险？", stock.entityId) } }
            View { attr { flex(1f) }; FinanceAction("检验 $short 缺量", FinanceActionStyle.QUIET) { onFollowUp("如果量能缺失呢？", stock.entityId) } }
        }
    }
}
