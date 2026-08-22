# Task 1 / Task 2 任务理解报告（题面校正版）

> 校正日期：2026-08-19（Asia/Shanghai）
> 原始题面：用户于本轮直接提供
> 本地基线：f5014c66181060e95fe8aa10cb738aeed2bdf1ca，main
> 报告性质：需求、源码、架构与实现方案分析；未修改业务代码

## 范围裁决

- [FACT] Shape Task 1 / Task 2 原始题面现已提供，不再存在“题面缺失”。
- [DECISION] 原始题面是唯一的 Task 范围与完成权威。
- [DECISION] Tencent-TDS/KuiklyUI Issue #1477 不是题面，只能作为独立历史或后置图表参考，不能增加 Task Must。
- [FACT] 活动 Task 1 从 finance_home 空壳重新开发；archive/task1-v1 中的旧 Shape 实现、成果和证据不计入当前完成度。
- [DECISION] 当前工程按题面和现有 target 验收 Android/H5；不推导 iOS、HarmonyOS 或 Kuikly 全平台完成要求。
- [DECISION] 通用图表、K 线、缩放、平移和高级手势均后置，只有用户明确补充并建卡后才进入活动范围。

## 1. 我对整个课题的理解

本课题要求基于 Kuikly 跨端框架连续完成两个股票业务原型。Task 1 先解决行情浏览与个股理解：用户在可滚动列表中查看股票名称、代码、价格和涨跌信息，点击进入详情，继续查看最高价、最低价、成交量等字段，并看到一种清晰的 AI 分析或解读。Task 2 再解决自然语言入口：用户能够输入、发送并查看会话；模型回复除 Markdown 外，还要包含至少一种股票、指数或行情业务内容，并能从聊天结果进入一个包含基础行情、走势区域、摘要或 AI 解读的详情页。两题的核心不是通用图表组件，也不要求 K 线或高级手势。当前实现采用离线确定性 Mock、Android/H5 验收与 code/tests/evidence/learning 四门；Task 1 完成后再启动 Task 2，是项目实施顺序而非题面附加要求。

## 2. Task 1

### 2.1 目标

基于 Kuikly 实现一个可操作的 AI 股票行情原型，形成“行情列表 → 个股详情 → AI 分析”的完整路径。

### 2.2 原始需求、输入、输出与交付

| 项目 | 题面内容 |
|---|---|
| 首页输入 | 股票行情数据 |
| 首页输出 | 名称、代码、最新价、涨跌额、涨跌幅组成的可滚动列表 |
| 交互 | 点击某只股票进入其详情 |
| 详情输出 | 名称、代码、最新价、涨跌幅、最高价、最低价、成交量等 |
| AI 输入 | 当前个股及其行情上下文 |
| AI 输出 | 点位、提示、趋势、风险、信号或行情总结中的一种或多种 |
| 展示形态 | 卡片、标签、提示区、文本分析区等，题面不限定 |
| 工程交付 | Kuikly 页面、Mock 数据/AI、Android/H5 构建与交互证据、测试和说明文档 |

### 2.3 Must、发散性与非目标

#### 题面 Must

1. 可滚动行情列表。
2. 每行展示名称、代码、最新价、涨跌额、涨跌幅。
3. 点击股票进入正确个股详情。
4. 详情展示名称、代码、最新价、涨跌幅、最高价、最低价、成交量。
5. 详情中存在用户可见的 AI 分析或解读。
6. 基于 Kuikly 形成跨端业务 Demo。

#### 发散性

- AI 内容可选择风险提醒、趋势判断、行情总结等安全且易解释的组合。
- 展示可以是分析卡、风险标签和摘要文本，不要求覆盖题面列出的全部示例。
- 买入/卖出点位是可选方向，不建议默认输出强交易指令；若使用必须强化 Mock 和非投资建议边界。

#### 题面未要求

- 通用 line/bar Chart API、坐标轴、网格、Tooltip 或图表 DSL。
- K 线、成交量图、缩放、平移和高级手势。
- 真实行情、真实模型、登录、交易、支付或联网检索。
- iOS、HarmonyOS 或所有 Kuikly 平台的本仓库 target/运行证据。

> 一句话：Task 1 本质上是让用户能跨端浏览股票、查看个股详情，并获得可理解的 AI 行情解读。

### 2.4 相关模块与当前实现

