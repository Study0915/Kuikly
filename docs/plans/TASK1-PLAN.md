# Task 1 正式实施计划 · 行情证据联动卡

版本：v1.0，2026-09-07。Owner：Windows Codex。阶段状态以 [workboard](../workboard.md) 为唯一入口。

**目标：完成“行情列表 → 个股详情 → 用 K 线、成交量与证据双向核对 Mock AI 解读”的可运行原型。** 本计划把联合设计 v3 落实到 Task 1；Task 2 的正式 PLAN 等待 T1-LEARNING VERIFIED。

用户指定以 v3 开始 Task 1 实施计划，并要求 Git commit 管理、依赖安装在工作区、避免污染 base 环境。2026-09-07 用户在审阅本计划提交 `0060711` 后明确“开始实施”，确认本 v1.0 并授权 Codex 进入 CODE；沿用已确认范围，不重新要求选择创新候选。

## 1. 输入、基线与已验证边界

| 项目 | 固定内容 |
|---|---|
| 需求权威 | [REQUIREMENTS](../REQUIREMENTS.md)；评分依据 [evaluation-pipeline](../evaluation-pipeline.md) |
| 设计输入 | 《行情证据联动卡：Task 1 / Task 2 共用设计方案 v3》，2026-09-07；用户本轮引用版本 |
| 设计输入 SHA-256 | `FE55B0044716D01A9C73142BF8927A21A38FA58CF131F7A7E2A423941CA2D72B` |
| 随计划保存的算例 | [task1-evidence-samples-v3.json](references/task1-evidence-samples-v3.json)，内容来自设计用《方案示例数据-v2.json》；仍是设计 fixture |
| 原始算例 SHA-256 | `15A1C2ECEDE57AC6727744C392B53E6006DAE0A2A090769EB5CCB6D4DF99C3CD`；Git 换行规范化后按 JSON 内容核对 |
| 已提交源码基线 | `24cedfe6b6f8be84bbed4115438c3c136241be32` |
| Task 分支 | `feature/task1-quote-evidence-lens`；PLAN/CODE/TESTS/LEARNING 继续使用同一短期分支 |
| 工作树 | 当前 Windows 仓库根目录；所有版本文件使用仓库相对路径，不写个人绝对路径 |
| 开始时 Git 状态 | 15 个既存 tracked 修改、1 个既存 untracked 兼容入口；暂存区为空。本轮不是从干净工作树开始 |
| 源码事实 | `shared` 只有 `Task1ShellPage` 及空壳合同测试；`KuiklyChart` 为保留空 module；Android/H5 是最小宿主 |
| 本轮实测 | PowerShell 核验两组 20 日 fixture：日期唯一有序、OHLC 合法、6 条证据引用与算例结果一致 |
| 本轮未验证 | 未执行 Gradle 构建、业务单测、浏览器交互或 Android 设备运行；未安装依赖；既存空壳证据不计作新业务通过 |

CODE 启动时在唯一实施记录中补充实际 `git rev-parse HEAD`，该值必须包含用户确认的本计划版本；另列尚存工作树差异。不得只引用原始源码 SHA 而遗漏计划提交或未提交前置条件。已有流程文档差异不是 CODE 成果，不自动全仓 `git add`。

## 2. 范围与创新取舍

### 2.1 题面 Must 与用户选择的扩展

| 编号 | 性质 | 必须得到的结果 | 实现落点 | 验收 ID |
|---|---|---|---|---|
| M01 | 题面 | 列表显示名称、代码、最新价、涨跌额、涨跌幅 | `market/`、`ui/FinanceHomePage.kt` | F01 |
| M02 | 题面 | 列表真实滚动，每项进入正确股票详情 | `navigation/`、首页与详情 | F02–F03 |
| M03 | 题面 | 详情显示名称、代码、最新价、涨跌幅、最高、最低、成交量 | `market/`、`ui/FinanceDetailPage.kt` | F04 |
| M04 | 题面 | 详情有可见的 AI 分析与解读 | `insight/QuoteEvidenceLens.kt` | F05、I01–I06 |
| X01 | v3 核心 | 固定 20 日 K 线＋成交量，同一时间轴、独立纵轴 | `insight/MarketPlotLayout.kt`、`MarketPlotView.kt` | P01–P05 |
| X02 | v3 核心 | 证据定位双图；点图检视日值；已有证据反查 | `EvidenceResolver.kt`、`LensState.kt`、Module UI | I01–I04 |
| X03 | v3 核心 | 不可变缺量快照改变比较可用性与解读，价格仍可核对 | Mock Provider、Resolver、详情 | I05、Q01 |
| Q01 | 工程合同 | loading / empty / failed / retry / unknown、时效、单位、非颜色选择语义 | 页面状态、格式化、Module UI | F06、Q01–Q05 |

计划确认后，X01–X03 是本 Task 的核心验收项。它们来自 v3 选择，不是 Issue #1477 或原题新增要求；不能实现一张静态摘要卡就宣称本计划完成。

### 2.2 候选比较及当前决定

此表保留选择依据，不重启本轮已经明确的方案方向。

