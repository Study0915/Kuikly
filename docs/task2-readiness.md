# Task 2 AI 股票问答 Demo 准备合同

## 目标与复用边界

Task 2 在现有 `shared` 模块增加 `finance_chat` 页面，不新建另一套应用。它复用 `MarketDataSource`、`stock_detail` 路由和 `KuiklyChart`；Task 1 的行情列表、详情页、Mock 分析及图表回归必须保持通过。

首个可验收版本继续离线运行：用户可输入问题、发送并查看多轮记录；Mock Chat Provider 返回 Markdown 说明和股票/指数结构化内容，内容可点击进入详情承接页。真实模型、实时行情、联网检索、语音、登录和会话云同步均不在本阶段范围。

## 公共模型与接口

实现时在 `shared/commonMain` 使用以下语义，命名可按 Kotlin 规范微调，但不得把结构化卡片编码进 Markdown 字符串：

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

- 页面由顶部标题、可滚动消息列表、推荐问题、底部输入框和发送按钮组成；发送中禁用重复提交，但允许查看历史消息。
- 提交非空问题后立即追加用户消息与 `SENDING` 助手占位；Provider 成功后原位替换为混合内容块，失败后标为 `FAILED` 并提供重试。
- `QuoteCard` 与 `TrendChart` 的点击事件统一调用现有 `RouterModule.openPage("stock_detail", code)`；详情页继续作为股票承接页。指数在首版可承接到复用详情模板，但必须由 `MarketEntityKind` 明确区分，不能假装为个股。
- Markdown 不允许原始 HTML、脚本、远程图片自动执行；外链只作为文本或经宿主确认后打开。结构化行情永远来自本地模型/Provider，不从 Markdown 反解析。

## Markdown 兼容决策

KuiklyMarkdown 当前公开 README 给出 `1.0.4-2.0.21` 依赖与流式渲染 API，但只声明 Android、iOS、HarmonyOS，未声明 H5。因此实施顺序固定为：

1. 在独立 Task 2 分支添加依赖探针，运行 Android 编译和 `:shared:compileKotlinJs`、`:h5App:jsBrowserProductionWebpack`。
2. 三项均通过时，Android/H5 共用 KuiklyMarkdown，并为标题、段落、列表、强调、代码块、链接加 smoke test。
3. JS/H5 variant 不可用时，不阻断 Task 2：保留 Android 的 KuiklyMarkdown adapter；H5 使用 `commonMain` 的受限 Markdown block renderer，只支持标题、段落、粗/斜体、无序/有序列表、引用、行内代码和代码块，未知标记按纯文本显示。
4. 不把“Android 依赖可解析”写成“H5 已支持”；最终状态必须以实际编译和浏览器截图为准。

## 验收门禁

- 单测：空白输入、连续发送、成功/失败/重试、未知代码、确定性响应、免责声明、Markdown 转义与不执行 HTML、混合内容块顺序。
- 路由：股票卡片/图表打开正确 `code` 的详情页，未知实体不跳转；详情返回后会话仍保留。
- UI：长会话可滚动、键盘不遮挡输入、发送中状态清晰、推荐问题可填入并发送、涨跌颜色不作为唯一语义。
- 构建：现有 `scripts\verify.ps1` 全通过，并新增 Task 2 common/Android 测试；H5 必须完成 production bundle 和浏览器手工交互。
- 证据：Android 构建与 H5 运行分开记录；未在真机/模拟器运行时只写“APK 构建通过”，不得写“Android 运行通过”。
