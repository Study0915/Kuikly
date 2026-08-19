# Task 2 AI 股票问答 Demo 准备合同

## 启动条件与复用边界

Task 2 只有在新版 `T1-EXPERIENCE` 进入 `VERIFIED` 后才开始。它在现有 `shared` 模块增加 `finance_chat` 页面，不新建另一套应用；只复用届时已验证的新行情模型、详情路由和图表接口，不得引用 `archive/task1-v1/`。

Task 1、Task 2 使用相同的 Codex 主开发与四门验收管线。OpenCode 仅在用户明确指定，或 Codex 带日志阻塞并经用户确认后作为备选。

首个可验收版本继续离线运行：用户可输入问题、发送并查看多轮记录；Mock Chat Provider 返回 Markdown 说明和股票/指数结构化内容。真实模型、实时行情、联网检索、语音、登录和会话云同步均不在本阶段范围。

## 固定切片顺序

1. `T2-000`：Android/H5 最小聊天页。
2. `T2-VERTICAL`：消息模型、确定性 Mock Chat、发送状态和结构化内容首个切片。
3. `T2-EXPERIENCE`：Markdown、长会话、失败重试、行情卡片/图表承接和跨端体验。

## 公共模型与接口

实现时在 `shared/commonMain` 使用以下语义；引用的行情和图表类型必须来自新版 Task 1 的已验证接口，不得从归档复制：

```kotlin
enum class ChatRole { USER, ASSISTANT }
enum class ChatStatus { SENDING, COMPLETE, FAILED }
enum class MarketEntityKind { STOCK, INDEX }

data class MarketEntityRef(
    val kind: MarketEntityKind,
    val code: String,
    val name: String,
)

sealed interface ChatBlock {
    data class Markdown(val text: String) : ChatBlock
    data class QuoteCard(val entity: MarketEntityRef, val quote: StockQuote) : ChatBlock
    data class TrendChart(val entity: MarketEntityRef, val points: List<ChartPoint>) : ChatBlock
    data class Notice(val text: String) : ChatBlock
}

data class ChatMessage(
    val id: String,
    val role: ChatRole,
    val blocks: List<ChatBlock>,
    val status: ChatStatus,
)

data class ChatRequest(val query: String, val history: List<ChatMessage>)
data class ChatResponse(val blocks: List<ChatBlock>)

interface ChatProvider {
    suspend fun answer(request: ChatRequest): ChatResponse
}
```

`MockChatProvider` 必须确定性处理至少四类问题：个股概览、价格/涨跌查询、走势总结、风险提醒；未知代码返回可恢复提示，不伪造行情。每条分析都展示“仅作技术演示，不构成投资建议”。

## 页面与数据流

- 页面由标题、可滚动消息列表、推荐问题、输入框和发送按钮组成；发送中禁用重复提交，但允许查看历史消息。
- 提交非空问题后立即追加用户消息与 `SENDING` 助手占位；成功后原位替换为混合内容块，失败后标为 `FAILED` 并提供重试。
- `QuoteCard` 与 `TrendChart` 点击时调用新版 Task 1 的已验证详情路由；股票与指数由 `MarketEntityKind` 显式区分。
- Markdown 不执行原始 HTML、脚本或远程图片；外链只作为文本或经宿主确认后打开。结构化行情只来自本地模型/Provider，不从 Markdown 反解析。

## Markdown 兼容决策

KuiklyMarkdown 公开说明给出 `1.0.4-2.0.21` 依赖与流式渲染 API，但未声明 H5。因此实施顺序固定为：

1. 在独立 Task 2 分支添加依赖探针，运行 Android 编译、`:shared:compileKotlinJs` 和 `:h5App:jsBrowserProductionWebpack`。
2. 三项均通过时，Android/H5 共用 KuiklyMarkdown，并为标题、段落、列表、强调、代码块和链接增加 smoke test。
3. JS/H5 variant 不可用时，Android 保留 KuiklyMarkdown adapter；H5 使用受测的 commonMain 受限 renderer，只支持标题、段落、粗/斜体、列表、引用、行内代码和代码块，未知标记按纯文本显示。
4. Android 依赖可解析不代表 H5 支持；最终状态以实际编译和浏览器证据为准。

## 验收门禁

- 单测：空白输入、连续发送、成功/失败/重试、未知代码、确定性响应、免责声明、Markdown 转义、不执行 HTML 和内容块顺序。
- 路由：卡片/图表打开正确实体；未知实体不跳转；详情返回后会话仍保留。
- UI：长会话可滚动、键盘不遮挡输入、发送状态清晰、推荐问题可发送、涨跌颜色不是唯一语义。
- 构建：`scripts\verify.ps1` 全通过，并新增 Task 2 common/Android 测试；H5 完成 production bundle 和浏览器交互。
- 证据：Android 构建、H5 运行和设备运行分别记录；四门未齐不得进入 `VERIFIED`。