| 候选 | 用户价值 / 20 秒可见效果 | Module / AI 载体 | 工程与 AI 投入 | 成本与风险 | 删除代价 / 结论 |
|---|---|---|---|---|---|
| A：最初单向证据定位 | 点击依据后定位价格走势，读取事实 | QuoteEvidenceLens / 证据按钮＋走势 | 校验和单向定位；复用较简单 | 较低；股票展示与反向检视不足 | 丢失 K 线与反查；保留作范围重议备选 |
| B：v3 双向联动卡 | 点击回落 → 双图标区间 → 点一天 → 返回完整依据 | QuoteEvidenceLens / K 线＋量柱＋互斥焦点＋解释 | 事实、关联、坐标、选择、渲染集中，A/B 共用 | 中等偏上；点选、滚动协调及平台差异 | 当前采用的规划方向；不自行降级 |
| C：行情摘要信息卡 | 展开支持、限制与时效，读取结构化摘要 | MarketInsight / 分层摘要卡 | 状态和摘要组织，弱化图形核对 | 较低；联动深度有限 | 丢失 v3 核心；仅严重阻塞后供用户重议 |

**首版保留：**12 个虚构行情条目；完整详情；固定 20 日 OHLCV；总体/局部/量能三类证据；双向点按；十字光标；缺量数据切换；一致性与异常恢复；A/B 数据变体共用同一实现。

**非目标及优先删除项：**拖动检视、缩放、平移、多周期、指标、复杂动画、跨标的比较、双快照复盘、真实行情/模型、登录、云服务、额外平台、通用图表 DSL。Bonus 本轮不申领。Task 2 的输入、Markdown、消息 Provider 和正式会话 UI 不在本轮 CODE 允许范围内。

## 3. 40/25/25/10 评分与证据合同

权重表示正式评分上限和投入方向，不预测实际得分。前三项计划覆盖该维度要求；没有运行证据不申报已得分。

| 维度 / 上限 | 计划目标 | 评审可见结果 | 实现位置 | TESTS 直接证据 / SUBMIT 呈现 | 状态 / 删减线 |
|---|---|---|---|---|---|
| R-FUNC / 40 | 覆盖全部题面 Must 与恢复路径 | 12 项列表滚动、正确详情、完整字段和 AI | market、navigation、ui | F01–F06；完整路径录像、字段表；README 启动与操作说明 | PLANNED；不得删主链路 |
| R-ENG / 25 | 聚焦 Interface、集中纯逻辑、状态隔离、可证实复用 | A/B 同一 Module；两实例互不影响；输入错误有定义 | market、insight、commonTest | 单测、P01–P05、Q01–Q05；Interface 说明与实际调用图 | PLANNED；T1 只声明数据变体/场景复用 |
| R-AI / 25 | 结论、理由、限制、时效与双向核对都可操作 | −6.09% 回落定位、日检视、证据回查、缺量失效 | Resolver、LensState、Lens UI | I01–I06；三类证据与缺量恢复录像、计算口径说明 | PLANNED；双向交互是核心 |
| R-BONUS / 10 | `0 / NOT PLANNED` | 暂不增加真实 API 或平台 | 无 | 不使用 APK 构建替代额外平台运行，不重复计核心交互 | NOT_STARTED；首先删除全部额外增强 |

## 4. 页面、数据与恢复合同

### 4.1 首页和详情

- 首页包含 `MOCK_A`、`MOCK_B` 与 `MOCK_C` 至 `MOCK_L`，共 12 个有承接的虚构条目；其余 10 项按固定规则生成 20 日合法数据、证据和摘要，禁止运行时随机数。每项最新字段来自该实体展示快照的最后一天。
- 固定涨跌语义：列表/详情当日涨跌相对上一交易日收盘；K 线实体颜色按 close 与 open；UI 分别标“当日”和“区间”，图例说明 K 线口径。涨跌同时有正负号和文字。
- 详情从上到下：返回/实体 → 最新价与当日变化 → 最高/最低/成交量 → 截止时间与窗口 → Mock AI 摘要 → 证据按钮 → K 线 → 成交量 → 当前事实/关联证据/限制 → 演示数据选择 → 免责声明。
- 单列排版，证据按钮换行；事实区在图下固定位置，不用遮住 K 线的大气泡。图形不响应页面外部坐标，不自动滚动到其他区域。
- 首页开出的详情使用默认完整快照。详情“完整 / 量能缺失”仅切换该详情的文档；返回列表仍显示列表原完整快照，不把局部演示选择写回 Provider 全局状态。
- 统一展示“历史 Mock”“AI 解读（Mock）”“仅作技术演示，不构成投资建议”；没有真实模型调用，不宣称预测有效或算法首创。

### 4.2 行情与事实

`MarketSnapshot` 包含 `entityId / snapshotId / name / asOf / source / currency / priceScale / bars`。Bar 使用 ISO 日期、整数分 `openMinor / highMinor / lowMinor / closeMinor`，以及可空的 `volumeShares`。首版固定 CNY、scale=100；量以股保存。只把已校验的不可变集合交给下游。

