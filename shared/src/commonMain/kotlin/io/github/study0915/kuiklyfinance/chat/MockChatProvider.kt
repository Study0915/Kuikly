package io.github.study0915.kuiklyfinance.chat

import io.github.study0915.kuiklyfinance.insight.*
import io.github.study0915.kuiklyfinance.market.*

/** Offline intent examples; never claims arbitrary questions were understood by a model. */
class MockChatProvider(private val market: MockMarketProvider = MockMarketProvider()) : ChatProvider {
    override fun reply(request: ChatRequest, now: Long): ChatReply {
        if (request.scenario == DemoScenario.FAIL_ONCE && request.attempt == 0)
            return ChatReply.Failed("演示：回答暂时不可用。重试会保留原问题与股票。")
        val question = request.question.uppercase()
        val explicit = Regex("(?:^|[^A-Z0-9_])(?:MOCK_|股票\\s*|示例\\s*)?([A-L])(?=$|[^A-Z0-9_])").findAll(question)
            .map { "MOCK_${it.groupValues[1]}" }.distinct().toList()
        val unknown = Regex("MOCK_([A-Z0-9]+)").findAll(question).any { it.value !in market.entityIds }
            || Regex("\\d{6}").containsMatchIn(question)
        val compare = listOf("比较", "对比").any { it in question }
        val ids = if (unknown) emptyList() else when {
            compare -> if (explicit.size >= 2) explicit.take(2) else listOf("MOCK_A", "MOCK_B")
            explicit.isNotEmpty() -> explicit.take(1)
            request.entityContext in market.entityIds -> listOf(request.entityContext!!)
            else -> emptyList()
        }
        if (ids.isEmpty()) return ChatReply.Ready(listOf(AnswerBlock.Markdown(
            "## 暂无匹配的演示股票\n仅支持 **示例股票 A–L**。试试“分析 A”“比较 A 和 B”或从卡片继续追问。\n> 这是离线 Mock 问答，不查询真实股票或实时资讯。")))
        val scenario = when {
            "缺量" in question || "缺失" in question -> DemoScenario.MISSING_VOLUME
            request.scenario == DemoScenario.FAIL_ONCE -> DemoScenario.COMPLETE
            else -> request.scenario
        }
        val docs = ids.mapNotNull { id ->
            (market.load(id, scenario = scenario, attempt = request.attempt) as? MarketLoad.Ready)?.document?.let { EvidenceResolver.resolve(it, now) }
        }
        if (docs.isEmpty()) return ChatReply.Failed("当前没有可用行情，请切换完整行情后重新提问。")
        val title = when { compare -> "同窗口对比"; "风险" in question -> "先核对风险边界"; "量" in question -> "量能需要完整样本"; else -> "先看事实，再看解释" }
        val explanation = when {
            compare -> "两张卡都采用 **20 个交易日** 的相同窗口。涨跌幅按各自起点计算；量能倍数按各自此前五日均量计算，不能按价格高低判断优劣。"
            "风险" in question -> "**历史上涨不等于未来收益。** 局部区间不等于最大回撤，量能不能证明资金意图；样例缺少基本面与实时信息。"
            "量" in question -> "比较目标日与此前 **连续五个窗口交易日**。缺少任何一天成交量就不计算倍数，也不跳过缺失日补样本。"
            else -> "下方结论来自固定行情的可复核计算。点选依据即可查看对应快照与区间；这不是预测或买卖建议。"
        }
        val blocks = mutableListOf<AnswerBlock>(AnswerBlock.Markdown("## $title\n$explanation\n- 数据：`历史 Mock`，非实时行情\n- 方法：结构化事实与规则模板，非真实模型\n> 仅作技术演示，不构成投资建议。"))
        blocks += docs.map { AnswerBlock.EvidenceCard(it) }
        return ChatReply.Ready(blocks)
    }
}
