# Shape with AI：GitHub 项目灵感库与创新组合方法

> 调研日期：2026-08-22（Asia/Shanghai）
> 用途：给 Task 1 / Task 2 做头脑风暴，不替代详细计划，也不增加题面 Must。
> 范围权威：仍以 [`../REQUIREMENTS.md`](../REQUIREMENTS.md) 为唯一题面与完成定义。
> 前置材料：导师重点见 [`2026-08-21-shape-brief-teacher-video-human.md`](2026-08-21-shape-brief-teacher-video-human.md)；更广的一手资料见 [`2026-08-21-task1-task2-inspiration-sources.md`](2026-08-21-task1-task2-inspiration-sources.md)。

## 1. 先回答“大家都有 AI，怎样做出创新”

你的担心是对的：直接问模型“给我想一个创新功能”，很容易得到相同的答案——情绪分析、智能荐股、K 线预测、新闻摘要、聊天机器人。问题不在于 AI 不会想，而在于它缺少你的取舍、真实约束和一手观察。

更可靠的方法是 **证据驱动的重组**：

1. 先从真实项目里收集已经被做出来的交互，而不是收集功能名。
2. 把项目拆成小的“交互原子”，例如：证据引用、反方观点、时效标记、可展开解释、置信度来源、失败降级、结构化卡片。
3. 选一个股票用户真正容易困惑的问题，例如“AI 为什么这样判断”“这条判断还能信多久”“哪些事实支持它”。
4. 把 2～3 个交互原子重新组合，并加入 Kuikly 跨端、Mock-first、可复现、可讲解等本题约束。
5. 最后用 20 秒演示检验：评委是否能看见输入、变化、解释和结果，而不是只听你口头讲创意。

可以把创新写成一个公式：

> **创新候选 = 已验证的交互 A + 股票场景痛点 B + 可验证反馈 C + 本项目独有约束 D**

例子：

> 新闻的时间标记 + 用户不知道 AI 结论是否过期 + 点击查看证据 + 离线确定性 Mock
> = “有保质期的 AI 行情洞察卡”

这不是让 AI 替你决定创意，而是让 AI 帮你做检索、归类和反驳；最后选哪个痛点、删掉什么、怎样演示，仍然是你的产品判断。

## 2. 怎样阅读下面的项目

### 2.1 迁移等级

| 等级 | 含义 | 对当前 Kuikly 项目的现实解释 |
|---|---|---|
| M3 | 可直接核对并适配源码 | 主要是 Kuikly 官方示例、同 DSL 组件；仍需按项目固定的 Kuikly UI 2.4.0 编译和双端验证 |
| M2 | 数据模型、算法或测试可改写 | Kotlin/KMP 项目最接近，但 Compose 组件不能直接粘进 Kuikly DSL |
| M1 | 只借产品和交互 | React、Rust、Python、Tauri 等项目；应重新设计和实现，不复制界面 |
| M0 | 只用于反例或边界研究 | 规模、依赖、许可或金融风险不适合活动 Demo |

### 2.2 许可证提醒

- “开源”不等于可以任意复制。采用源码前必须再次核对仓库根目录 `LICENSE`、NOTICE、第三方依赖和当前提交。
- AGPL 项目尤其适合研究产品思路，不建议在本次活动里复制源码形成许可证负担。
- 即使是 MIT/Apache-2.0，仍要保留相应版权或 NOTICE 要求。
- 下面写“许可证待复核”的项目，只能先看设计，不能默认复制源码。

## 3. 第一组：最值得看的股票与 AI 产品

### 3.1 StockAI：让 AI 的成本、证据和弱结论都可见