| 事实 | 计算规则 / 样例 |
|---|---|
| 当日价格变化 | `close[last] - close[previous]`；百分比除以前收。窗口首日没有前收时显示不可计算，不能用开盘代替 |
| 区间变化 | `(endClose / startClose - 1) * 100%`；端点有序且均存在，基数必须大于 0；不称为最大回撤 |
| 量能倍数 | 目标日 volume / 此前连续 5 个窗口交易日平均量；恰好 5 个且不含目标日，不能跳过缺量样本拼凑 5 个有效值 |
| 金额/百分比显示 | 价格、百分比与倍数保留两位小数；末尾显示时四舍五入、半值远离零，消除负零；中间计算不先取整，不依赖系统 locale |
| A | 区间 +12.00%；08-24 至 08-27：11.50 → 10.80，−6.09%；180 万股 / 120 万股 = 1.50 倍 |
| B | 区间 −4.00%；08-27 至 09-04：9.10 → 9.60，+5.49%；80 万股 / 100 万股 = 0.80 倍 |

日期唯一递增、OHLC 正数且 `low <= min(open, close) <= max(open, close) <= high`；volume 为 null 或非负数。非法 OHLC、重复日期、非有限/不支持的价格输入让该行情文档失败，禁止悄悄排序、丢行或伪造正常图形。缺量只影响关联比较。空 bars 进入 Empty。

解读使用有限模板与结构化事实组合。设计 JSON 的 `expectedPercent / expectedRatio` 仅是测试预期；业务显示必须计算。Evidence ID 在文档内唯一，summary、证据与 snapshot 绑定；不能从自然语言猜引用日期。重复 evidenceId 让该证据集合无效，行情仍可检视。

时效采用可注入时钟：固定展示截止时间与窗口；“历史 Mock”始终可见。演示陈旧阈值定为 `now - asOf > 72 小时`，明确它是演示策略，不是交易所实时性标准；未来时间单独标记异常。阈值内也不显示“实时”。在边界等于阈值、超过阈值和未来时间处测试。

完整/缺量版本使用不同 snapshotId；缺量版本将目标日前的一个比较日（A/B 统一为 09-02）成交量置 null，保留所有 OHLC 与其余量值。E3 显示不可用及缺失日期，摘要不得继续输出旧量能倍数；E1/E2 不变。不得修改原 snapshot，切回完整版本重新计算。

### 4.3 外层状态与质量提示

外层 `Loading / Ready / Empty / Failed / UnknownEntity / SnapshotUnavailable` 与内层质量提示分离。Mock Provider 用显式演示场景控制成功、空、首次失败后重试成功、已知实体无解读；不靠网络/随机延时触发。

| 情况 | 可见反馈 | 恢复与失败判据 |
|---|---|---|
| Loading | 明确等待，导航仍可返回 | 离开页面/新请求后旧结果不得回写；失败不能无限 loading |
| Empty | 暂无演示行情，无伪造图 | 返回或重新加载同一上下文 |
| Failed | 加载失败与重试 | 重试保留实体/快照；不换默认股票；一次操作一次结果 |
| UnknownEntity | 无法识别该股票 | 返回列表；绝不回退 A |
| SnapshotUnavailable | 原快照不可用 | 保留实体与原快照说明，可返回；不静默换最新快照 |
| 已知实体无解读 | 保留基础行情与图，显示暂无解读 | Overview 可检视原始数据，不假装未知股票 |
| 无效引用/缺量 | 该依据暂不可用＋原因，按钮禁用 | 清理对应图形标记；可选择其他有效依据 |
| 长文/窄屏 | 完整可读，按钮换行、字段不遮挡 | 320/390 CSS px（H5）与实测 Android 宽度分开记证据 |

## 5. QuoteEvidenceLens 的 Interface 与状态

一个业务包作为 Module，首版不新增 Gradle module。Interface 封装校验、事实、关联、坐标、选择与渲染；caller 仅管理载入、焦点状态和导航事件。新增包及来源见 [ADR-013](../decisions/ADR-013-task1-evidence-lens.md)。

### 5.1 Interface 草案（业务合同，不是假定 Kuikly API）

```text
EvidenceDocument = snapshot + summaryModel + evidence[]
DocumentKey = entityId + snapshotId
LensUiState = documentKey + focus
Focus = Overview | EvidenceFocus(evidenceId) | DayInspect(date)

输入：EvidenceDocument、LensUiState、Detail 展示密度、可用宽度、时效上下文
动作：SelectEvidence(id) | InspectDay(date) | ReturnToSummary
输出：校验后的新 LensUiState、可渲染事实/高亮，以及有上下文的用户事件
导航事件合同：OpenDetail(entityId, snapshotId, focus)
```

`resolve(document, timeContext)` 输出有效事实与质量问题；`reduce(resolved, state, action)` 输出规范化状态；布局和 UI 只消费这些结果。它们是同一 Module 的小型调用面，不要求 caller 接触 Canvas、日槽计算或格式化规则。不建立只有一个实现却无实际替换需求的通用 Provider 框架。