| 路径 | 当前作用 | 当前状态 |
|---|---|---|
| **shared/src/commonMain/kotlin/io/github/study0915/kuiklyfinance/ui/Task1ShellPage.kt** | 注册 finance_home，展示 reset 和免责声明 | 唯一活动业务页 |
| **shared/src/commonTest/kotlin/io/github/study0915/kuiklyfinance/ui/Task1ShellContractTest.kt** | 校验入口和免责声明 | 不覆盖题面业务 |
| **shared/build.gradle.kts** | Android/JS shared KMP module | 已依赖 Kuikly core |
| **androidApp/src/main/kotlin/io/github/study0915/kuiklyfinance/android/MainActivity.kt** | Android host 与生命周期 | 固定 attach finance_home |
| **h5App/src/jsMain/kotlin/Main.kt** | H5 host 与生命周期 | 固定 attach finance_home |
| **KuiklyChart/** | 保留的空 module | 不是 Task 1 前置 |
| **archive/task1-v1/** | 旧行情、详情、Mock AI、图表和证据 | 只读参考，不参与当前构建 |

当前真实调用链：

~~~text
Android MainActivity / H5 Main.kt
  → Kuikly delegator
  → finance_home
  → Task1ShellPage
  → reset 文本与免责声明
~~~

当前缺失：

- [FACT] 没有活动行情模型、fixture Provider 或 AI Analysis Provider。
- [FACT] 没有行情列表、滚动列表项、详情页或详情 route。
- [FACT] 没有题面字段的 formatter、页面状态和业务测试。
- [FACT] 没有 AI 分析实现。
- [VERIFIED 2026-08-19] T1-000 reset scaffold 已完成 H5 真实浏览器与完整自动门禁；Android 设备运行仍未验证。

### 2.5 目标调用链与架构插入点

~~~text
Android / H5 host
  → @Page finance_home
  → Home state
  → MarketProvider.load()
  → Ready(MarketSnapshot)
  → observableList snapshot.quotes
  → Kuikly List + row
  → typed Detail destination
  → @Page stock_detail
  → MarketSnapshot.findByCode(stock_code)
  → AnalysisProvider.analyze(quote) [T1-EXPERIENCE]
  → Detail fields + AI analysis card/text
~~~

插入位置：

- 行情 domain、Provider、AI 分析和状态放在 shared/commonMain。
- 页面使用现有 Pager、List、Text、View、observable/observableList 和 RouterModule 模式。
- Android/H5 host 只在共享 Router 无法完成详情跳转时做最小 adapter。
- 不修改 Kuikly layout engine，不新增 Modifier 体系，不创建原生 chart view，也不要求 KuiklyChart 先实现。

### 2.6 最接近的参考实现

1. **当前 Task1ShellPage、Task1Routes 与 Android/H5 delegator**

   可复用 @Page、Pager、共享入口常量、attr DSL、生命周期和免责声明模式。

2. **archive/task1-v1 的 MarketModels、MockProviders 与 FinancePages**

   可借鉴题面字段、确定性 fixture、页面布局和历史 H5 route adapter。不能复制旧公开签名、页面直接构造具体 Provider、unknown code 静默回退或历史完成状态。

3. **Kuikly 官方 Page、List 与 Router 模式**

   业务实现应继续使用 commonMain Pager、响应式列表与 RouterModule，不创建平台专属页面。环境与框架入口参考：[Kuikly 快速开始](https://kuikly.tds.qq.com/QuickStart/env-setup.html)（访问：2026-08-19）。

### 2.7 实现方案

#### 方案 A：MarketProvider + AnalysisProvider + thin Pages（推荐）

~~~kotlin
sealed interface MarketLoadResult {
    data class Ready(val snapshot: MarketSnapshot) : MarketLoadResult
    data object Unavailable : MarketLoadResult
}

fun interface MarketProvider {
    fun load(): MarketLoadResult
}

interface AnalysisProvider {
    fun analyze(quote: StockQuote): AnalysisResult
}
~~~

页面只负责查询、状态映射、Kuikly DSL 和导航；Provider 隐藏 fixture、索引、字段一致性和未知实体；AnalysisProvider 隐藏确定性 Mock 分析模板。

Caller 示例：

~~~kotlin
when (val result = marketProvider.load()) {
    is MarketLoadResult.Ready -> quotes.diffUpdate(result.snapshot.quotes)
    MarketLoadResult.Unavailable -> state = HomeState.Failed
}
~~~

依赖分类：

| 依赖 | 类别 | 处理 |
|---|---|---|
| 字段规范化、格式化、状态转换 | in-process | 留在 shared implementation，直接单测 |
| FixtureMarketProvider / test fake | local-substitutable adapters | 前者返回确定性 Ready；后者通过同一 state/resolver 覆盖 Unavailable，完整 retry 留给 T1-EXPERIENCE |
| MockAnalysisProvider / ScriptedAnalysisProvider | local-substitutable adapters | 前者确定性输出，后者覆盖失败和边界 |
| RouterModule | framework-owned seam | common route codec + 必要的 Android/H5 host adapter |
| 真实行情/模型 | true external，当前非目标 | 不提前实现 |

Depth：两个小 interface 隐藏 fixture、聚合、错误和 AI 模板。Leverage：首页、详情和未来 Task 2 共用行情事实；AI 展示可替换而不改页面结构。Locality：数据源只改 Provider adapter，AI 策略只改 AnalysisProvider，路由只改 codec/host。

#### 方案 B：页面内置 Mock 列表和分析文本

- 最快得到截图，改动最小。
- 页面将承担查找、格式化、错误、AI 文案和导航，测试必须绕过 UI。
- Task 2 复用时会复制行情与详情逻辑。
- 只适合抛弃式 prototype，不适合作为正式交付。

#### 方案 C：统一 FinanceRuntime / Intent / Effect

- 用 session dispatch 统一首页、详情、刷新、AI 和导航。
- 适合未来真实服务、取消、缓存与更多页面。
- 当前只有两页和离线 Mock，revision/effect 协议成本过高。
- 保留为未来演化方向，不在首版实现。

| 指标 | 方案 A | 方案 B | 方案 C |
|---|---|---|---|
| 改动量 | 中 | 小 | 大 |
| 架构一致性 | 高 | 中低 | 高但超范围 |
| 风险 | 中低 | 中：返工高 | 高 |
| 跨平台 | 高：commonMain 为主 | 中 | 高 |
| 可维护性 | 高 | 低 | 高 |
| 实现难度 | 中 | 低 | 高 |

[DECISION] 采用方案 A。它覆盖题面的真实变化点，又没有把通用图表或会话协议提前塞进 Task 1。

### 2.8 技术难点与风险

| 维度 | 风险 | 控制 |
|---|---|---|
| API | 首页与详情字段不一致或 unknown code 返回别的股票 | 统一实体 id；typed result；禁止 fallback |
| 数值 | 价格、涨跌额/幅、最高/最低格式和正负语义 | common formatter；有限值与边界测试 |
| AI | Mock 分析被误认为真实建议 | 显式 Mock、免责声明、优先风险/总结而非强交易指令 |
| KMP | commonMain 引入 JVM-only API | 业务逻辑留 common；不引入不必要 expect/actual |
| Rendering | 长列表、List 尺寸、颜色成为唯一涨跌语义 | observableList + 稳定 key；明确尺寸；符号/文本并用 |
| 路由 | Android/H5 参数编码不同 | typed codec；两端实际点击验证 |
| 性能 | 在 attr 中反复解析 fixture、格式化全列表 | fixture 一次加载；增量列表；缩小 observable 范围 |
| 真实性 | build 被写成 browser/device 通过 | 构建、浏览器、设备分别留证 |

### 2.9 验收方式

1. 首页所有题面字段可见，列表真实可滚动。
2. 点击至少两只不同股票分别进入正确详情。
3. 详情所有题面字段可见且与列表实体一致。
4. AI 分析区域可见、确定性、带 Mock 与免责声明。
5. 空列表、Provider 失败、unknown code 和 retry 可恢复。
6. shared tests、Kotlin/JS、H5 production bundle、Android JVM 和 APK 门禁通过。
7. H5 浏览器实际完成滚动/点击/返回；Android 设备单独记录。
8. code/tests/evidence/learning 四门齐全后 Task 1 才 VERIFIED。

## 3. Task 2

### 3.1 目标

基于 Kuikly 实现一个 AI 股票问答 Demo，形成“输入 → 发送 → 会话 → Markdown + 非 Markdown 业务内容 → 详情承接”的完整路径。

### 3.2 原始需求、输入、输出与交付

| 项目 | 题面内容 |
|---|---|
| 用户输入 | 自然语言问题 |
| 聊天输出 | 会话记录和模型回复 |
| 文本能力 | Markdown |
| 业务内容 | 股票、指数或行情相关的结构化卡片、图表或其他形态，至少一种 |
| 跳转 | 至少一个聊天结果进入股票或指数详情 |
| 详情输出 | 基础行情、走势区域、摘要信息或 AI 解读 |
| 工程交付 | Kuikly chat/detail 页面、Mock Chat、选定业务 block、Android/H5 证据和测试 |

### 3.3 Must、发散性与非目标

#### 题面 Must

1. 聊天主页面支持输入、发送、会话记录。
2. 支持 Markdown。
3. Markdown 之外至少一种股票/指数/行情业务内容。
4. 至少一个聊天结果可跳转至详情。
5. 详情包含基础行情与走势区域，并提供摘要或 AI 解读中的至少一种。
6. 基于 Kuikly 实现业务 Demo。

#### 发散性

- 非 Markdown 内容可以选择 MarketSummaryCard、IndexCard、TrendChart 或其他合理形态。
- 首版不要求股票和指数同时覆盖，也不要求同时实现卡片与图表。
- 推荐首个结构化形态为可点击 MarketSummaryCard：实现简单，能够直接证明业务内容与详情承接。

#### 题面未要求

- 通用 Chart module、指定 line/bar/K 线类型或高级手势。
- 失败重试、busy、流式 token、工具调用、持久化和云同步。
- 真实 LLM、实时行情、联网检索、语音或登录。

> 一句话：Task 2 本质上是让聊天结果不止是文本，还能承载股票业务内容并自然进入详情页面。

### 3.4 相关模块与当前实现

| 路径 | 作用 | 当前状态 |
|---|---|---|
| **docs/task2-readiness.md** | 当前实施合同 | 已按原题面校正 |
| **shared/** | chat model/provider/session/page 的目标 module | 没有活动聊天源码 |
| **h5App/src/jsMain/kotlin/Main.kt** | H5 页面入口与路由 | 固定 finance_home |
| **shared/build.gradle.kts** | 可选 Markdown 依赖探针 | 当前无 Markdown 依赖 |
| **KuiklyChart/** | 可选后置图表 module | 不是 Task 2 前置 |

[FACT] finance_chat、ChatMessage、ChatProvider、Input、Markdown renderer、业务 block 和 Task 2 tests 均未实现；全部 T2 切片为 BACKLOG。

### 3.5 目标调用链与架构插入点

~~~text
@Page finance_chat
  → Input + send
  → ChatSession / state reducer
  → MockChatProvider.answer
  → ordered content blocks
      ├─ Markdown
      └─ MarketSummaryCard（首版推荐）
  → typed market detail destination
  → 复用或扩展 Task 1 detail
  → 基础行情 + 最小走势区域 + 摘要/AI
~~~

- Chat model、Provider、session 和 block renderer 放在 shared/commonMain。
- 首版走势区域可以是聚焦业务的 MarketTrendView，不先建设通用 Chart DSL。
- 当详情与聊天内部出现第二个真实图表 caller，且用户决定扩展时，再评估提炼 KuiklyChart。

### 3.6 最接近的参考实现

1. **Task 1 的行情 model 与详情 route**

   Task 2 可以复用已验证的实体、行情字段和详情页面，避免重复业务事实；不复用 archive API。

2. **Kuikly 官方 ChatDemo**

   可借鉴 Kuikly 消息列表、Input 和生命周期使用方式；业务模型仍使用本项目的显式 block。

3. **题面给出的 KuiklyMarkdown**

   截至 2026-08-19，其 README 声明 Android、iOS、HarmonyOS，未声明 H5；标准依赖示例与本项目 Kotlin 版本也不完全对齐，因此它是参考而非强制依赖，必须先做 Android/JS/H5 探针。[KuiklyMarkdown](https://github.com/Kuikly-contrib/KuiklyMarkdown)（访问：2026-08-19）。

### 3.7 实现方案

#### 方案 A：显式内容块 + ChatSession + 一个首版业务 block（推荐）

~~~kotlin
sealed interface ChatBlock {
    data class Markdown(val text: String) : ChatBlock
    data class MarketSummaryCard(
        val entity: MarketEntityRef,
        val quote: MarketQuote,
        val summary: String,
    ) : ChatBlock
    data class Notice(val text: String) : ChatBlock
}

interface ChatProvider {
    suspend fun answer(request: ChatRequest): ChatResponse
}
~~~

ChatSession 隐藏 message id、history、sending、success/failure 和 retry；页面只处理 Input、observableList、block renderer 和 route。

核心顺序：

~~~text
valid submit
  → append USER
  → append ASSISTANT/SENDING
  → provider
  → replace same assistant id with COMPLETE or FAILED
~~~

Depth：小型 ChatSession 隐藏多轮状态，ChatProvider 隐藏问题分类和业务内容组合。Leverage：手输问题、推荐问题和 retry 共用状态机，MarketSummaryCard 同时证明非 Markdown 内容与详情跳转。Locality：新增 block 只改 provider、renderer 和该 block 测试。

#### 方案 B：字符串消息 + Markdown 内嵌股票语法

- 初始实现最短。
- 必须从文本解析实体和卡片，安全、路由和测试脆弱。
- 业务内容与 Markdown renderer 耦合。
- 不推荐。

#### 方案 C：通用流式 Conversation Engine

- 支持 token stream、tool calls、持久 session 和 renderer registry。
- 对真实 LLM 有扩展性。
- 当前离线 Mock 和题面不需要，取消/背压/协议成本过高。
- 后置独立 ADR。

| 指标 | 方案 A | 方案 B | 方案 C |
|---|---|---|---|
| 改动量 | 中 | 小 | 大 |
| 架构一致性 | 高 | 低 | 高但超范围 |
| 风险 | 中低 | 高：字符串协议 | 高：并发协议 |
| 跨平台 | 高 | 中 | 高 |
| 可维护性 | 高 | 低 | 高 |
| 实现难度 | 中 | 低 | 高 |

[DECISION] 采用方案 A，但首版只冻结一个非 Markdown business block；TrendChart、IndexCard 和更多 block 均为后置扩展。

### 3.8 技术难点与风险

| 维度 | 风险 | 控制 |
|---|---|---|
| API | 把题面“至少一种”扩成全部 block | 首版任务卡只选 MarketSummaryCard |
| 状态 | 重复发送、晚到结果、retry 复制消息 | stable id、single in-flight、原位替换 |
| Reactive | observableList item 更新不可见 | append/set 明确更新；稳定 key |
| Markdown | H5 artifact/版本未知，HTML/script/remote image 风险 | 依赖探针；安全 renderer；未知标记纯文本 |
| 路由 | 详情实体错误，返回后会话丢失 | typed entity；Android/H5 跳转和返回测试 |
| 走势区域 | 被误扩成通用图表平台 | 先做最小 MarketTrendView；通用图表后置 |
| KMP | coroutine 生命周期、JS 平台差异 | Kuikly lifecycleScope；取消后不更新 UI |
| 性能 | 长会话全量替换、Markdown 重解析 | lazy list、稳定 id、缓存已完成 block |

### 3.9 验收方式

1. 输入、发送和会话记录实际可用。
2. 至少一种 Markdown 内容正确渲染。
3. 至少一个 MarketSummaryCard 等非 Markdown business block 可见。
4. 该结果打开正确实体详情。
5. 详情展示基础行情、走势区域，以及摘要或 AI 解读至少一种。
6. 空白输入、重复发送、未知实体和 Mock 免责声明有证据；失败/retry 作为项目质量门验证。
7. Android/Kotlin JS/H5 bundle、H5 浏览器和 Android 设备分别记录。
8. code/tests/evidence/learning 四门齐全后 Task 2 才 VERIFIED。

## 4. Task 1 与 Task 2 的关系

两题在题面上是两个可独立理解的 Demo；在本仓库的实施策略中按 Task 1 → Task 2 顺序开发，并复用契约匹配的业务能力：

~~~text
Task 1
StockQuote + MarketSnapshot
  → finance_home
  → stock_detail
  → AI analysis

Task 2
finance_chat
  → ChatProvider
  → Markdown + MarketSummaryCard
  → Task 1 stock_detail
  → Task 2 增补最小走势区域
~~~

| 关系 | 判断 |
|---|---|
| 顺序 | 项目决定 Task 1 VERIFIED 后启动 Task 2 |
| Task 1 提供 | 行情实体、列表、详情、Mock 行情、AI 分析 |
| Task 2 提供 | 聊天状态、Markdown、至少一种业务 block、详情承接 |
| 直接复用 | 契约匹配的 StockQuote、MarketSnapshot、详情 route/page |
| 非前置 | KuiklyChart、line/bar、K 线、高级手势 |
| 禁止 | Task 2 引用 archive，或为了可选图表反向阻塞 Task 1 |

## 5. 推荐开发顺序

### Step 0：关闭 reset 基线

- [DONE 2026-08-19] 完成 T1-000 H5 可视检查并裁决 VERIFIED。
- 只验证 reset 页面；不把它写成 Task 1 业务完成。

### Step 1：T1-VERTICAL

- 建任务卡，冻结 StockQuote、MarketLoadResult、MarketSnapshot、MarketProvider 和 typed detail route。
- 先写 Provider、字段格式化和 route tests。
- 实现可滚动行情列表、点击和题面详情字段。
- 完成 Android/H5 build 与浏览器路径证据。

### Step 2：T1-EXPERIENCE

- 冻结 AnalysisProvider 和 AI 展示形态。
- 推荐实现风险提示 + 趋势判断 + 行情总结，不默认给强交易建议。
- 实现 loading/empty/error/retry、可访问性和跨端体验。
- 四门齐全后 Task 1 VERIFIED。

### Step 3：T2-000

- 建 finance_chat 页面空壳、Input、发送入口和消息列表。

### Step 4：T2-VERTICAL

- 冻结 ChatMessage、ChatProvider、ChatSession。
- 首版选择 Markdown + MarketSummaryCard。
- 完成一次聊天结果 → 正确详情的端到端路径。

### Step 5：T2-EXPERIENCE

- 在详情增补最小走势区域和摘要/AI。
- 完成 Markdown probe/fallback、长会话、失败/retry、键盘和跨端体验。
- 四门齐全后 Task 2 VERIFIED。

### Step 6：后置 Optional

- 用户需要时再分别建卡：通用 Chart、line/bar、K 线、成交量图、缩放、平移、高级手势、更多结构化 block。
- Optional 不阻塞 Task 1/2 核心完成声明。

## 6. 预计需要修改的文件

### 6.1 Task 1 必须新增或修改

| 路径 | 作用 |
|---|---|
| **shared/src/commonMain/kotlin/io/github/study0915/kuiklyfinance/market/MarketModels.kt** | StockQuote、typed load result 与不可变 snapshot |
| **shared/src/commonMain/kotlin/io/github/study0915/kuiklyfinance/market/MarketProvider.kt** | 单一 load/provider seam |
| **shared/src/commonMain/kotlin/io/github/study0915/kuiklyfinance/market/FixtureMarketProvider.kt** | 确定性 Mock 数据 |
| **shared/src/commonMain/kotlin/io/github/study0915/kuiklyfinance/analysis/AnalysisProvider.kt** | AI 分析 seam |
| **shared/src/commonMain/kotlin/io/github/study0915/kuiklyfinance/analysis/MockAnalysisProvider.kt** | 确定性分析 |
| **shared/src/commonMain/kotlin/io/github/study0915/kuiklyfinance/navigation/FinanceRoutes.kt** | home/detail typed route |
| **shared/src/commonMain/kotlin/io/github/study0915/kuiklyfinance/ui/FinanceHomePage.kt** | 行情列表 |
| **shared/src/commonMain/kotlin/io/github/study0915/kuiklyfinance/ui/StockDetailPage.kt** | 题面详情；AI 区域在 T1-EXPERIENCE 增补 |
| **shared/src/commonMain/kotlin/io/github/study0915/kuiklyfinance/ui/Task1ShellPage.kt** | 替换 reset 入口，避免重复 Page |
| **shared/src/commonTest/kotlin/io/github/study0915/kuiklyfinance/market/** | models/provider/formatter tests |
| **shared/src/commonTest/kotlin/io/github/study0915/kuiklyfinance/analysis/** | analysis tests |
| **shared/src/commonTest/kotlin/io/github/study0915/kuiklyfinance/navigation/** | route tests |
| **h5App/src/jsMain/kotlin/Main.kt** | 仅在 H5 详情导航需要 host adapter 时修改 |
| **docs/handoffs/T1-*-TASK.md、docs/evidence、docs/learning** | 四门任务文档 |

### 6.2 Task 2 必须新增或修改

| 路径 | 作用 |
|---|---|
| **shared/src/commonMain/kotlin/io/github/study0915/kuiklyfinance/chat/ChatModels.kt** | role/status/message/blocks |
| **shared/src/commonMain/kotlin/io/github/study0915/kuiklyfinance/chat/ChatProvider.kt** | chat seam |
| **shared/src/commonMain/kotlin/io/github/study0915/kuiklyfinance/chat/MockChatProvider.kt** | 确定性回复 |
| **shared/src/commonMain/kotlin/io/github/study0915/kuiklyfinance/chat/ChatSession.kt** | submit/state/history/retry |
| **shared/src/commonMain/kotlin/io/github/study0915/kuiklyfinance/ui/FinanceChatPage.kt** | 聊天主页 |
| **shared/src/commonMain/kotlin/io/github/study0915/kuiklyfinance/ui/ChatBlockViews.kt** | Markdown + 首版 business block |
| **shared/src/commonMain/kotlin/io/github/study0915/kuiklyfinance/ui/MarketTrendView.kt** | 详情最小走势区域 |
| **shared/src/commonTest/kotlin/io/github/study0915/kuiklyfinance/chat/** | session/provider/block tests |
| **shared/src/commonTest/kotlin/io/github/study0915/kuiklyfinance/navigation/** | chat → detail tests |
| **docs/handoffs/T2-*-TASK.md、docs/evidence、docs/learning** | 四门任务文档 |

### 6.3 可能修改

| 路径 | 条件 |
|---|---|
| **shared/build.gradle.kts** | Markdown 依赖探针后确需新增依赖 |
| **androidApp/src/main/kotlin/io/github/study0915/kuiklyfinance/android/MainActivity.kt** | Android route 实测需要 host adapter |
| **h5App/src/jsMain/kotlin/Main.kt** | H5 route/session 返回需要 adapter |
| **.github/workflows/ci.yml** | 补 shared Android JVM test，与本地 verify 对齐 |

### 6.4 后置 Optional

| 路径 | 条件 |
|---|---|
| **KuiklyChart/src/commonMain/** | 用户明确启动通用图表任务 |
| **KuiklyChart/src/commonTest/** | 与图表任务同步新增 |
| **docs/api.md** | 通用图表 API 真正进入范围后 |

### 6.5 不建议修改

- archive/task1-v1。
- settings.gradle.kts 和根依赖版本，除非独立任务卡/ADR。
- Kuikly core/layout engine 或平台原生 chart view。
- 为 Task 1 预造聊天 engine，或为 Task 2 预造全套图表平台。
- 真实服务、密钥、账号或未授权外部发布。

## 7. 需要新增的测试

### 7.1 当前真实性

- [FACT] 当前 shared test 只覆盖 shell route/免责声明。
- [FACT] KuiklyChart 没有活动测试，jsNodeTest 为 NO-SOURCE/SKIPPED；这不阻塞无图表的 Task 1 核心。
- [FACT] 既有文档记录 shared JS、H5 bundle、Android JVM 和 APK 构建通过；本轮未重跑构建。
- [VERIFIED] H5 空壳已完成真实 Edge/Chromium 可视验收；[UNVERIFIED] Android 设备运行仍未完成。

### 7.2 核心测试矩阵

| 类型 | Task 1 | Task 2 |
|---|---|---|
| Unit | 所有题面字段、格式化、fixture 确定性、empty/failure、unknown code、AI 分析确定性 | input/send/history、selected business block、Mock 确定性、unknown entity、Markdown 安全、failure/retry |
| Integration | Provider → Home state → row；row → correct detail；detail → AnalysisProvider | query → blocks；MarketSummaryCard → correct detail；detail → trend/summary |
| UI | 长列表滚动、涨跌文本/符号、点击、详情字段、AI 区域、empty/error/retry | Input/keyboard、消息滚动、Markdown、business card、详情跳转/返回 |
| Demo | list → detail → AI analysis | chat → Markdown + card → detail → trend/summary |
| Platform | shared JS、H5 bundle/browser、Android JVM/APK/device 分级记录 | 同左，并增加 Markdown dependency probe |

### 7.3 Optional 图表测试

只有后置图表任务启动时才新增 empty/single/equal/non-finite、range/layout/hit-test、line/bar/K 线和手势测试；这些不进入 Task 1/2 核心门禁。

### 7.4 建议命令

~~~powershell
. .\scripts\use-cli-env.ps1
.\gradlew.bat :shared:compileKotlinJs
.\gradlew.bat :h5App:jsBrowserProductionWebpack
.\gradlew.bat :shared:testDebugUnitTest
.\gradlew.bat :androidApp:assembleDebug
.\scripts\run-h5.ps1
~~~

**scripts/verify.ps1** 仍可作为整体验证入口；其中空 KuiklyChart test 的 SKIPPED 必须如实记录，不解释为图表能力。

## 8. Git 历史与证据边界

| Commit | 事实 | 当前意义 |
|---|---|---|
| af48db7 | 一次性加入旧行情、详情、Mock AI 和图表 | 历史参考，不定义新题面 |
| c7ab72e | 扩充旧实现和验证 | 历史参考，不产生兼容义务 |
| ab8675d | 引入学习工作区与四门 | 四门继续有效 |
| 0e10f87 | 旧 Task 1 归档，活动工程 reset | 当前业务完成度归零 |
| d1cf428 | 删除无行为 chart scaffold，集中 route，恢复旧 Task 2 设计合同 | Git 历史不能覆盖本轮原题面 |
| f5014c6 | upstream main/reset 源码基线 | 后续只改文档与验收证据；活动源码未变化 |
| ddf83e6 | 按原始题面校正文档 | 解除 Issue/图表绑定，不改变 reset 源码 |

关键结论：

- archive 只证明以前做过什么，不证明现在完成了什么。
- 旧 readiness、旧 ADR 或 Git commit 不能扩张用户提供的原始题面。
- Issue #1477 可在未来图表任务中提供技术参考，但不决定 Shape Task 的 Must、平台或完成声明。

## 9. 参考资料的正确层级

1. 原始 Task 1/2 题面：范围与验收权威。
2. 当前活动源码、任务卡、测试和证据：实现事实。
3. [Kuikly 快速开始](https://kuikly.tds.qq.com/QuickStart/env-setup.html)：框架环境参考。（访问：2026-08-19）
4. [KuiklyMarkdown](https://github.com/Kuikly-contrib/KuiklyMarkdown)：Task 2 组件参考，不是强制依赖。（访问：2026-08-19）
5. [Issue #1477](https://github.com/Tencent-TDS/KuiklyUI/issues/1477)：独立历史/可选图表参考，不是题面。（访问：2026-08-19）
6. archive/task1-v1：只读历史实现参考。

## 10. 已解决的冲突

### 裁决一：原始题面

【需求原文】

用户已提供 Task 1/2 完整题面。

【代码现状】

旧 docs 曾把 Issue #1477 和衍生 readiness 当作范围来源。

【存在的冲突】

旧报告把 line/bar、图表 DSL 和平台矩阵推导为 Task Must。

【我的判断】

[RESOLVED] 原始题面优先；Issue #1477 不是题面。REQUIREMENTS、readiness、ADR、workboard 和本报告已同步校正。

### 裁决二：Task 1 当前状态

【需求原文】

Task 1 状态重置为重新开发，原来开发的 Shape with AI 内容在归档文件夹。

【代码现状】

活动源码只有 finance_home reset；旧实现位于 archive/task1-v1。

【存在的冲突】

旧构建、获奖和功能证据可能被误计入活动完成度。

【我的判断】

[RESOLVED] 活动 Task 1 业务完成度为 0；archive 不参与构建、回归或验收。

### 裁决三：平台

【需求原文】

基于 Kuikly 跨端框架实现，以 Task 题面为准。

【代码现状】

当前工程只配置 Android 与 JS/H5 target。

【存在的冲突】

旧报告曾用 Issue #1477 的“多平台”追问 iOS/HarmonyOS。

【我的判断】

[RESOLVED] 当前按 Android/H5 实际构建和交互验收；不声明未配置平台，也不让它们阻塞 Task。

### 裁决四：K 线与高级手势

【需求原文】

Task 题面未要求 K 线或高级手势，用户决定后面再补充。

【代码现状】

archive 有历史实现，活动 KuiklyChart 为空。

【存在的冲突】

旧报告把图表能力放入 Task 1 核心切片。

【我的判断】

[RESOLVED] 通用图表、K 线和高级手势全部后置 Optional；没有独立任务卡时不实施、不验收、不宣称。

## 11. 最终技术判断

1. Task 1 采用 MarketProvider + AnalysisProvider + thin Pages；核心是列表、详情和 AI 分析，不是 Chart。
2. Task 2 采用显式内容块 + ChatSession；首版只选择一个 MarketSummaryCard 类业务 block，满足“至少一种”而不预造全部类型。
3. Task 2 可以复用 Task 1 的行情模型与详情 route；通用 Chart 不是依赖。
4. Task 2 的走势区域先做最小 MarketTrendView；只有出现真实复用和用户新增范围后，才提炼 KuiklyChart。
5. T1-000 H5 可视门已关闭；后续按 T1-VERTICAL → T1-EXPERIENCE → T2-000 → T2-VERTICAL → T2-EXPERIENCE 推进。

## 本轮验证边界

- 已读取 workspace/repo 规则、REQUIREMENTS、readiness、ARCHITECTURE、DECISIONS、workboard、活动源码、测试、构建和 Git 历史。
- 已按用户提供的题面重写范围，不再用 Issue #1477 推导 Task Must。
- 本轮只修改需求、Agent 指令、计划、架构、ADR、任务板、学习/交接注记和本报告；没有修改业务代码。
- 纯文档变更不运行 Gradle；最终以 Markdown、语义 grep、git diff --check 和工作树范围审计交付。

下一步：T1-VERTICAL 任务卡已基于 `90e0066` 建立并进入 READY；由 Codex 按卡实现行情模型、单一 Provider/快照 seam、滚动列表和详情 route，不加入 AI、图表、K 线或高级手势。本步不需要外部 Agent。