- 项目：[hyhmrright/StockAI](https://github.com/hyhmrright/StockAI)
- 技术栈：Tauri、React/TypeScript、Rust/Bun；许可证 MIT。
- 迁移等级：M1。
- 它做了什么：股票新闻、行情、基本面、量化评分、多个投资风格的分析角色和带引用的问答。
- 最值得借鉴的不是“13 个 Agent”，而是这些细节：
  - AI 不会切换股票就自动消耗 Token，用户明确点击后才运行；
  - 运行前显示将发生多少次模型调用；
  - 回答带可点击来源；
  - 数据维度缺失时降低结论强度，而不是补一个假数字；
  - 不同币种不做没有意义的强行汇总。
- 对本题的启发：
  - Task 1 的 AI 卡可以显示“依据覆盖度”和“数据时间”，而不是伪装成精确预测概率；
  - Task 2 的业务块可以把观点、来源、风险拆开，不把全部内容塞进 Markdown；
  - Mock AI 也能展示“弱证据 → 弱措辞”的严谨状态机。
- 不建议照搬：13 角色、多数据源、回测、真实 LLM 和 Token 计费都会迅速失控。

### 3.2 alphai-tui：把“新鲜度、相关度、反方观点”变成交互

- 项目：[makeev/alphai-tui](https://github.com/makeev/alphai-tui)
- 技术栈：Rust + ratatui；许可证采用前复核。
- 迁移等级：M1。
- 值得拆出来的交互原子：
  - 新闻卡包含情绪、可能影响、相关度、新颖度、可行动性和反方观点；
  - 15 分钟内的新内容有明显时间提示；
  - 用户可调相关度阈值，立即看到列表变化；
  - 列表与详情复用已经取回的数据，避免重复请求；
  - API 限额和缓存行为是产品体验的一部分，而不是隐藏在后台。
- 对本题的启发：Task 1 可做一个“洞察筛选条”，在风险、事件、趋势三类信号间切换；Task 2 可做带“反方怎么看”折叠区的洞察卡。
- 不建议照搬：终端布局、真实新闻抓取和价格影响预测都不适合直接移植。

### 3.3 OpenTerminalUI：AI 输出不是一段字，而是有阶段的研究过程

- 项目：[Hitheshkaranth/OpenTerminalUI](https://github.com/Hitheshkaranth/OpenTerminalUI)
- 技术栈：React + Python 后端；许可证待复核。
- 迁移等级：M1 / M0（只选极少交互，不能学它的整体规模）。
- 值得看的内容：
  - `bull / bear` 两方卡片和最终结论；
  - 研究阶段 stepper，让用户知道现在在分析什么；
  - 观点、置信条和证据分层展示；
  - 数据源不可用时有降级，而不是整个页面消失；
  - AI 只读，不直接下单。
- 对本题的启发：Task 2 可以用 3 个固定结构化 block 表达“看多证据 / 看空证据 / 当前结论”，不需要真的运行多 Agent。
- 不建议照搬：交易终端、70 多个指标、回测、期权、组合优化都远超题面，也会淹没核心创新。

### 3.4 StockSense：敢于展示模型经常不如朴素基线

- 项目：[HarshMondal/StockSense](https://github.com/HarshMondal/StockSense)
- 技术栈：前后端实时 ML 系统；许可证待复核。
- 迁移等级：M1。
- 独特点：它不是只显示预测曲线，还在真实值到来后给模型打分，并与朴素基线比较；项目明确承认模型经常没有胜过基线。
- 对本题的启发：不要做假的“AI 预测准确率 92%”。可以做一个确定性 Mock 的“判断复盘卡”：
  - 当时观察到什么；
  - 规则给出什么解释；
  - 后续发生了什么；
  - 这条解释是否需要降级。
- 风险：如果把它做成正式预测/回测系统，成本和金融责任都会显著上升。这里只借“诚实反馈”机制。

### 3.5 TradingAgents：多视角有价值，多 Agent 本身没有加分保证

- 项目：[TauricResearch/TradingAgents](https://github.com/TauricResearch/TradingAgents)
- 技术栈：Python、LLM 多 Agent；许可证 Apache-2.0。
- 迁移等级：M1。
- 可借鉴：把基本面、情绪、新闻、技术面分成不同视角，再由一个决策层做综合；流程可恢复、输出结构化。
- 对本题的更轻量改造：不要真的运行多个 Agent，而是让一张卡明确呈现“事实 / 正方解释 / 反方解释 / 暂时结论”。这既有多视角价值，又能保持离线可复现。
- 不建议照搬：多模型编排、长链推理和交易决定不等于更好的 UI 载体，而且很难在短视频里证明正确性。

### 3.6 AI Hedge Fund：把“角色”变成可替换策略，而不是头像表演

- 项目：[virattt/ai-hedge-fund](https://github.com/virattt/ai-hedge-fund)
- 技术栈：Python；许可证 MIT。
- 迁移等级：M1。
- 可借鉴：不同分析角色是可插拔策略；项目明确写明教育用途、不实际交易。
- 对本题的启发：如果采用“不同视角”，数据模型应是 `InsightPerspective` 之类的稳定类型，而不是把几段不同口吻的文本拼起来。
- 不建议照搬：知名投资人角色扮演容易喧宾夺主，也可能让评委觉得只是 Prompt 包装。

### 3.7 OpenBB：统一数据接口的思想有用，整个平台不适合搬

- 项目：[OpenBB-finance/OpenBB](https://github.com/OpenBB-finance/OpenBB)
- 技术栈：Python 数据平台；许可证 AGPLv3。
- 迁移等级：M0 / M1。
- 可借鉴：“连接一次，多处消费”的 provider 思路——列表、详情、AI 卡都从同一份已格式化领域数据读取。
- 对本题的启发：即使首版全是 Mock，也应让 `MarketDataSource`、fixture 和页面消费关系清楚，未来真实 API 只做 Optional adapter。
- 不建议复制：AGPL 源码、庞大 provider 体系、FastAPI 服务与真实数据授权都不是当前任务。

### 3.8 Ghostfolio：从资产管理产品学习“解释信息层级”

- 项目：[ghostfolio/ghostfolio](https://github.com/ghostfolio/ghostfolio)
- 技术栈：Angular/NestJS；AGPL 系项目，采用前复核当前许可证。
- 迁移等级：M1 / M0。
- 可借鉴：总览先给关键数字，再逐层展开持仓、风险和历史；空状态、隐私和本地部署边界也较完整。
- 对本题的启发：详情页不要把所有 AI 文字同时展开。先给一句结论和风险级别，用户点击后再看依据。
- 不建议复制：资产账户、交易、同步和数据库完全超范围。

## 4. 第二组：结构化 AI UI 与聊天产品

### 4.1 Google A2UI：模型只能选择白名单组件，不能随意生成代码

- 项目：[google/A2UI](https://github.com/google/A2UI)
- 迁移等级：M1。
- 核心思想：服务端发送声明式数据，客户端只渲染预先批准的组件目录。
- 对 Task 2 的直接启发：定义少量 typed blocks，例如：
  - `MarkdownBlock`
  - `QuoteCardBlock`
  - `EvidenceBlock`
  - `RiskBlock`
  - `ScenarioBlock`
- 价值：新增一种 AI 业务形态时，不需要改消息列表的总体逻辑；未知 block 可以安全降级。
- 边界：A2UI 本身仍在演进，也没有 Kuikly renderer。本项目只借设计，不引入协议。

### 4.2 Microsoft Adaptive Cards：卡片要有版本、fallback 和宿主边界

- 项目：[microsoft/AdaptiveCards](https://github.com/microsoft/AdaptiveCards)
- 迁移等级：M1。
- 可借鉴：JSON schema、不同宿主渲染、版本协商、fallback text、可访问性 label。
- 对 Task 2 的启发：每个业务 block 都应有稳定类型、必要字段和 fallback；不能识别时至少显示安全纯文本，而不是白屏。
- 边界：不要直接集成整个 Adaptive Cards SDK；其 UI 技术栈与 Kuikly 不一致。

### 4.3 CopilotKit Generative UI：先决定“生成自由度”

- 项目：[CopilotKit/CopilotKit](https://github.com/CopilotKit/CopilotKit)
- 参考：[Generative UI 示例](https://github.com/CopilotKit/generative-ui)
- 迁移等级：M1。
- 可借鉴的决策轴：
  - 低自由度：模型只填充预制卡片；
  - 中自由度：模型输出声明式结构；
  - 高自由度：模型生成开放 UI。
- 本项目建议：选低自由度。评委看到的是稳定、精致、跨端一致的业务卡；不要让 Mock AI 随机改变页面结构。

### 4.4 assistant-ui：聊天体验由可组合部件组成

- 项目：[assistant-ui/assistant-ui](https://github.com/assistant-ui/assistant-ui)
- 技术栈：React/TypeScript；许可证 MIT。
- 迁移等级：M1。
- 可借鉴：消息、输入器、操作条、重试、附件、流式状态、工具调用和审批都是可组合 primitive；工具 JSON 可渲染为结构化组件。
- 对 Task 2 的启发：发送中、失败、完成、重试不是几处临时布尔值，而是消息状态合同；business block renderer 与会话状态分离。
- 不建议照搬：React 组件和完整生产级聊天能力。

### 4.5 Vercel Chatbot：文本、结构化对象和工具结果共处一条消息流

- 项目：[vercel/chatbot](https://github.com/vercel/chatbot)
- 技术栈：Next.js + AI SDK；许可证采用前复核。
- 迁移等级：M1。
- 可借鉴：一条会话可以同时包含文本、结构化对象和工具调用结果；消息持久化与渲染类型分离。
- 对 Task 2 的启发：Mock 回复也应按 `message parts` 组织，而不是把一大段 Markdown 再反解析成行情。

### 4.6 Chainlit：把步骤和中间状态当作可见 UI

- 项目：[Chainlit/chainlit](https://github.com/Chainlit/chainlit)
- 技术栈：Python + Web；许可证 Apache-2.0。
- 迁移等级：M1。
- 可借鉴：工具步骤、加载状态、可停止/重试、starter prompts、聊天 profile。
- 对 Task 2 的启发：可以有轻量的“正在整理行情 → 正在核对风险 → 已生成摘要”三阶段 Mock 状态，但必须短、确定、可测试。
- 风险：不要为了做进度动画而引入真实 Agent orchestration。

## 5. 第三组：图表、KMP 与 Kuikly 技术参考

### 5.1 KuiklyUI 官方仓库与 AI 规则

- 项目：[Tencent-TDS/KuiklyUI](https://github.com/Tencent-TDS/KuiklyUI)
- 辅助规则：[Tencent-TDS/KuiklyUI-AI](https://github.com/Tencent-TDS/KuiklyUI-AI)
- 迁移等级：M3（但只对项目固定 2.4.0 真实存在的 API 成立）。
- 用法：查 `List`、`Input`、`Canvas`、路由、响应式状态和官方 demo 的实际模式。
- 关键边界：上游 `main` 可能远新于 2.4.0。AI rules 只是检索入口，不是版本证据；采用任何 API 前都要回到固定版本源码、编译和 Android/H5 运行验证。

### 5.2 KuiklyMarkdown：Task 2 最接近的参考，但 H5 必须先探针

- 项目：[Kuikly-contrib/KuiklyMarkdown](https://github.com/Kuikly-contrib/KuiklyMarkdown)
- 迁移等级：Android 方向可能 M3，H5 当前只能视为未验证。
- 可借鉴：Markdown block 划分、样式定制、AI 块级增量渲染。
- 已知边界：上游 README 没有声明 H5；制品与当前 Kotlin 版本也需要兼容性核对。
- 正确策略：先做 Android/JS/H5 小探针，同时准备受限 Markdown renderer fallback；不能把“找到仓库”写成“跨端可用”。

### 5.3 KoalaPlot：学习图表 API、标注和交互，不直接搬 Compose

- 项目：[KoalaPlot/koalaplot-core](https://github.com/KoalaPlot/koalaplot-core)
- 技术栈：Compose Multiplatform；0.x API 仍可能变化。
- 迁移等级：M2 / M1。
- 可借鉴：数据点、轴模型、annotation、鼠标 tracking、zoom/pan 如何被拆成配置和可替换部件。
- 对本题的启发：若用户最终选择“最小趋势 + AI 事件标记”，应把坐标换算、数据归一化、标记布局放在纯 Kotlin 层测试，Kuikly Canvas 只负责绘制。
- 不可直接做：把 Compose `@Composable` 组件粘到 Kuikly 传统 DSL。

### 5.4 HDCharts：模块化图表比“万能 Chart”更容易控制范围

- 项目：[dautovicharis/charts（原 HDCharts/charts）](https://github.com/dautovicharis/charts)
- 技术栈：Compose Multiplatform；许可证 MIT。
- 迁移等级：M2 / M1。
- 可借鉴：line、bar、pie 等独立 artifact，共享 core；只引入需要的类型，避免所有功能绑在一起。
- 对本题的启发：即使需要趋势，也先定义 `Sparkline` 或 `InsightTrend` 这种窄组件，禁止先造通用 K 线平台。

### 5.5 TradingView Lightweight Charts：学习金融图表细节和测试，不建议直接嵌入

- 项目：[tradingview/lightweight-charts](https://github.com/tradingview/lightweight-charts)
- 技术栈：TypeScript + HTML5 Canvas；许可证 Apache-2.0，并有产品署名/NOTICE 要求。
- 迁移等级：M1。
- 可借鉴：时间序列、marker、tooltip、price scale、缩放和插件的交互细节，以及图形回归测试思路。
- 对本题的启发：趋势标记应同时有位置、文字、方向和点击后的解释，不能只画一个红绿点。
- 不建议直接嵌入：H5 能运行不代表 Android 可共享；WebView/JS bridge 会破坏当前“一码多端”的演示重点。

### 5.6 Grafima：图表也可以有无障碍合同

- 项目：[Kyriakos-Georgiopoulos/Grafima](https://github.com/Kyriakos-Georgiopoulos/Grafima)
- 技术栈：Compose Multiplatform。
- 迁移等级：M1 / M2。
- 可借鉴：图表测试不只是像素；项目强调 Android/iOS UI 测试和可访问性合同。
- 对本题的启发：趋势图下方保留“总体上涨 2.4%，两个风险事件”文字等价物；涨跌不能只靠颜色表达。

### 5.7 HexaStock：真实 API 只是一个可替换端口

- 项目：[alfredorueda/HexaStock](https://github.com/alfredorueda/HexaStock)
- 技术栈：Kotlin/Android，六边形架构示例。
- 迁移等级：M2。
- 可借鉴：`StockPriceProviderPort` 后面可以接 Finnhub、Alpha Vantage 或 Mock；离线测试不依赖环境变量。
- 对本题的启发：先让 Mock provider 完整通过；真实 API 只能是后置 Optional adapter，不能渗透页面。
- 采用前：再次确认仓库许可证、活跃状态和依赖版本。

## 6. 跨项目提炼出的 10 个“创新原子”

这些原子比“做一个 AI 荐股功能”更有用，因为每个都能成为可见交互、组件合同和测试点。

| 原子 | 用户看到什么 | 工程上可以怎样落地 | 主要来源 |
|---|---|---|---|
| 观点—证据—风险 | 一句话判断、支持事实、反例和风险 | `InsightCard` 的显式字段，而非一段长文本 | StockAI、OpenTerminalUI |
| 时效 / 保质期 | “数据截至 14:30”“已过期” | fixture 时间 + freshness formatter + expired state | alphai-tui |
| 弱证据弱措辞 | 缺数据时显示“线索有限”，不造概率 | coverage rule + deterministic copy | StockAI |
| 正反双视角 | 看多与看空理由并排或切换 | `Perspective` sealed model | TradingAgents、OpenTerminalUI |
| 反方观点折叠 | “这条判断可能错在哪” | disclosure/accordion state | alphai-tui |
| 可点击证据 | 点击事实进入来源/解释详情 | typed evidence id + local resolver | StockAI |
| 有限组件目录 | AI 只能组合批准的业务卡 | `ChatBlock` + renderer registry | A2UI、Adaptive Cards |
| 可见的处理阶段 | 整理行情、核对风险、完成 | deterministic message state machine | Chainlit |
| 诚实复盘 | 结论与后续事实对照 | evaluation fixture + comparison card | StockSense |
| 安全动作 | 只允许查看、比较、展开，不下单 | action allowlist + navigation tests | OpenTerminalUI |

## 7. 由资料组合出的候选创新（不是替你拍板）

### 候选 A：可解释行情洞察卡（推荐作为 Task 1 首选比较对象）

组合：`观点—证据—风险` + `时效` + `弱证据弱措辞`。

用户旅程：

1. 在行情详情看到一句结构化洞察，例如“短线波动放大，暂不形成方向性结论”。
2. 卡片显示 2 条观察事实、1 条反方解释、数据截至时间。
3. 点击“为什么”展开判断链；点击某个证据可定位到对应趋势点或解释行。
4. 缺一类数据时，卡片自动降级为“证据不足”，而不是仍给高置信判断。

为什么可能得分：

- 功能：在既有详情闭环里完整演示；
- 工程：可抽成 `InsightCard` + `InsightModel` + formatter；
- AI 场景：AI 信息载体和交互很明确；
- 风险可控：完全可以由确定性 Mock 驱动。

### 候选 B：行情事件透镜

组合：`最小趋势` + `可点击证据` + `时效`。

用户旅程：趋势区域只有少量事件点；点击某一点，出现“发生了什么 / AI 如何解释 / 还有什么不确定”的小卡。它不是通用 Chart，也不需要缩放、K 线和几十个指标。

优势：画面表现强，和导师提到的“曲线中加入 AI 提示”一致。
风险：Canvas 坐标、命中区、Android/H5 一致性和文本等价物都会增加成本。因此必须先确认是否值得进入详细计划。

### 候选 C：牛 / 基准 / 熊三情景卡

组合：`正反双视角` + `风险` + `安全动作`。

用户不是得到一个“会涨”的答案，而是可以切换：

- 看多情景：哪些条件成立时更乐观；
- 基准情景：当前最中性的解释；
- 看空情景：哪些风险会推翻判断。

优势：比扮演 13 个投资人更简洁，也天然避免把 Mock 写成确定投资建议。
风险：三组内容必须短且有差异，否则只是三段文案。

### 候选 D：AI 判断复盘卡

组合：`诚实复盘` + `弱证据弱措辞`。

使用两段确定性时间 fixture：先显示当时的判断，再切到后续数据，卡片显示“判断被支持 / 部分支持 / 被推翻”，同时说明是哪条证据变化了。

优势：非常少见，能体现 AI 不是永远正确。
风险：必须明确这是演示数据和规则复盘，不能伪装成真实历史预测准确率。

### 候选 E：结构化研究画布（推荐作为 Task 2 基线）

组合：`有限组件目录` + `观点—证据—风险` + `安全动作`。

一条 AI 回复由有序 blocks 组成：

1. Markdown 摘要；
2. 行情卡；
3. 证据列表；
4. 风险卡；
5. “查看详情 / 比较”动作。

优势：同时满足 Markdown 与非文本业务内容，组件化和扩展边界清晰；Task 1 的详情/模型可按实际接口复用。
风险：首版只选一种高质量业务 block，不能一开始把所有类型都实现。

### 候选 F：正反辩论结果块

组合：`正反双视角` + `反方观点折叠` + `有限组件目录`。

Task 2 的一次回答先给结论，用户点击后切换“支持 / 反对 / 尚缺证据”。这可以是完全本地、固定输出，不需要多 Agent。

优势：比普通 QuoteCard 更有 AI 场景辨识度。
风险：如果没有真实交互和结构化字段，就会退化成三段 Markdown。

### 候选 G：可追问的“为什么”芯片

组合：`可点击证据` + `处理阶段` + `安全动作`。

在结构化卡片底部给固定的安全追问：

- “为什么判断为震荡？”
- “最大的反例是什么？”
- “数据什么时候失效？”

点击后追加一条确定性回复，并保持会话返回状态。

优势：成本低，但能把 AI 卡从静态展示变成真正会话。
风险：问题数量要少；不能伪装成任意问题都能回答的真实模型。

### 候选 H：双标的同口径比较卡

组合：`有限组件目录` + `弱证据弱措辞` + `时效`。

用户问“比较 A 和 B”，卡片只用双方都有的数据维度；缺失项明确标记，不拿不同口径数字强行排名。

优势：业务价值直观，可孵化成通用组件。
风险：会引入第二实体的路由和数据合同，Task 1 未稳定前不宜提前实现。

## 8. 三组值得进入下一轮比较的组合方案

评分为本次调研的产品判断，不是正式评分，也不是已经确认的 Task 计划。

| 方案 | Task 1 | Task 2 | 辨识度 | 实现成本 | 工程复用 | 主要风险 | 建议 |
|---|---|---|---:|---:|---:|---|---|
| P1 可信洞察系统 | 可解释行情洞察卡 | 结构化研究画布 + “为什么”追问 | 4.5/5 | 3/5 | 5/5 | 文案和状态需设计得克制 | **首推** |
| P2 多情景系统 | 牛/基准/熊三情景卡 | 正反辩论结果块 | 4/5 | 3/5 | 4/5 | 容易变成三段文案 | 推荐对比 |
| P3 诚实 AI 系统 | 判断复盘卡 | 证据变化说明块 | 5/5 | 4/5 | 3.5/5 | 需避免伪造准确率，演示脚本更复杂 | 高风险高辨识度 |

### 为什么我暂时更推荐 P1

它并不依赖真实 API、真实模型或复杂图表，却能同时产生：

- 一个评委一眼能懂的 AI 交互载体；
- 一套可复用的结构化模型；
- 数据缺失、过期、展开、导航等可测状态；
- Task 1 到 Task 2 自然复用的故事；
- “AI 不应凭空自信”的个人产品立场。

这比“我用了很多 Agent”更像完整的产品创新，也更符合导师强调的“窄而深、组件可孵化、AI 信息载体可演示”。

## 9. 暂时不建议选择的方向

1. **13 个 Agent 或投资大师角色扮演**：展示成本高，容易被看成 Prompt 包装。
2. **直接预测涨跌和精确置信率**：没有真实评估合同就不可信，也有金融表达风险。
3. **完整 TradingView / Bloomberg 克隆**：页面很多，但与 AI 场景设计分无直接关系。
4. **先接实时行情和真实 LLM**：网络、Key、配额、延迟和演示稳定性会吞掉主线时间。
5. **开放式生成 UI**：随机布局不利于跨端一致、测试和短视频演示。
6. **先造通用 Chart DSL**：题面没有要求；只有明确选中最小趋势交互后才值得进入计划。
7. **复制高星项目视觉**：高星不等于适配题目，像素级复刻也无法形成你的产品判断。
8. **把 AGPL 项目源码直接搬入**：可能引入不必要的许可证义务；研究思想即可。

## 10. 供你亲自做决定的头脑风暴表

请给每个候选按 1～5 分打分。不要只看总分，优先淘汰“20 秒讲不清”和“无法稳定录屏”的方案。

| 候选 | 我真正想解决的用户困惑 | 20 秒能看懂 | 有明显交互 | 可抽通用组件 | Android/H5 风险可控 | 我本人真的感兴趣 | 总分 |
|---|---|---:|---:|---:|---:|---:|---:|
| A 可解释行情洞察卡 |  |  |  |  |  |  |  |
| B 行情事件透镜 |  |  |  |  |  |  |  |
| C 三情景卡 |  |  |  |  |  |  |  |
| D AI 判断复盘卡 |  |  |  |  |  |  |  |
| E 结构化研究画布 |  |  |  |  |  |  |  |
| F 正反辩论结果块 |  |  |  |  |  |  |  |
| G “为什么”追问芯片 |  |  |  |  |  |  |  |
| H 双标的比较卡 |  |  |  |  |  |  |  |

还可以用三个强制问题筛选：

1. 删掉 AI 文案后，这个交互本身是否仍然有价值？
2. 换一只股票、换一组 Mock 数据后，这个组件是否仍然成立？
3. 出现缺数据、过期或失败时，它是否仍然诚实且可用？

如果三个答案都是“是”，它更可能是真正的产品/工程创新，而不是一次性 Demo 特效。

## 11. 采用任何项目之前的检查清单

- [ ] 链接仍然存在，查看的是明确提交或 release，而不是仅凭搜索摘要。
- [ ] 重新核对根目录 LICENSE、NOTICE 和第三方依赖。
- [ ] 标注是“源码复用”“模型/算法改写”还是“仅借交互”。
- [ ] 与当前 Kuikly UI 2.4.0、Kotlin 2.0.21、Android/H5 target 做兼容性探针。
- [ ] React/Compose/SwiftUI 代码没有被误当成 Kuikly DSL 可直接粘贴。
- [ ] 新结构没有越过 `docs/REQUIREMENTS.md` 和用户确认的详细计划。
- [ ] Mock、构建成功、浏览器运行、设备运行分别记录，没有混写。
- [ ] 创新有正常、缺失、失败/过期状态，并能在短视频中完整演示。

## 12. 下一步

本文件只完成资料搜集与候选生成，不授权进入业务代码。下一步建议：

1. 你先从 A～H 中选出最有感觉的 2～3 个，不必只选最高分；
2. Codex 为它们分别写一页“用户问题—交互脚本—组件边界—状态—成本—40/25/25/10 映射”的对比稿；
3. 你确认最终创新母题和删减线；
4. 再把被选方案写入 Task 1 详细总计划，确认后才进入 CODE handoff。

本步不需要外部 Agent；也没有修改任何业务代码。