Task 1 只实现 Detail 密度；消息密度留待 Task 2 真正接入后冻结，不制造另一张复制卡片。Task 1 在纯逻辑中测试同文档两实例隔离，但不把测试容器称为正式聊天接入。

### 5.2 焦点转换

| 输入/动作 | 下一状态 | 必须同步发生的变化 |
|---|---|---|
| 首次载入完整文档 | 第一条有效 EvidenceFocus；无有效证据则 Overview | 图、说明、按钮同源 |
| 选择有效证据 | EvidenceFocus(id) | 清掉单日十字光标，显示完整证据范围和事实 |
| 点图内有效日槽 | DayInspect(date) | 清掉旧区间主标记，双图日期光标同步，价格横线在所选日收盘 |
| 点击关联证据入口 | EvidenceFocus(id) | 恢复完整端点/区间口径，不将区间变化写成所点日涨跌 |
| 返回解读 | 第一条有效 EvidenceFocus 或 Overview | 不保留旧的单日事实 |
| 文档键改变 | 按新文档重新初始化 | 不继承旧 evidenceId/date；同名 E1 也不能跨实体继承 |
| 同文档容器宽度变化 | 语义 focus 不变 | 只重算坐标；丢弃旧像素位置 |
| 无效动作/文档键不匹配 | 忽略旧事件；先规范化当前状态 | 不定位临近日期伪装命中；失效焦点回 Overview 并给原因 |

同 documentKey 代表同一不可变内容。新请求的响应按请求标识与文档键裁决；较晚返回的旧响应不得覆盖新页面。实例状态由各 caller 持有，不使用全局可变选中项。

日期关联按显式引用查找：目标日优先，其次局部区间/端点，再次量能比较样本，最后整体窗口；同优先级保持原文档顺序并去重。标签使用“目标日 / 区间内 / 比较样本 / 整体观察”。整体窗口证据的端点也归整体观察，不假装独立日解释。仅有整体证据时显示“没有针对该日的单独解读”；完全无有效关联也能看原始日值。用户自行选入某条证据，不随机替用户选择。

### 5.3 双图、命中与滚动规则

- 两图使用相同 `plotLeft / plotWidth / N=20`；第 i 日中心为 `plotLeft + (i + 0.5) * plotWidth / N`。横坐标只由日期槽决定，不按周末间隔插空槽。
- 价格纵轴用完整窗口 low/high 及固定留白，成交量用完整可用量值与零基线；切证据不重新缩放。价格平线扩展至少 1 分的纵轴范围；全零量用非零坐标分母但数值仍显示 0；全缺量显示缺量状态。
- 区间焦点在两图标同一闭区间。量能焦点区分目标日与此前五日。DayInspect 只有价格区画收盘横线，成交量只共享日期竖线与所选柱强调。
- 命中使用图内日槽，范围左闭右开；内部边界归右槽；最后右边界及图外不命中。轴标签区/两图间隙不命中。0 或不可用宽度不生成命中区域。蜡烛空隙仍可选对应日槽。
- 量柱缺失不画成 0；使用缺量标记，日槽仍可检视。K 线涨跌/平盘和当前焦点均有形状、边框或文字辅助，不能只靠红绿区别。
- 点按手势从 down 到 up 期间超过 8 个逻辑像素的移动、容器 scroll、cancel 或多指即取消；用最大位移判定，移走又回原位仍不算点按。按下不提交选择，合法抬起只产生一次 InspectDay。首版不捕获拖动、不阻断父级纵向滚动。
- 8 逻辑像素为初始阈值；探针验证框架事件单位及是否已有滚动取消语义。允许根据两个平台实测微调阈值并记录，不改变“滚动不误选”的合同。命中纯测试无法替代真实触摸/滚动验证。

### 5.4 路由与未来复用

保留唯一入口 Page 名 `finance_home`。首版在同一 Pager 内用显式路由状态 `Home | Detail(entityId, snapshotId?, focus?)` 切换内容，避免依赖尚未验证的跨端页面 push API；`snapshotId` 为空仅代表来自列表的首次选择，显式指定而不存在时进入 SnapshotUnavailable。

首页滚动位置保存在首页 caller。进入详情前记录；返回恢复原列表位置。返回顺序：详情先回首页，首页再交给宿主默认返回行为。H5 浏览器后退与 Android 返回键由最小宿主适配把动作传给同一路由状态，具体 Kuikly 桥接方法须在探针核对版本源码和运行。H5 history 不保存整份行情文档或像素坐标；刷新后允许重新加载 Mock，上次进程的会话恢复不在 Task 1 要求内。

来自未来消息的明确 focus 经验证后恢复其类型；坏 focus 回 Overview 并提示，不悄悄选 E1。详情中的选择仅影响详情实例。Task 2 的消息 ID、草稿、滚动与会话恢复由 Task 2 caller 管理；本轮仅冻结这些输入输出约束，不提前写聊天代码。

## 6. 文件、依赖及允许路径

下表包路径均以 `shared/src/commonMain/kotlin/io/github/study0915/kuiklyfinance/` 为根。逻辑测试对应放入 `shared/src/commonTest/kotlin/io/github/study0915/kuiklyfinance/`。

