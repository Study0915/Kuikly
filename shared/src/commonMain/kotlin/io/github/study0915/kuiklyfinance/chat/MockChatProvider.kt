package io.github.study0915.kuiklyfinance.chat

import io.github.study0915.kuiklyfinance.insight.*
import io.github.study0915.kuiklyfinance.market.*

/** Offline intent examples; never claims arbitrary questions were understood by a model. */
class MockChatProvider(private val market: MockMarketProvider = MockMarketProvider()) : ChatProvider {
    override fun reply(request: ChatRequest, now: Long): ChatReply {
        if (request.scenario == DemoScenario.FAIL_ONCE && request.attempt == 0)
            return ChatReply.Failed("演示：回答暂时不可用。重试会保留原问题与股票。")
        val question = request.question.uppercase()
        // Read whole ASCII tokens: an eight-digit date or quantity is not a six-digit stock code.
        // Keep unsupported single letters too, so "C 和 Z" cannot silently become a C answer.
        val tokens = Regex("[A-Z0-9_]+").findAll(question).toList()
        val explicit = tokens.mapNotNull { token -> when {
            token.value.startsWith("MOCK_") -> token.value
            token.value.length == 1 && token.value[0] in 'A'..'Z' -> "MOCK_${token.value}"
            else -> null
        } }.distinct()
        val unknown = explicit.any { it !in market.entityIds } || tokens.any { token ->
            token.value.length == 6 && token.value.all { it.isDigit() } &&
                !Regex("^(股|手|元|万|亿|倍)").containsMatchIn(question.substring(token.range.last + 1).trimStart())
        }
        val compare = listOf("比较", "对比").any { it in question }
        if (unknown) return prompt("暂无匹配的演示股票", "问题包含演示范围外的股票。仅支持 **示例股票 A–L**，请更正后再试。")
        if (compare && explicit.size != 2) return prompt("请指定两只不同的演示股票",
            "同窗口对比需要明确的两个对象，例如 **比较 C 和 D**。当前不会补入其他股票，也不会忽略多出的对象。")
        if (!compare && explicit.size > 1) return prompt("请选择单股分析或双股对比",
            "单股分析请指定一只股票；要看两只股票，请输入 **比较 A 和 B**。")
        val ids = when {
            explicit.isNotEmpty() -> explicit
            request.entityContext in market.entityIds -> listOf(request.entityContext!!)
            else -> emptyList()
        }
        if (ids.isEmpty()) return prompt("暂无匹配的演示股票", "仅支持 **示例股票 A–L**。试试“分析 A”“比较 A 和 B”或从卡片继续追问。")
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

    private fun prompt(title: String, detail: String) = ChatReply.Ready(listOf(AnswerBlock.Markdown(
        "## $title\n$detail\n> 这是离线 Mock 问答，不查询真实股票或实时资讯。")))
}