| 文件/目录 | 职责 | 实施步 |
|---|---|---|
| `market/MarketModels.kt` | 不可变实体、快照、Bar、载入结果 | C2 |
| `market/MockMarketProvider.kt`、`MockMarketFixtures.kt` | 12 实体、A/B、缺量及故障场景、按快照查找 | C2 |
| `market/MarketFormatter.kt` | 整数价格、比例、股数、日期与时效文字 | C2 |
| `insight/EvidenceModels.kt`、`EvidenceResolver.kt` | 引用/事实/质量、解读模板、日期关联 | C2 |
| `insight/LensState.kt` | 互斥焦点与规范化 reducer | C1→C2 |
| `insight/MarketPlotLayout.kt` | 双图几何、日槽命中、标记描述 | C1 |
| `insight/MarketPlotView.kt` | Kuikly 绘制和局部点按事件；不管理路由 | C1→C4 |
| `insight/QuoteEvidenceLens.kt` | Module UI 入口、事实与关联解释 | C4 |
| `navigation/FinanceRoute.kt`、`ui/FinancePageState.kt` | 路由校验、请求身份、页面状态与返回位置 | C1→C3 |
| `ui/FinanceHomePage.kt` | `finance_home` 唯一 Pager 注册和首页 | C3 |
| `ui/FinanceDetailPage.kt` | 内部详情 ViewBuilder/内容，不重复注册 Page | C3→C4 |
| `ui/Task1ShellPage.kt` | C1 暂接诊断内容；C3 替换并删除空壳，只保留一个入口 | C1→C3 |
| `ui/Task1Routes.kt` | 从空壳提取入口常量与免责声明合同 | C3 |
| `shared/src/commonTest/…/market,insight,navigation,ui/` | 计算、状态、命中、Provider 与路由行为；替换空壳专用断言 | C1–C4 |
| `androidApp/…/MainActivity.kt`、`h5App/…/Main.kt` | 必需的生命周期、返回、宽度适配，不放业务事实 | C1→C3 |
| `scripts/use-cli-env.ps1`、`bootstrap-cli.ps1`、`doctor.ps1`、`verify.ps1`、`run-h5.ps1` | 工作区工具环境、检查/启动与证据入口的必要修正 | C0/C5 |

允许的构建修改仅限根/`shared`/Android/H5 Gradle、`gradle.properties`、版本目录、`h5App/package*.json` 的隔离、测试或宿主必需配置；首版不升级现有依赖、不引入图表库。若确需新库，先在本计划和 ADR 记录版本、官方来源、许可、安装目录和不可由现有工具解决的原因，再决定是否属于范围变更。

允许的文档：本计划、workboard、ARCHITECTURE、对应 readiness、ADR、必要 README、唯一 CODE/TESTS/LEARNING 报告与证据目录；不新增 Feature 任务卡。必要构建产物进入被忽略目录。

禁止：`archive/task1-v1/**`、`KuiklyChart/**` 的功能开发或迁移；Task 2 `chat/**`；WSL 代码/SDK/缓存；凭据、签名、真实用户数据、账户、个人绝对路径与本机配置进入 Git。目录变化已在此声明，CODE 不静默重构。

### 6.1 工具隔离合同

工具与依赖统一位于仓库 `.cache/`；复用已有目录下 JDK 17、Node、Android SDK。现有仓库设置 Kuikly 2.4.0 / 制品 2.4.0-2.0.21、Kotlin 2.0.21、Gradle Wrapper 8.0、AGP 7.4.2。当前仅核对了配置和目录/文件存在，不表示这些版本本轮运行通过。

| 类型 | 工作区目标 / 规则 |
|---|---|
| Java / Gradle | `.cache/jdk17`、`.cache/gradle`、`.cache/user-home`；使用 wrapper，带 `-Duser.home`，不全局安装 Gradle |
| Node / npm / Yarn | `.cache/node`、`.cache/npm`；prefix/corepack/yarn cache 均局部；禁止全局 npm 安装 |
| Android | `.cache/android-sdk`、`.cache/android`；AVD、模拟器、用户配置与密钥生成目录须局部验证后才启动 |
| 临时文件 | `.cache/tmp`；子进程 `TEMP/TMP` 与 Java `java.io.tmpdir` 均指向它 |
| 浏览器测试 | 优先工作区便携运行时；新装浏览器/驱动/测试库与 profile 均置 `.cache/`，只绑本机地址 |
| Python（若后续确需） | 工作区便携解释器＋`.venv`，pip cache 在 `.cache/pip`；不激活/修改 conda base、不全局 pip 安装；本轮不需要 Python |

所有环境变量只改当前任务子进程，不用 `setx`、机器/用户持久环境或系统 PATH。CODE C0 先补齐现有脚本未显式约束的 TEMP/TMP、npm prefix 和测试 profile，不承诺现有脚本已经完全隔离。

现有 doctor 已注明 Windows ADB 可能访问用户配置目录；在确认 ADB server、用户目录和新建配置实际落点前不启动它。可先完成 H5 与 APK 构建，Android 运行仍为 UNVERIFIED。若约束下无法验证，记录命令与原因；不得因等待超过某时长便视为用户允许写 C 盘。此约束限定本任务工具的安装、依赖、缓存、配置和临时输出，不宣称能阻止 Windows/既有应用自身所有系统写入。

本地另有上游源码缓存，当前 HEAD 为 `19e4a7212fcd8bbc9d6b760f800f2c4aa78b8e03`，未匹配精确 tag；不能直接当成 2.4.0 API 依据。C1 优先从锁定制品的 source JAR 或精确 tag 核对 Canvas、布局、触摸取消和返回桥接；保存版本/符号证据。无需为此运行上游工程或升级主仓库。

## 7. 有序实施、技术探针与聚焦 commit

以下均为计划，尚未执行 CODE。每步代码与其行为测试同批验证；通过后记录 commit 与退出证据。失败留在本步，不以继续堆页面掩盖。

| 步骤 | 内容与执行顺序 | 退出条件 / 失败处理 | 建议聚焦 commit |
|---|---|---|---|
| C0 | 固定确认版本、实际 HEAD/差异；审计并补齐工作区工具隔离，再运行 bootstrap/doctor 与空壳构建基线 | 工具路径均局部、版本日志明确、无 base/global 安装；不能确认写入位置则不启动相应工具 | `chore(env): confine task1 tooling to workspace`（仅实际修改时） |
| C1 | 先做 20 日静态双图 → 双图日槽点选 → Evidence/Day 焦点切换 → 宽度变化与滚动取消 → 返回桥接探针 | P01–P05 在 H5 实测；Android 编译/运行分别记录。出现核心行为失败先修复；无法局部解决才回 PLAN | `feat(task1): verify paired chart interaction` |
| C2 | 将 A/B 转为业务 fixture、补 10 个确定性样例与异常；实现校验、事实、时效、关联和 reducer | 数值/缺失/非法引用/过期/跨文档/实例隔离等行为测试通过，业务不依赖设计 expected 值 | `feat(task1): add deterministic evidence model` |
| C3 | 首页 12 项真实滚动、详情完整字段、路由/返回、载入与恢复；替换空壳注册 | F01–F04、F06 在 H5 通过；无装饰死链接，无旧请求覆写 | `feat(task1): complete quote browsing flow` |
| C4 | 联动卡接入详情；三类证据、日检视与反查、缺量切换、边界说明；A/B 同一实现 | F05、I01–I06、Q01–Q05；在单列窄屏连续操作，模块不依赖详情类 | `feat(task1): connect quote evidence lens` |
| C5 | 完整 TESTS、声明—证据矩阵、录像；失败回同一 Task CODE；通过后撰写 LEARNING | 必需构建与 H5 全通过；Android 运行状态明确；T1-LEARNING 有可复述报告才启动 T2 PLAN | `test(task1): verify evidence interactions`；`docs(task1): record validation and learning` |

C1 先用最少量 fixture 切片验证风险，C2 归并为正式数据，探针不留下复制的业务实现。可临时使用同 Pager 内纵向列表与两张独立卡验证父容器滚动/实例隔离；它只是技术夹具，C4 后移除诊断入口，不能声称 Task 2 聊天滚动已验证。

回滚采用本分支上的修复提交或聚焦 `git revert <本轮具体提交>`，保留成功部分和用户既有修改；不使用 `reset --hard`、`clean` 或覆盖整棵工作树。C1 绘制、点选或滚动核心失败时记录复现、平台、源码/事件证据及局部修复结果；必要时用户重议删减线，不能自动换为 A/C 并称 B 完成。

## 8. TESTS：命令与可逐项验收矩阵

### 8.1 启动和命令

以下从仓库根执行，须先完成 C0 的路径审计。首次工具准备运行 `scripts/bootstrap-cli.ps1`；已有工具时它执行存在/包检查，不为计划本身下载安装。命令入口已有，业务预期均待 CODE 实测。

```powershell
.\scripts\bootstrap-cli.ps1
.\scripts\doctor.ps1
. .\scripts\use-cli-env.ps1
.\gradlew.bat :shared:testDebugUnitTest @KuiklyGradleArgs
.\gradlew.bat :shared:compileKotlinJs :h5App:jsBrowserProductionWebpack @KuiklyGradleArgs
.\gradlew.bat :androidApp:assembleDebug @KuiklyGradleArgs
# 上述分项用于定位；最终一次全量验收使用仓库统一入口
.\scripts\verify.ps1
# 单独会话运行 H5 开发服务器；记录实际监听 URL 和端口
.\scripts\run-h5.ps1
```

构建命令不互相并行抢 Gradle 状态。C5 可用 `verify.ps1` 一次覆盖构建集合，不无理由反复全量执行。`KuiklyChart:jsNodeTest` 的空 module SKIPPED 不代表 shared 单测通过；必须检查 shared 实际执行的测试数与报告。共同逻辑由 `:shared:testDebugUnitTest` 执行；JS 的编译/浏览器行为单独覆盖跨目标风险。

检查单测 XML/HTML、H5 输出 JS/HTML 和 APK 文件存在、大小、SHA-256；不能只记录 exit 0。预期输出分别在 `shared/build/test-results/`、`h5App/build/`、`androidApp/build/outputs/apk/`，以实际任务产物路径为准，日志不得未经脱敏进入公开文档。

### 8.2 交互、边界与失败判据

| ID | 场景 / 操作 | 预期结果与失败判据 | 层级 / 直接证据 |
|---|---|---|---|
| F01 | 检查并滚动全部 12 项 | 名称、代码、价、额、幅完整；最后一项实际可到达；缺字段失败 | Provider 单测＋H5 录像 |
| F02 | 依次点击 A–L | 12 个实体都进入自身详情且有确定性解读；别名/默认 A 失败 | 路由单测＋H5 操作记录 |
| F03 | 从滚动后的列表进详情再返回；H5 后退与 Android 返回分别检查 | 恢复原列表位置；详情返回不直接退出应用；设备未执行必须标明 | H5 录像＋Android 独立记录 |
| F04 | A/B 详情字段核对 | A 收盘 11.20、最高 11.26、最低 10.99、180 万股、当日 +0.15/+1.36%；B 收盘 9.60、最高 9.66、最低 9.49、80 万股、当日 +0.05/+0.52% | 事实单测＋字段截图 |
| F05 | 首次载入详情 | AI Mock 摘要、证据、限制、时效和免责声明同时可访问 | H5 截图/录像 |
| F06 | empty / 首次失败后重试 / unknown / 指定快照不存在 / 已知实体无解读 | 各自状态与恢复正确；不伪造卡片，不默认切实体；离开后旧响应不回写 | 状态单测＋H5 异常操作 |
| P01 | 320、390 CSS px 布局；中途变宽再变窄 | 两图 20 日期中心相同；保持语义选择；关键字段可读；无超界 NaN | 布局单测＋两宽截图 |
| P02 | 点两图相同日槽的首/中/末日及内部边界、柱间隙、轴区、图外 | 同日同焦点；右边界不命中；区外不改选择；不能按价格/量 y 值选日期 | 命中单测＋H5 操作 |
| P03 | 从已选区间点一天，再从关联证据返回 | EvidenceFocus 与 DayInspect 互斥；收盘横线仅在价格区；三处事实一致 | reducer 测试＋录像 |
| P04 | 图上按下后纵向滚动、移出再移回、多指/cancel、快速点选 | 滚动顺畅，取消动作不在 up 误选；一次有效点按一次事件 | 手势逻辑测试＋真实触控运行证据 |
| P05 | 父列表中两张技术夹具卡轮流选择与滚动 | 状态独立，父滚动不被吞；仅证明夹具，正式聊天留 T2 | 双实例测试＋H5；Android 单列状态 |
| I01 | 点击总体、局部、量能三类证据 | 两图范围/目标/基线各自变化，窗口与轴范围不变，说明使用同文档事实 | 关联/事实单测＋三态截图 |
| I02 | A 中途回落 → 点 08-25 → 相关证据回区间 | −6.09% 对应完整 08-24 至 08-27；08-25 单日检视不能冒充 −6.09% 单日跌幅 | 单测＋连续录像 |
| I03 | 点击量能目标日、比较样本日 | 关联按目标/区间/样本/整体排序；标明关联角色，多条由用户选择 | 单测＋H5 |
| I04 | 点仅有整体关联的日期，以及无有效证据日期 | 日值保留；明确无单日解读；不生成新的解释 | 单测＋H5 |
| I05 | 完整 → 缺量 → 完整 | 缺量标记、E3 不可用及日期原因、无旧倍数；E1/E2 有效；原快照未突变 | 不可变性/事实测试＋录像 |
| I06 | A/B 切换、相同 E1 ID、同快照两实例、非法恢复 focus | 不串实体/快照/实例；非法 focus 提示并 Overview；不同证据事实不靠 UI 分支硬编码 | 状态/路由测试＋H5 |
| Q01 | 平价、全零量、部分/全缺量、零比较均值、不足 5 日 | 坐标有限、0 与 null 不混淆；样本不足不计算倍数；价格可用时保留 | 参数化纯逻辑测试＋代表场景截图 |
| Q02 | 重复/无序日期、非法 OHLC、重复证据 ID、无效端点/目标日 | 价格无效不画伪 K 线；证据无效不画高亮；所有原因可解释 | 校验测试＋H5 代表错误 |
| Q03 | 注入时钟：72h 边界、超过、未来时间 | 历史 Mock 始终可见；陈旧/异常提示准确，无“实时”伪称 | 时效单测＋截图 |
| Q04 | 长证据、按钮换行、两宽、颜色辨识受限 | 选择另有文字/边框；关键数值不截断；图例与单位清楚 | H5 视觉检查；Android 独立检查 |
| Q05 | 全链路控制台/网络/日志检查 | 无未处理异常/意外外部服务请求；Mock 标签清楚；日志可脱敏 | 浏览器记录＋diff 审查 |

真实触控可使用工作区浏览器测试运行时的触摸输入；仅鼠标 click 或纯 reducer 测试不算 P04 完整通过。H5 平台证据至少包含两个宽度、鼠标与触摸取消。Android P/F/I 必须在实际设备或模拟器运行后才记通过。

### 8.3 阶段退出与平台限制

- T1-CODE：C0–C4 清单和实施记录完整；业务及相关测试已实现，本地失败已修复或具体记录，才交 TESTS。
- T1-TESTS：shared 实际单测、Kotlin/JS、production bundle、Android Debug 构建及 H5 必需交互通过；声明—证据一一对应。
- Android 运行若因工作区隔离或设备可用性尚不能完成，可按 readiness 单独记录 UNVERIFIED 及解除条件；仅在没有已知 Android 核心失败且不把该平台申领为运行通过时，才可裁决受限 TESTS VERIFIED。若运行发现双图/点选/滚动失败，必须回 CODE 修复，不能用“未验证”隐藏失败。
- iOS/鸿蒙不执行、不宣称支持已验证；Task 2 会话滚动/Markdown/返回与第二正式 caller 都留待 T2 独立验收。

## 9. 唯一记录、证据与学习交付

| 产物 | 落点 / 内容 |
|---|---|
| CODE 实施记录 | `docs/handoffs/TASK1-CODE.md`；沿用现有记录目录，Owner Codex，无外部 handoff；记录确认版本、实际 HEAD、基线差异、步骤/文件/commit/命令/风险 |
| TESTS 报告 | `docs/reviews/TASK1-TESTS.md`；F/P/I/Q 场景、构建、浏览器、APK、设备分别记录，含评分声明—证据表 |
| 证据索引 | `docs/evidence/task1/README.md`；保存可公开截图/精简日志、日期、构建 SHA 和操作；大录屏/原始日志先放 `.cache/task1-evidence/`，索引记录相对位置与 hash |
| Interface 说明 | `docs/interfaces/QuoteEvidenceLens.md`；在 CODE 实现后给出实际类型、一个真实 caller 与 A/B 数据变体例子，不伪造 Task 2 接入 |
| LEARNING | `docs/learning/TASK1-LEARNING.md`；TESTS 裁决后编写，含 30 秒介绍、职责、体验链、3–5 难点、2–3 简历条目、8–12 问答和局限 |
| SUBMIT 预留 | README 讲清启动、列表/详情/联动、目录、Mock 和平台；两题 LEARNING 完成后才生成本地统一候选包 |

学习重点：为什么是 OHLCV 与结构化证据；当日/区间涨跌区别；事实由哪里计算；互斥状态怎样避免错配；点按与父滚动怎样协调；如何证明复用；为什么不新增图表 Gradle module；缺量如何影响表达；各平台证据能证明什么。简历数字和成果只从实际 TESTS 提取，不预写“跨题复用已完成”。

Task 1 演示建议约 90 秒：0–15 秒滚动列表并进 A；15–35 秒回落依据与 −6.09%；35–55 秒单日十字光标与证据回查；55–70 秒量能完整/缺失切换；70–80 秒返回并进入 B；80–90 秒说明 Mock、时效和验证平台。额外录制失败重试与未知实体片段。完整 Task 2 联合演示待真实接入后另补。

## 10. 风险、确认与下一步

| 风险 | 先做什么 | 何时需要变更计划 |
|---|---|---|
| 锁定 Kuikly 的触摸/绘制能力不明 | C1 核对锁定源码并小范围运行，先双图再点选 | 核心双向行为无法局部实现时；不直接升级框架 |
| 窄日槽导致误选或吞滚动 | 统一局部坐标，取消阈值与宽度探针 | 需要拖动/缩放或平台专用业务实现时 |
| 不可变快照被 UI 状态污染 | documentKey、纯 reducer、按 caller 保存状态 | 需要引入持久化/实时数据时 |
| 先规划复用但暂无聊天 caller | T1 只证明 A/B 与隔离；T2 再冻结消息密度 | T2 实际消费要求改变公开合同，回写计划/ADR |
| 环境工具默认写 C 盘 | C0 重定向并验证，无法局部化的工具暂不运行 | 用户约束不能满足时报告具体位置与阻塞，不擅自放宽 |
| 工作树带既存流程修改 | 逐文件保留与记录，显式暂存本轮文件 | 需要处理与本轮无关内容时，不自动清理或打包进 commit |

**本轮授权已记录：**按 v3 编写 Task 1 正式实施计划；使用 Git commit；依赖与工具安装在工作区；Codex 执行。本轮不要求再次选择已经明确的方案方向。

**完整合同已确认：**2026-09-07 用户回复“开始实施”，确认本 v1.0 的实现顺序、文件/状态/路由、20 日点按范围、测试与平台限制，并授权进入 CODE。T1-PLAN 进入 VERIFIED，T1-CODE 进入 IN_PROGRESS；不重复索取相同授权。

下一步：Codex 从 C0 工作区隔离检查开始，再执行 C1 双图绘制、点选和滚动协调探针，按 C2–C5 持续实施、验收和学习交付；不提前启动 Task 2，不推送、合并、PR、tag、发布或对外沟通。
